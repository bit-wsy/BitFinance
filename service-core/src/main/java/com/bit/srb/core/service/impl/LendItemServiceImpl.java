package com.bit.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.common.exception.Assert;
import com.bit.common.result.ResponseEnum;
import com.bit.srb.core.enums.LendItemStatusEnum;
import com.bit.srb.core.enums.LendStatusEnum;
import com.bit.srb.core.enums.TransTypeEnum;
import com.bit.srb.core.hfb.FormHelper;
import com.bit.srb.core.hfb.HfbConst;
import com.bit.srb.core.hfb.RequestHelper;
import com.bit.srb.core.mapper.LendItemMapper;
import com.bit.srb.core.mapper.LendMapper;
import com.bit.srb.core.mapper.UserAccountMapper;
import com.bit.srb.core.mapper.UserInfoMapper;
import com.bit.srb.core.pojo.bo.TransFlowBO;
import com.bit.srb.core.pojo.entity.Lend;
import com.bit.srb.core.pojo.entity.LendItem;
import com.bit.srb.core.pojo.entity.UserAccount;
import com.bit.srb.core.pojo.entity.UserInfo;
import com.bit.srb.core.pojo.vo.InvestVO;
import com.bit.srb.core.service.LendItemService;
import com.bit.srb.core.service.LendService;
import com.bit.srb.core.service.TransFlowService;
import com.bit.srb.core.service.UserAccountService;
import com.bit.srb.core.util.LendNoUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Service
@Slf4j
public class LendItemServiceImpl extends ServiceImpl<LendItemMapper, LendItem> implements LendItemService {

    @Resource
    private LendMapper lendMapper;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private LendService lendService;

    @Resource
    private TransFlowService transFlowService;

    @Resource
    private UserAccountService userAccountService;

    @Override
    public String commitInvest(InvestVO investVO) {
        // 投资状态是否是投标中
        Lend lend = lendMapper.selectById(investVO.getLendId());
        Assert.isTrue(lend.getStatus().equals(LendStatusEnum.INVEST_RUN.getStatus()), ResponseEnum.LEND_INVEST_ERROR);
        // 用户余额是否充足
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", investVO.getInvestUserId());
        UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);
        Assert.isTrue(userAccount.getAmount().doubleValue() >= Double.parseDouble(investVO.getInvestAmount()),
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);
        // 是否超卖
        Assert.isTrue(lend.getInvestAmount().doubleValue() + Double.parseDouble(investVO.getInvestAmount()) <= lend.getAmount().doubleValue(),
                ResponseEnum.LEND_FULL_SCALE_ERROR);
        // 插入lendItem
        LendItem lendItem = new LendItem();
        //
        lendItem.setLendItemNo(LendNoUtils.getLendItemNo());
        lendItem.setLendId(lend.getId());
        lendItem.setInvestUserId(investVO.getInvestUserId());
        lendItem.setInvestName(investVO.getInvestName());
        lendItem.setInvestAmount(new BigDecimal(investVO.getInvestAmount()));
        lendItem.setLendYearRate(lend.getLendYearRate());
        lendItem.setInvestTime(LocalDateTime.now());
        lendItem.setLendStartDate(lend.getLendStartDate());
        lendItem.setLendEndDate(lend.getLendEndDate());

        BigDecimal expectAmount = lendService.getInterestCount(lendItem.getInvestAmount(), lendItem.getLendYearRate(),
                lend.getPeriod(), lend.getReturnMethod());
        lendItem.setExpectAmount(expectAmount);
        lendItem.setStatus(LendItemStatusEnum.DEFAULT_STATUS.getStatus());
        lendItem.setRealAmount(new BigDecimal(0));

        baseMapper.insert(lendItem);
        // 组装表单
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);

        UserInfo borrowUserInfo = userInfoMapper.selectById(investVO.getInvestUserId());
        paramMap.put("voteBindCode", borrowUserInfo.getBindCode());


        UserInfo voteUserInfo = userInfoMapper.selectById(lend.getUserId());
        paramMap.put("benefitBindCode", voteUserInfo.getBindCode());
        paramMap.put("agentProjectName", lend.getTitle());
        paramMap.put("agentProjectCode", lend.getLendNo());

        // 这里是这笔投资的编号 lendItem编号
        paramMap.put("agentBillNo", lendItem.getLendItemNo());

        paramMap.put("voteAmt", investVO.getInvestAmount());
        paramMap.put("votePrizeAmt", "0");
        paramMap.put("voteFeeAmt", "0");
        paramMap.put("projectAmt", lend.getAmount());

        paramMap.put("notifyUrl", HfbConst.INVEST_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.INVEST_RETURN_URL);
        paramMap.put("timeStamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        return FormHelper.buildForm(HfbConst.INVEST_URL, paramMap);
    }

    @Override
    public String notify(Map<String, Object> paramMap) {
        // 幂等性判断
        String agentBillNo = (String)paramMap.get("agentBillNo");
        if(transFlowService.isTransFlowSaved(agentBillNo)){
            log.warn("幂等性判断");
            return "success";
        }else{
            // 更改用户账户
            String voteBindCode = (String) paramMap.get("voteBindCode");
            QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
            userInfoQueryWrapper.eq("bind_code", voteBindCode);
            UserInfo voteUserInfo = userInfoMapper.selectOne(userInfoQueryWrapper);
            Long voteUserId = voteUserInfo.getId();

            QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
            userAccountQueryWrapper.eq("user_id", voteUserId);
            UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);


            BigDecimal voteAmt = new BigDecimal((String)paramMap.get("voteAmt"));
            userAccount.setAmount(userAccount.getAmount().subtract(voteAmt));
            userAccount.setFreezeAmount(userAccount.getFreezeAmount().add(voteAmt));

            userAccountMapper.updateById(userAccount);

            // 更改标的
            String agentProjectCode = (String)paramMap.get("agentProjectCode");
            QueryWrapper<Lend> lendQueryWrapper = new QueryWrapper<>();
            lendQueryWrapper.eq("lend_no", agentProjectCode);
            Lend lend = lendMapper.selectOne(lendQueryWrapper);

            lend.setInvestAmount(lend.getInvestAmount().add(voteAmt));
            lend.setInvestNum(lend.getInvestNum() + 1);

            // 如果满标是否更改投标状态
//            if(lend.getAmount().equals(lend.getInvestAmount())){
//                lend.setStatus(LendStatusEnum.FINISH.getStatus());
//            }

            lendMapper.updateById(lend);

            // 更改投资状态
            QueryWrapper<LendItem> lendItemQueryWrapper = new QueryWrapper<>();
            lendItemQueryWrapper.eq("lend_item_no", agentBillNo);
            LendItem lendItem = baseMapper.selectOne(lendItemQueryWrapper);
            lendItem.setStatus(LendItemStatusEnum.PALLED.getStatus());
            baseMapper.updateById(lendItem);

            // 添加流水
            TransFlowBO transFlowBO = new TransFlowBO(
                    voteBindCode,
                    agentBillNo,
                    TransTypeEnum.INVEST_LOCK.getTransType(),
                    voteAmt,
                    "投资项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle()
            );
            transFlowService.saveTransFlowByBO(transFlowBO);
            return "success";
        }
    }

    @Override
    public List<LendItem> getLendItemByLendId(Long lendId, Integer status) {
        QueryWrapper<LendItem> lendItemQueryWrapper = new QueryWrapper<>();
        lendItemQueryWrapper
                .eq("lend_id", lendId)
                .eq("status", status);
        List<LendItem> lendItems = baseMapper.selectList(lendItemQueryWrapper);
        return lendItems;
    }

    @Override
    public List<LendItem> getAllLendItemByLendId(Long lendId) {
        QueryWrapper<LendItem> lendItemQueryWrapper = new QueryWrapper<>();
        lendItemQueryWrapper.eq("lend_id", lendId);
        return baseMapper.selectList(lendItemQueryWrapper);
    }

}
