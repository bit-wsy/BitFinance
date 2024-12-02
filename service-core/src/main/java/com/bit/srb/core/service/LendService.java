package com.bit.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.srb.core.pojo.entity.BorrowInfo;
import com.bit.srb.core.pojo.entity.Lend;
import com.bit.srb.core.pojo.entity.LendItem;
import com.bit.srb.core.pojo.entity.LendItemReturn;
import com.bit.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface LendService extends IService<Lend> {

    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    List<Lend> selectList();

    Map<String, Object> getLendById(Long id);

    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod);

    void makeLoan(Long id);

    void getReturnList(Lend lend);

    List<LendItemReturn> getLendItemReturn(Lend lend, Map<Integer, Long> lendReturnMap, LendItem lendItem);

}
