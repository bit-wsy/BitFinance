package com.bit.srb.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bit.srb.core.pojo.entity.BorrowInfo;

import java.util.List;

/**
 * <p>
 * 借款信息表 Mapper 接口
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface BorrowInfoMapper extends BaseMapper<BorrowInfo> {

    List<BorrowInfo> getBorrowInfoAndUserInfoList();

}
