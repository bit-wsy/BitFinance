package com.bit.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.common.exception.Assert;
import com.bit.common.result.ResponseEnum;
import com.bit.common.util.MD5;
import com.bit.srb.base.util.JwtUtils;
import com.bit.srb.core.mapper.UserAccountMapper;
import com.bit.srb.core.mapper.UserInfoMapper;
import com.bit.srb.core.mapper.UserLoginRecordMapper;
import com.bit.srb.core.pojo.entity.UserAccount;
import com.bit.srb.core.pojo.entity.UserInfo;
import com.bit.srb.core.pojo.entity.UserLoginRecord;
import com.bit.srb.core.pojo.query.UserInfoQuery;
import com.bit.srb.core.pojo.vo.LogInVO;
import com.bit.srb.core.pojo.vo.RegisterVO;
import com.bit.srb.core.pojo.vo.UserIndexVO;
import com.bit.srb.core.pojo.vo.UserInfoVO;
import com.bit.srb.core.service.UserInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Service
@Slf4j
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO registerVO) {

        // 判断是不是已注册用户
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile", registerVO.getMobile());
        Long count = baseMapper.selectCount(userInfoQueryWrapper);
        Assert.isTrue(count == 0, ResponseEnum.MOBILE_EXIST_ERROR);
        // 向userInfo插入记录
        UserInfo userInfo = new UserInfo();
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        userInfo.setHeadImg(UserInfo.AVATAR_DEFAULT);
        baseMapper.insert(userInfo);
        // 向userAccount插入记录
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVO login(LogInVO logInVO, String ip) {
        String password = logInVO.getPassword();
        String mobile = logInVO.getMobile();
        Integer userType = logInVO.getUserType();
        
        // 判断用户是否存在
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper
                .eq("mobile",mobile)
                .eq("user_type",userType);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        // 密码是否正确
        Assert.equals(userInfo.getPassword(), MD5.encrypt(password), ResponseEnum.LOGIN_PASSWORD_ERROR);
        // 用户是否被禁用
        Assert.equals(userInfo.getStatus(), UserInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOKED_ERROR);
        // 记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);
        // 生成token
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());
        log.info("token: " + token);
        // 组装userinfoVO
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setName(userInfo.getName());
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setHeadImg(userInfo.getHeadImg());
        userInfoVO.setUserType(userType);
        userInfoVO.setToken(token);
        userInfoVO.setMobile(mobile);

        return userInfoVO;
    }

    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery) {

        if(userInfoQuery == null){
            return baseMapper.selectPage(pageParam, null);
        }

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();

        String mobile = userInfoQuery.getMobile();
        Integer userType = userInfoQuery.getUserType();
        Integer status = userInfoQuery.getStatus();

        userInfoQueryWrapper
                .eq(StringUtils.isNotBlank(mobile),"mobile", mobile)
                .eq(userType != null, "user_type", userType)// 必须和数据库表列名一致
                .eq(status != null, "status", status);

        return baseMapper.selectPage(pageParam, userInfoQueryWrapper);
    }

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public boolean checkMobile(String mobile) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",mobile);
        Long count = baseMapper.selectCount(userInfoQueryWrapper);
        return count > 0;
    }

    @Override
    public UserIndexVO getIndexUserInfo(Long userId) {
        UserInfo userInfo = baseMapper.selectById(userId);

        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", userId);
        UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);

        QueryWrapper<UserLoginRecord> userLoginRecordQueryWrapper = new QueryWrapper<>();
        userLoginRecordQueryWrapper
                .eq("user_id", userId)
                .orderByDesc("create_time")
                .last("limit 1");
        UserLoginRecord userLoginRecord = userLoginRecordMapper.selectOne(userLoginRecordQueryWrapper);

        UserIndexVO userIndexVO = new UserIndexVO();
        userIndexVO.setUserId(userId);
        userIndexVO.setName(userInfo.getName());
        userIndexVO.setNickName(userInfo.getNickName());
        userIndexVO.setUserType(userInfo.getUserType());
        userIndexVO.setHeadImg(userInfo.getHeadImg());
        userIndexVO.setBindStatus(userInfo.getBindStatus());
        userIndexVO.setAmount(userAccount.getAmount());
        userIndexVO.setFreezeAmount(userAccount.getFreezeAmount());
        userIndexVO.setLastLoginTime(userLoginRecord.getCreateTime());
        return userIndexVO;
    }

    @Override
    public String getMobileByBindCode(String bindCode) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("bind_code", bindCode);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);
        return userInfo.getMobile();
    }
}
