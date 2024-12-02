package com.bit.srb.core.service;

import com.bit.srb.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    BigDecimal getBorrowAmountByUserId(Long userId);

    void saveBorrowInfoById(Long userId, BorrowInfo borrowInfo);

    Integer getStatusByUserId(Long userId);

    List<BorrowInfo> getBorrowInfoList();

    Map<String, Object> getBorrowInfoByUserId(Long userId);

    void borrowInfoApproval(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
