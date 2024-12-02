package com.bit.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.common.exception.Assert;
import com.bit.common.result.ResponseEnum;
import com.bit.srb.core.enums.BorrowInfoStatusEnum;
import com.bit.srb.core.enums.BorrowerStatusEnum;
import com.bit.srb.core.enums.UserBindEnum;
import com.bit.srb.core.mapper.BorrowInfoMapper;
import com.bit.srb.core.mapper.BorrowerMapper;
import com.bit.srb.core.mapper.IntegralGradeMapper;
import com.bit.srb.core.mapper.UserInfoMapper;
import com.bit.srb.core.pojo.entity.BorrowInfo;
import com.bit.srb.core.pojo.entity.Borrower;
import com.bit.srb.core.pojo.entity.IntegralGrade;
import com.bit.srb.core.pojo.entity.UserInfo;
import com.bit.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.bit.srb.core.pojo.vo.BorrowerDetailVO;
import com.bit.srb.core.service.BorrowInfoService;
import com.bit.srb.core.service.BorrowerService;
import com.bit.srb.core.service.DictService;
import com.bit.srb.core.service.LendService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private BorrowerService borrowerService;

    @Resource
    private LendService lendService;

    @Override
    public BigDecimal getBorrowAmountByUserId(Long userId) {

        // 根据userid获取积分
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        Integer integral = userInfo.getIntegral();

        // 根据积分区间获取借款
        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper
                .le("integral_start", integral)
                .ge("integral_end",integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(integralGradeQueryWrapper);
        if(integralGrade == null){
            return BigDecimal.valueOf(0.0);
        }

        return integralGrade.getBorrowAmount();
    }

    @Override
    public void saveBorrowInfoById(Long userId, BorrowInfo borrowInfo) {
        // !!!!判断用户绑定状态和额度申请状态
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.equals(userInfo.getBindStatus(), UserBindEnum.BIND_OK.getStatus(), ResponseEnum.USER_NO_BIND_ERROR );
        Assert.equals(userInfo.getBorrowAuthStatus(), BorrowerStatusEnum.AUTH_OK.getStatus(), ResponseEnum.USER_NO_AMOUNT_ERROR);
        // 表单校验
        BigDecimal borrowAmount = this.getBorrowAmountByUserId(userId);
        BigDecimal amount = borrowInfo.getAmount();
        Assert.notNull(amount, ResponseEnum.BORROW_AMOUNT_NULL_ERROR);

        int compare = borrowAmount.compareTo(amount);
        Assert.equals(compare, 1, ResponseEnum.USER_AMOUNT_LESS_ERROR);

        // 设置状态
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        borrowInfo.setUserId(userId);
        // ！！把年华换成小数
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        baseMapper.insert(borrowInfo);
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        // id和userid不是一个东西
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.eq("user_id", userId);
        List<BorrowInfo> borroweInfo = baseMapper.selectList(borrowInfoQueryWrapper);

        if(borroweInfo.isEmpty()){ // 未申请
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }
        // 是否要取更新时间最晚的那个
        return borroweInfo.get(borroweInfo.size() - 1).getStatus();
    }

    @Override
    public List<BorrowInfo> getBorrowInfoList() {
        // 获取关联查询结果
        List<BorrowInfo> borrowInfoAndUserInfoList = baseMapper.getBorrowInfoAndUserInfoList();

        // 查询数据字典然后push到其他参数里
        borrowInfoAndUserInfoList.forEach(borrowInfo -> {
            borrowInfo.getParam()
                    .put("returnMethod", dictService.getNameByValueAndDictCode(borrowInfo.getReturnMethod(), "returnMethod"));

            borrowInfo.getParam()
                    .put("moneyUse", dictService.getNameByValueAndDictCode(borrowInfo.getMoneyUse(), "moneyUse"));

            borrowInfo.getParam()
                    .put("borrowStatus", BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus()));

        });

        return borrowInfoAndUserInfoList;
    }

    @Override
    public Map<String, Object> getBorrowInfoByUserId(Long id) {
        // 获取借款信息
        BorrowInfo borrowInfo = baseMapper.selectById(id);
        borrowInfo.getParam()
                .put("returnMethod", dictService.getNameByValueAndDictCode(borrowInfo.getReturnMethod(), "returnMethod"));

        borrowInfo.getParam()
                .put("moneyUse", dictService.getNameByValueAndDictCode(borrowInfo.getMoneyUse(), "moneyUse"));

        borrowInfo.getParam()
                .put("borrowStatus", BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus()));

        Long userId = borrowInfo.getUserId();

        // 获取借款人信息
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id", userId);
        List<Borrower> borrowers = borrowerMapper.selectList(borrowerQueryWrapper);
        Borrower borrower = borrowers.get(borrowers.size() - 1);

        BorrowerDetailVO borrowerDetailVOById = borrowerService.getBorrowerDetailVOById(borrower.getId());

        Map<String, Object> borrowInfoDetail = new HashMap<>();

        borrowInfoDetail.put("borrowInfo", borrowInfo);
        borrowInfoDetail.put("borrower", borrowerDetailVOById);

        return borrowInfoDetail;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void borrowInfoApproval(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        // 更新borrow_info表的状态
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoApprovalVO.getId());
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        baseMapper.updateById(borrowInfo);

        // 如果审批通过 新建一个标的
        if(Objects.equals(borrowInfoApprovalVO.getStatus(), BorrowInfoStatusEnum.CHECK_OK.getStatus())){
            lendService.createLend(borrowInfoApprovalVO, borrowInfo);
        }
    }
}
