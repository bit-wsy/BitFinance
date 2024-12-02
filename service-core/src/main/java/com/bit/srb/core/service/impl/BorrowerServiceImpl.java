package com.bit.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.common.exception.Assert;
import com.bit.common.result.ResponseEnum;
import com.bit.srb.core.enums.BorrowerStatusEnum;
import com.bit.srb.core.enums.IntegralEnum;
import com.bit.srb.core.mapper.BorrowerAttachMapper;
import com.bit.srb.core.mapper.BorrowerMapper;
import com.bit.srb.core.mapper.UserInfoMapper;
import com.bit.srb.core.mapper.UserIntegralMapper;
import com.bit.srb.core.pojo.entity.Borrower;
import com.bit.srb.core.pojo.entity.BorrowerAttach;
import com.bit.srb.core.pojo.entity.UserInfo;
import com.bit.srb.core.pojo.entity.UserIntegral;
import com.bit.srb.core.pojo.vo.BorrowerApprovalVO;
import com.bit.srb.core.pojo.vo.BorrowerDetailVO;
import com.bit.srb.core.pojo.vo.BorrowerVO;
import com.bit.srb.core.service.BorrowerAttachService;
import com.bit.srb.core.service.BorrowerService;
import com.bit.srb.core.service.DictService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerAttachService borrowerAttachService;

    @Resource
    private UserIntegralMapper userIntegralMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);
        // 更新borrower
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        // &&&&
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        baseMapper.insert(borrower);

        // 更新borrowattach
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach( borrowerAttach -> {
            // &&&&
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });

        // 更新Userinfo
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);

    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        // id和userid不是一个东西
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id", userId);
        List<Borrower> borrowers = baseMapper.selectList(borrowerQueryWrapper);

        if(borrowers.isEmpty()){ // 未进行绑定
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        // 是否要取更新时间最晚的那个
        return borrowers.get(borrowers.size() - 1).getStatus();
    }

    @Override
    public IPage<Borrower> listBorrowerBypage(Page<Borrower> borrowerPage, String key) {

        if(StringUtils.isBlank(key)){
            return baseMapper.selectPage(borrowerPage, null);
        }

        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper
                .like("name", key)
                .or()
                .like("mobile", key)
                .or()
                .like("id_card", key);
        return baseMapper.selectPage(borrowerPage, borrowerQueryWrapper);
    }

    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {


        Borrower borrower = baseMapper.selectById(id);
        Assert.notNull(borrower, ResponseEnum.ERROR);

        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();

        // 拷贝
        BeanUtils.copyProperties(borrower, borrowerDetailVO);

        // 设置二元数字转字符串变量
        borrowerDetailVO.setMarry(borrower.getMarry() ? "是": "否");
        borrowerDetailVO.setSex(borrower.getSex() == 1 ? "男": "女");

        // 设置数据列表变量
        borrowerDetailVO.setIndustry(dictService.getNameByValueAndDictCode(borrower.getIndustry(), "industry"));
        borrowerDetailVO.setEducation(dictService.getNameByValueAndDictCode(borrower.getEducation(), "education"));
        borrowerDetailVO.setIncome(dictService.getNameByValueAndDictCode(borrower.getIncome(), "income"));
        borrowerDetailVO.setReturnSource(dictService.getNameByValueAndDictCode(borrower.getReturnSource(), "returnSource"));
        borrowerDetailVO.setContactsRelation(dictService.getNameByValueAndDictCode(borrower.getContactsRelation(), "relation"));

        borrowerDetailVO.setStatus(BorrowerStatusEnum.getMsgByStatus(borrower.getStatus()));


        // 设置附件
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachService.getBorrowerAttachVOByBorrowerId(id));
        return borrowerDetailVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void getApproval(BorrowerApprovalVO borrowerApprovalVO) {


        // 改变borrower的状态信息
        Borrower borrower = baseMapper.selectById(borrowerApprovalVO.getBorrowerId());
        borrower.setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(borrower);

        // 改变userinfo的积分信息
        Long userId = borrower.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer currentIntegral = userInfo.getIntegral();

        //！！！
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);

        //！！！
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral.setContent("借款人基本信息");
        userIntegralMapper.insert(userIntegral);
        currentIntegral += borrowerApprovalVO.getInfoIntegral();


        if(borrowerApprovalVO.getIsIdCardOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();
        }

        if(borrowerApprovalVO.getIsCarOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
        }

        if(borrowerApprovalVO.getIsHouseOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
        }

        userInfo.setIntegral(currentIntegral);
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        userInfoMapper.updateById(userInfo);
    }
}
