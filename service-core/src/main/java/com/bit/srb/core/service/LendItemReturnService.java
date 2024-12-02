package com.bit.srb.core.service;

import com.bit.srb.core.pojo.entity.LendItemReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface LendItemReturnService extends IService<LendItemReturn> {

    List<LendItemReturn> getLendItemReturnByUserIdAndLendId(Long userId, Long lendId);

    List<Map<String, Object>> getLendItemListByLendReturnId(Long lendReturnId);
}
