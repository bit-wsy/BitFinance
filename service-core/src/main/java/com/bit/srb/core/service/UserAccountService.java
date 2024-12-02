package com.bit.srb.core.service;

import com.bit.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface UserAccountService extends IService<UserAccount> {

    String commitCharge(Long userId, BigDecimal chargeAmount);

    String nofity(Map<String, Object> paramMap);

    BigDecimal getAmount(Long userId);

    String commitWithdraw(Long userId, BigDecimal fetchAmt);

    void notifyWithdraw(Map<String, Object> paramMap);
}
