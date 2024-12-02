package com.bit.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bit.srb.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.srb.core.pojo.vo.BorrowerApprovalVO;
import com.bit.srb.core.pojo.vo.BorrowerDetailVO;
import com.bit.srb.core.pojo.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface BorrowerService extends IService<Borrower> {


    void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId);

    Integer getStatusByUserId(Long userId);

    IPage<Borrower> listBorrowerBypage(Page<Borrower> borrowerPage, String key);

    BorrowerDetailVO getBorrowerDetailVOById(Long id);


    void getApproval(BorrowerApprovalVO borrowerApprovalVO);
}
