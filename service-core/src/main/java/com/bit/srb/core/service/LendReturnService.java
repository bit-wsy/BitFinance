package com.bit.srb.core.service;

import com.bit.srb.core.pojo.entity.LendReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface LendReturnService extends IService<LendReturn> {

    List<LendReturn> getLendReturnListByLendId(Long lendId);

    String commitReturn(Long lendReturnId, Long userId);

    void notifyUrl(Map<String, Object> paramMap);
}
