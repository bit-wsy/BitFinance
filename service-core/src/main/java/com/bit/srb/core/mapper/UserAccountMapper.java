package com.bit.srb.core.mapper;

import com.bit.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    void updateAccount(@Param("bindCode") String bindCode,
                       @Param("chargeAmt") BigDecimal chargeAmt,
                       @Param("freezeAmt") BigDecimal freezeAmt);
}
