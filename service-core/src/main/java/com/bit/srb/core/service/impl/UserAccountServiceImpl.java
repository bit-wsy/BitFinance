package com.bit.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.common.exception.Assert;
import com.bit.common.result.ResponseEnum;
import com.bit.srb.base.dto.SmsDTO;
import com.bit.srb.core.enums.TransTypeEnum;
import com.bit.srb.core.hfb.FormHelper;
import com.bit.srb.core.hfb.HfbConst;
import com.bit.srb.core.hfb.RequestHelper;
import com.bit.srb.core.mapper.UserAccountMapper;
import com.bit.srb.core.mapper.UserInfoMapper;
import com.bit.srb.core.pojo.bo.TransFlowBO;
import com.bit.srb.core.pojo.entity.UserAccount;
import com.bit.srb.core.pojo.entity.UserInfo;
import com.bit.srb.core.service.TransFlowService;
import com.bit.srb.core.service.UserAccountService;
import com.bit.srb.core.service.UserInfoService;
import com.bit.srb.core.util.LendNoUtils;
import com.bit.srb.rabbitutil.constant.MQConst;
import com.bit.srb.rabbitutil.service.MQService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Service
@Slf4j
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private TransFlowService transFlowService;

    @Resource
    private MQService mqService;

    @Override
    public String commitCharge(Long userId, BigDecimal chargeAmount) {

        UserInfo userInfo = userInfoMapper.selectById(userId);

        Map<String, Object> params = new HashMap<>();

        //判断账户绑定状态
        Assert.notEmpty(userInfo.getBindCode(), ResponseEnum.USER_NO_BIND_ERROR);

        params.put("agentId", HfbConst.AGENT_ID);
        params.put("agentBillNo", LendNoUtils.getChargeNo());
        params.put("bindCode", userInfo.getBindCode());
        params.put("chargeAmt", chargeAmount);
        params.put("feeAmt", new BigDecimal(0));
        params.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);
        params.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        params.put("timeStamp", RequestHelper.getTimestamp());
        params.put("sign", RequestHelper.getSign(params));

        return FormHelper.buildForm(HfbConst.RECHARGE_URL, params);
    }

    @Override
    public String nofity(Map<String, Object> paramMap) {
        // 幂等性判断
        // 插入交易流水号
        String agentBillNo = (String) paramMap.get("agentBillNo");
        boolean transFlowSaved = transFlowService.isTransFlowSaved(agentBillNo);
        if(transFlowSaved){
            log.warn("幂等性返回");
            return "success";
        }

        log.info("充值成功");
        String bindCode = (String) paramMap.get("bindCode");
        String chargeAmt = (String) paramMap.get("chargeAmt");

        baseMapper.updateAccount(bindCode, new BigDecimal(chargeAmt), new BigDecimal(0));


        TransFlowBO transFlowBO = new TransFlowBO();
        transFlowBO.setBindCode(bindCode);
        transFlowBO.setTransNo(agentBillNo);
        transFlowBO.setTransType(TransTypeEnum.RECHARGE.getTransType());
        transFlowBO.setTransAmount(new BigDecimal(chargeAmt));
        transFlowBO.setMemo("用户充值");

        transFlowService.saveTransFlowByBO(transFlowBO);

        // 用RABBIT MQ发送消息
        SmsDTO smsDTO = new SmsDTO();
        smsDTO.setMessage("充值成功");
        smsDTO.setMobile(userInfoService.getMobileByBindCode(bindCode));
        mqService.sendMessage(
                MQConst.EXCHANGE_TOPIC_SMS,
                MQConst.ROUTING_SMS_ITEM,
                smsDTO
        );

        return "success";
    }

    @Override
    public BigDecimal getAmount(Long userId) {
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", userId);
        UserAccount userAccount = baseMapper.selectOne(userAccountQueryWrapper);
        return userAccount.getAmount();
    }

    @Override
    public String commitWithdraw(Long userId, BigDecimal fetchAmt) {
        // 校验用户余额是否充足
        BigDecimal amount = this.getAmount(userId);
        Assert.isTrue(amount.doubleValue() >= fetchAmt.doubleValue(), ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        UserInfo userInfo = userInfoMapper.selectById(userId);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getWithdrawNo());
        paramMap.put("bindCode", userInfo.getBindCode());
        paramMap.put("fetchAmt", fetchAmt);
        paramMap.put("returnUrl", HfbConst.WITHDRAW_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.WITHDRAW_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));
        return FormHelper.buildForm(HfbConst.WITHDRAW_URL, paramMap);

    }

    @Override
    public void notifyWithdraw(Map<String, Object> paramMap) {
        // 幂等性判断
        String agentBillNo = (String)paramMap.get("agentBillNo");
        if(transFlowService.isTransFlowSaved(agentBillNo)){
            log.info("幂等性返回");
            return;
        }else{
            // 更新用户账户
            String fetchAmt = (String)paramMap.get("fetchAmt");
            String bindCode = (String)paramMap.get("bindCode");
            baseMapper.updateAccount(bindCode, new BigDecimal(fetchAmt).negate(), new BigDecimal(0));
            // 增加交易流水
            TransFlowBO transFlowBO = new TransFlowBO();
            transFlowBO.setBindCode(bindCode);
            transFlowBO.setTransNo(agentBillNo);
            transFlowBO.setTransType(TransTypeEnum.WITHDRAW.getTransType());
            transFlowBO.setTransAmount(new BigDecimal(fetchAmt));
            transFlowBO.setMemo("用户提现");
            transFlowService.saveTransFlowByBO(transFlowBO);

        }

    }
}
