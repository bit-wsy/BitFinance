package com.bit.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.common.exception.BusinessException;
import com.bit.srb.core.enums.LendItemStatusEnum;
import com.bit.srb.core.enums.LendStatusEnum;
import com.bit.srb.core.enums.ReturnMethodEnum;
import com.bit.srb.core.enums.TransTypeEnum;
import com.bit.srb.core.hfb.HfbConst;
import com.bit.srb.core.hfb.RequestHelper;
import com.bit.srb.core.mapper.*;
import com.bit.srb.core.pojo.bo.TransFlowBO;
import com.bit.srb.core.pojo.entity.*;
import com.bit.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.bit.srb.core.pojo.vo.BorrowerDetailVO;
import com.bit.srb.core.service.*;
import com.bit.srb.core.util.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Service
@Slf4j
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private BorrowerService borrowerService;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private TransFlowService transFlowService;

    @Resource
    @Lazy
    private LendItemService lendItemService;

    @Resource
    private LendReturnService lendReturnService;

    @Resource
    private LendItemReturnService lendItemReturnService;
    @Autowired
    private LendReturnMapper lendReturnMapper;

    @Override
    public void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo) {
        // 创建一个lend
        Lend lend = new Lend();
        lend.setUserId(borrowInfo.getUserId());
        lend.setBorrowInfoId(borrowInfo.getId());
        lend.setLendNo(LendNoUtils.getLendNo());

        lend.setTitle(borrowInfoApprovalVO.getTitle());
        lend.setAmount(borrowInfo.getAmount());
        lend.setPeriod(borrowInfo.getPeriod());

        lend.setLendYearRate(borrowInfoApprovalVO.getLendYearRate().divide(new BigDecimal(100)));
        lend.setServiceRate(borrowInfoApprovalVO.getServiceRate().divide(new BigDecimal(100)));

        lend.setReturnMethod(borrowInfo.getReturnMethod());

        lend.setLowestAmount(new BigDecimal(100));
        lend.setInvestAmount(new BigDecimal(0));
        lend.setInvestNum(0); // 已投资的人数

        lend.setPublishDate(LocalDateTime.now());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parse = LocalDate.parse(borrowInfoApprovalVO.getLendStartDate(), dateTimeFormatter);
        lend.setLendStartDate(parse);
        lend.setLendEndDate(parse.plusMonths(borrowInfo.getPeriod()));

        lend.setLendInfo(borrowInfoApprovalVO.getLendInfo());

        // 平台收益 = 标的金额 * 月年化 * 期数
        BigDecimal expectAmount = borrowInfo.getAmount()
                .multiply(borrowInfoApprovalVO.getServiceRate()
                .divide(BigDecimal.valueOf(12),8,BigDecimal.ROUND_DOWN))
                .multiply(BigDecimal.valueOf(borrowInfo.getPeriod()));

        lend.setExpectAmount(expectAmount);
        lend.setRealAmount(new BigDecimal(0));

        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus());
        lend.setCheckTime(LocalDateTime.now());
        lend.setCheckAdminId(1L);

        baseMapper.insert(lend);
    }

    @Override
    public List<Lend> selectList() {
        List<Lend> lends = baseMapper.selectList(null);
        lends.forEach( lend -> {
            lend.getParam().put("returnMethod",
                    dictService.getNameByValueAndDictCode(lend.getReturnMethod(),"returnMethod"));
            lend.getParam().put("status", LendStatusEnum.getMsgByStatus(lend.getStatus()));
        });
        return lends;
    }

    @Override
    public Map<String, Object> getLendById(Long id) {
        Map<String, Object> lendDetail = new HashMap<>();

        Lend lend = baseMapper.selectById(id);
        lend.getParam().put("returnMethod",
                dictService.getNameByValueAndDictCode(lend.getReturnMethod(),"returnMethod"));
        lend.getParam().put("status", LendStatusEnum.getMsgByStatus(lend.getStatus()));
        lendDetail.put("lend", lend);

        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id", lend.getUserId());
        List<Borrower> borrowers = borrowerMapper.selectList(borrowerQueryWrapper);
        Borrower borrower = borrowers.get(borrowers.size() - 1);
        BorrowerDetailVO borrowerDetailVOById = borrowerService.getBorrowerDetailVOById(borrower.getId());

        lendDetail.put("borrower", borrowerDetailVOById);

        return lendDetail;
    }

    @Override
    public BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod) {
        BigDecimal interestCount = new BigDecimal(0);
        if(returnMethod.equals(ReturnMethodEnum.ONE.getMethod())){
            interestCount = Amount1Helper.getInterestCount(invest, yearRate, totalmonth);
        } else if (returnMethod.equals(ReturnMethodEnum.TWO.getMethod())){
            interestCount = Amount2Helper.getInterestCount(invest, yearRate, totalmonth);
        }else if (returnMethod.equals(ReturnMethodEnum.THREE.getMethod())){
            interestCount = Amount3Helper.getInterestCount(invest, yearRate, totalmonth);
        } else {
            interestCount = Amount4Helper.getInterestCount(invest, yearRate, totalmonth);
        }

        return interestCount;
    }

    @Override
    public void makeLoan(Long id) {
        Lend lend = baseMapper.selectById(id);
        // 调用HFB同步接口
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentProjectCode", lend.getLendNo());
        paramMap.put("agentBillNo", LendNoUtils.getLoanNo());

        BigDecimal serviceFee = lend.getServiceRate()
                .divide(new BigDecimal(12), 8, BigDecimal.ROUND_DOWN)
                .multiply(new BigDecimal(lend.getPeriod()))
                .multiply(lend.getInvestAmount());

        paramMap.put("mchFee", serviceFee);
        paramMap.put("note", "放款申请");
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));
        JSONObject result = RequestHelper.sendRequest(paramMap, HfbConst.MAKE_LOAD_URL);
        log.info("放款结果" + result.toJSONString());

        if(!result.getString("resultCode").equals("0000")){
            throw new BusinessException(result.getString("resultMsg"));
        }

        // 处理业务
        // 标的状态与收益 放款时间
        lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lend.setRealAmount(serviceFee);
        lend.setPaymentTime(LocalDateTime.now());
        baseMapper.updateById(lend);

        // 给借款账号转入金额
        Long borrowUserId = lend.getUserId();
        UserInfo borrowuserInfo = userInfoMapper.selectById(borrowUserId);
        userAccountMapper.updateAccount(
                borrowuserInfo.getBindCode(),
                new BigDecimal(result.getString("voteAmt")),
                new BigDecimal(0)
                );

        // 增加借款交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                borrowuserInfo.getBindCode(),
                //
                result.getString("agentBillNo"),
                TransTypeEnum.BORROW_BACK.getTransType(),
                new BigDecimal(result.getString("voteAmt")),
                "放款到账,项目编号: " + lend.getLendNo()
        );
        transFlowService.saveTransFlowByBO(transFlowBO);


        List<LendItem> lendItems = lendItemService.getLendItemByLendId(lend.getId(), LendItemStatusEnum.PALLED.getStatus());
        lendItems.forEach( item -> {
            UserInfo investUserInfo = userInfoMapper.selectById(item.getInvestUserId());
            // 扣除投资人资金
            userAccountMapper.updateAccount(
                    investUserInfo.getBindCode(),
                    new BigDecimal(0),
                    item.getInvestAmount().negate()
                    );
            // 增加投资人流水
            TransFlowBO investTransFlowBO = new TransFlowBO(
                    investUserInfo.getBindCode(),
                    LendNoUtils.getTransNo(), //
                    TransTypeEnum.INVEST_UNLOCK.getTransType(),
                    item.getInvestAmount(),
                    "扣款成功,项目编号: " + lend.getLendNo()
            );

            transFlowService.saveTransFlowByBO(investTransFlowBO);
        });

        // 生成借款人还款计划和出借人回款计划
        this.getReturnList(lend);
    }

    @Override
    public void getReturnList(Lend lend) {
        // 先根据标的生成对应的还款计划，但是不设置还款金额、利息等参数
        List<LendReturn> lendReturns = new ArrayList<>();
        Integer period = lend.getPeriod();
        for (int i = 1; i <= period; i++) {
            // 生成每一期的还款计划
            LendReturn lendReturn = new LendReturn();
            lendReturn.setLendId(lend.getId());
            lendReturn.setBorrowInfoId(lend.getBorrowInfoId());
            lendReturn.setReturnNo(LendNoUtils.getReturnNo());
            lendReturn.setUserId(lend.getUserId());
            lendReturn.setAmount(lend.getAmount());
            lendReturn.setBaseAmount(lend.getInvestAmount());
            lendReturn.setCurrentPeriod(i);
            lendReturn.setLendYearRate(lend.getLendYearRate());
            lendReturn.setReturnMethod(lend.getReturnMethod());

            // 其余属性
            lendReturn.setFee(new BigDecimal(0));
            lendReturn.setReturnDate(lend.getLendStartDate().plusMonths(i));
            lendReturn.setStatus(0);
            // TODO
            // 暂时硬编码为无逾期情况出现
            lendReturn.setOverdue(false);

            lendReturn.setLast(i == period);
            lendReturns.add(lendReturn);
        }
        lendReturnService.saveBatch(lendReturns);

        Map<Integer, Long> lendReturnMap = lendReturns
                .stream()
                .collect(Collectors.toMap(LendReturn::getCurrentPeriod, LendReturn::getId));

        // 获取回款计划
        // 针对每个标的下的每笔投资，生成回款计划
        List<LendItemReturn> lendItemReturnsAll = new ArrayList<>();
        List<LendItem> lendItems = lendItemService.getLendItemByLendId(lend.getId(), LendItemStatusEnum.PALLED.getStatus());
        lendItems.forEach(item -> {
            List<LendItemReturn> lendItemReturns = this.getLendItemReturn(lend, lendReturnMap, item);
            lendItemReturnsAll.addAll(lendItemReturns);
        });

        // 根据每个回款计划，再填充还款计划里的还款金额，以防误差产生的问题
        lendReturns.forEach( lendReturn -> {
            BigDecimal totalPrincipal = lendItemReturnsAll
                            .stream()
                            .filter(item -> item.getLendReturnId().equals(lendReturn.getId()))
                            .map(LendItemReturn::getPrincipal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalInterest = lendItemReturnsAll
                    .stream()
                    .filter(item -> item.getLendReturnId().equals(lendReturn.getId()))
                    .map(LendItemReturn::getInterest)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalTotal = lendItemReturnsAll
                    .stream()
                    .filter(item -> item.getLendReturnId().equals(lendReturn.getId()))
                    .map(LendItemReturn::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            lendReturn.setPrincipal(totalPrincipal);
            lendReturn.setInterest(totalInterest);
            lendReturn.setTotal(totalTotal);

            lendReturnMapper.updateById(lendReturn);
                }
        );
    }

    @Override
    public List<LendItemReturn> getLendItemReturn(Lend lend, Map<Integer, Long> lendReturnMap, LendItem lendItem) {
        List<LendItemReturn> lendItemReturns = new ArrayList<>();
        Integer period = lend.getPeriod();

        Map<Integer, BigDecimal> perMonthInterest;
        Map<Integer, BigDecimal> perMonthPrincipal;

        if(lend.getReturnMethod().equals(ReturnMethodEnum.ONE.getMethod())){
            perMonthInterest = Amount1Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), period);
            perMonthPrincipal = Amount1Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), period);
        } else if (lend.getReturnMethod().equals(ReturnMethodEnum.TWO.getMethod())){
            perMonthInterest = Amount2Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), period);
            perMonthPrincipal = Amount2Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), period);
        }else if (lend.getReturnMethod().equals(ReturnMethodEnum.THREE.getMethod())){
            perMonthInterest = Amount3Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), period);
            perMonthPrincipal = Amount3Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), period);
        } else {
            perMonthInterest = Amount4Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), period);
            perMonthPrincipal = Amount4Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), period);
        }

        for (int i = 1; i <= period; i++) {
            LendItemReturn lendItemReturn = new LendItemReturn();
            lendItemReturn.setLendReturnId(lendReturnMap.get(i));
            lendItemReturn.setLendItemId(lendItem.getId());
            lendItemReturn.setLendId(lend.getId());
            lendItemReturn.setInvestUserId(lendItem.getInvestUserId());
            lendItemReturn.setInvestAmount(lendItem.getInvestAmount());
            lendItemReturn.setCurrentPeriod(i);
            lendItemReturn.setLendYearRate(lend.getLendYearRate());
            lendItemReturn.setReturnMethod(lend.getReturnMethod());
            lendItemReturn.setFee(new BigDecimal(0));
            lendItemReturn.setReturnDate(lend.getLendStartDate().plusMonths(i));
            lendItemReturn.setOverdue(false);
            lendItemReturn.setStatus(0);

            if(!lendItemReturns.isEmpty() &&  i == period){ // 是不是最后一期

                BigDecimal principals = lendItemReturns
                        .stream()
                        .map(LendItemReturn::getPrincipal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                lendItemReturn.setPrincipal(lendItem.getInvestAmount().subtract(principals));

                BigDecimal interests = lendItemReturns
                        .stream()
                        .map(LendItemReturn::getInterest)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                lendItemReturn.setInterest(lendItem.getExpectAmount().subtract(interests));

                lendItemReturn.setTotal(lendItem.getInvestAmount().subtract(principals)
                        .add(lendItem.getExpectAmount().subtract(interests)));

            }else{
                lendItemReturn.setPrincipal(perMonthPrincipal.get(i));
                lendItemReturn.setInterest(perMonthInterest.get(i));
                lendItemReturn.setTotal(perMonthPrincipal.get(i).add(perMonthInterest.get(i)));
            }

            lendItemReturns.add(lendItemReturn);
        }
        lendItemReturnService.saveBatch(lendItemReturns);
        return lendItemReturns;
    }


}
