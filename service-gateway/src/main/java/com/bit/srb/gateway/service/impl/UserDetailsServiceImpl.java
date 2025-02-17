package com.bit.srb.gateway.service.impl;

//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    @Autowired
//    private UserServiceClient userServiceClient;
//
//    @Override
//    public UserDetail loadUserByUserId(Long userId) throws UsernameNotFoundException {
//        UserInfo userInfo = userServiceClient.getUserInfoById(userId);
//        // 用户存在
//        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
//        // 用户是否被禁用
//        Assert.equals(userInfo.getStatus(), UserInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOKED_ERROR);
//        return new UserDetail(userInfo, Collections.emptyList());
//    }
//}
