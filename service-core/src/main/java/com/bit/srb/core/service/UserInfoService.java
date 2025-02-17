package com.bit.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bit.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.srb.core.pojo.query.UserInfoQuery;
import com.bit.srb.core.pojo.vo.LogInVO;
import com.bit.srb.core.pojo.vo.RegisterVO;
import com.bit.srb.core.pojo.vo.UserIndexVO;
import com.bit.srb.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVO registerVO);

    UserInfoVO login(LogInVO logInVO, String ip);

    IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery);

    void lock(Long id, Integer status);

    boolean checkMobile(String mobile);

    UserIndexVO getIndexUserInfo(Long userId);

    String getMobileByBindCode(String bindCode);

    UserInfo getUserInfoById(Long userId);
}
