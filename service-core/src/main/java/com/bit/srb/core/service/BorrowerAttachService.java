package com.bit.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.srb.core.pojo.entity.BorrowerAttach;
import com.bit.srb.core.pojo.vo.BorrowerAttachVO;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {
    List<BorrowerAttachVO> getBorrowerAttachVOByBorrowerId(Long borrowerId);
}
