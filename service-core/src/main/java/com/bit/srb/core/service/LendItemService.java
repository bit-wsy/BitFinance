package com.bit.srb.core.service;

import com.bit.srb.core.pojo.entity.LendItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.srb.core.pojo.vo.InvestVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface LendItemService extends IService<LendItem> {

    String commitInvest(InvestVO investVO);

    String notify(Map<String, Object> paramMap);

    List<LendItem> getLendItemByLendId(Long lendId, Integer status);

    List<LendItem> getAllLendItemByLendId(Long lendId);

}
