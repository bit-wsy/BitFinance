package com.bit.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bit.srb.core.pojo.entity.BorrowerAttach;
import com.bit.srb.core.mapper.BorrowerAttachMapper;
import com.bit.srb.core.pojo.vo.BorrowerAttachVO;
import com.bit.srb.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    @Override
    public List<BorrowerAttachVO> getBorrowerAttachVOByBorrowerId(Long borrowerId) {

        QueryWrapper<BorrowerAttach> borrowerAttachQueryWrapper = new QueryWrapper<>();
        borrowerAttachQueryWrapper.eq("borrower_id", borrowerId);
        List<BorrowerAttach> borrowerAttaches = baseMapper.selectList(borrowerAttachQueryWrapper);

        List<BorrowerAttachVO> borrowerAttachVOS = new ArrayList<>();

        borrowerAttaches.forEach( borrowerAttach -> {

            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();
            borrowerAttachVO.setImageUrl(borrowerAttach.getImageUrl());
            borrowerAttachVO.setImageType(borrowerAttach.getImageType());

            borrowerAttachVOS.add(borrowerAttachVO);

        });
        return borrowerAttachVOS;
    }
}
