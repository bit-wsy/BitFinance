package com.bit.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.common.exception.Assert;
import com.bit.common.result.ResponseEnum;
import com.bit.srb.core.enums.LendStatusEnum;
import com.bit.srb.core.enums.TransTypeEnum;
import com.bit.srb.core.hfb.FormHelper;
import com.bit.srb.core.hfb.HfbConst;
import com.bit.srb.core.hfb.RequestHelper;
import com.bit.srb.core.mapper.*;
import com.bit.srb.core.pojo.bo.TransFlowBO;
import com.bit.srb.core.pojo.entity.*;
import com.bit.srb.core.service.LendItemReturnService;
import com.bit.srb.core.service.LendReturnService;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 还款记录表 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Service
@Slf4j
public class LendReturnServiceImpl extends ServiceImpl<LendReturnMapper, LendReturn> implements LendReturnService {

    @Resource
    private LendMapper lendMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private LendItemReturnService lendItemReturnService;

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private TransFlowService transFlowService;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private LendItemReturnMapper lendItemReturnMapper;

    @Resource
    private LendItemMapper lendItemMapper;

    @Override
    public List<LendReturn> getLendReturnListByLendId(Long lendId) {
        QueryWrapper<LendReturn> lendReturnQueryWrapper = new QueryWrapper<>();
        lendReturnQueryWrapper.eq("lend_id", lendId);
        return baseMapper.selectList(lendReturnQueryWrapper);
    }

    @Override
    public String commitReturn(Long lendReturnId, Long userId) {

        LendReturn lendReturn = baseMapper.selectById(lendReturnId);
        Lend lend = lendMapper.selectById(lendReturn.getLendId());
        UserInfo fromUserInfo = userInfoMapper.selectById(lend.getUserId());

        // 判断借款人账号余额是否充足
        BigDecimal amount = userAccountService.getAmount(fromUserInfo.getId());
        Assert.isTrue(amount.doubleValue() >= lendReturn.getTotal().doubleValue(), ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentGoodsName", lend.getTitle());
        paramMap.put("agentBatchNo", lendReturn.getReturnNo());
        paramMap.put("fromBindCode", fromUserInfo.getBindCode());// 借款人BindCode
        paramMap.put("totalAmt", lendReturn.getTotal());
        paramMap.put("note", "还款");
        // 根据还款获取回款明细
        List<Map<String, Object>> data = lendItemReturnService.getLendItemListByLendReturnId(lendReturnId);
        paramMap.put("data", JSONObject.toJSONString(data));
        paramMap.put("voteFeeAmt", new BigDecimal(0));
        paramMap.put("returnUrl", HfbConst.BORROW_RETURN_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.BORROW_RETURN_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));
        return FormHelper.buildForm(HfbConst.BORROW_RETURN_URL, paramMap);
    }

    @Override
    public void notifyUrl(Map<String, Object> paramMap) {
        // 幂等性判断
        String agentBatchNo = (String) paramMap.get("agentBatchNo");
        if(transFlowService.isTransFlowSaved(agentBatchNo)){
            log.info("幂等性返回");
            return;
        }
        // 更新还款计划状态
        QueryWrapper<LendReturn> lendReturnQueryWrapper = new QueryWrapper<>();
        lendReturnQueryWrapper.eq("return_no", agentBatchNo);
        LendReturn lendReturn = baseMapper.selectOne(lendReturnQueryWrapper);
        lendReturn.setStatus(1);
        lendReturn.setRealReturnTime(LocalDateTime.now());
        baseMapper.updateById(lendReturn);
        // 如果是最后一期 更新标的状态
        Lend lend = lendMapper.selectById(lendReturn.getLendId());
        if(lendReturn.getLast()){
            lend.setStatus(LendStatusEnum.PAY_OK.getStatus());
            lendMapper.updateById(lend);
        }
        // 还款人账户扣款
        Long userId = lendReturn.getUserId();
        UserInfo fromUserInfo = userInfoMapper.selectById(userId);
        String totalAmt = (String) paramMap.get("totalAmt");
        userAccountMapper.updateAccount(fromUserInfo.getBindCode(), new BigDecimal(totalAmt).negate(), new BigDecimal(0));
        // 增加交易流水
        AtomicReference<TransFlowBO> transFlowBO = new AtomicReference<>(new TransFlowBO(
                fromUserInfo.getBindCode(),
                agentBatchNo,
                TransTypeEnum.RETURN_DOWN.getTransType(),
                new BigDecimal(totalAmt),
                "还款扣款，项目编号" + lend.getLendNo()
        ));
        transFlowService.saveTransFlowByBO(transFlowBO.get());
        // 获取回款计划列表
        QueryWrapper<LendItemReturn> lendItemReturnQueryWrapper = new QueryWrapper<>();
        lendItemReturnQueryWrapper.eq("lend_return_id", lendReturn.getId());
        List<LendItemReturn> lendItemReturnList = lendItemReturnMapper.selectList(lendItemReturnQueryWrapper);

        lendItemReturnList.forEach( item -> {
            // 更新回款计划状态
            item.setStatus(1);
            item.setRealReturnTime(LocalDateTime.now());
            lendItemReturnMapper.updateById(item);
            // 更新lenditem投资信息
            LendItem lendItem = lendItemMapper.selectById(item.getLendItemId());
            lendItem.setRealAmount(lendItem.getRealAmount().add(item.getInterest()));
            lendItemMapper.updateById(lendItem);
            // 投资人账户打款
            Long investUserId = item.getInvestUserId();
            UserInfo investUserInfo = userInfoMapper.selectById(investUserId);
            userAccountMapper.updateAccount(investUserInfo.getBindCode(), item.getTotal(), new BigDecimal(0));
            // 增加交易流水
            transFlowBO.set(new TransFlowBO(
                    investUserInfo.getBindCode(),
                    LendNoUtils.getReturnItemNo(),
                    TransTypeEnum.INVEST_BACK.getTransType(),
                    item.getTotal(),
                    "放款回款，项目编号" + lend.getLendNo()
            ));
            transFlowService.saveTransFlowByBO(transFlowBO.get());
        });
    }
}
