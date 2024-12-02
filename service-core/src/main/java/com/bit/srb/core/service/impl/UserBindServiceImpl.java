package com.bit.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.common.exception.Assert;
import com.bit.common.result.ResponseEnum;
import com.bit.srb.core.enums.UserBindEnum;
import com.bit.srb.core.hfb.FormHelper;
import com.bit.srb.core.hfb.HfbConst;
import com.bit.srb.core.hfb.RequestHelper;
import com.bit.srb.core.mapper.UserBindMapper;
import com.bit.srb.core.mapper.UserInfoMapper;
import com.bit.srb.core.pojo.entity.UserBind;
import com.bit.srb.core.pojo.entity.UserInfo;
import com.bit.srb.core.pojo.vo.UserBindVO;
import com.bit.srb.core.service.UserBindService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {

        // 不同id相同身份证号，报错
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper
                .eq("id_card", userBindVO.getIdCard())
                .ne("user_id", userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        Assert.isNull(userBind, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);

        // 相同id 更新
        userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper
                .eq("user_id", userId);
        userBind = baseMapper.selectOne(userBindQueryWrapper);
        if(userBind == null)
        {
            // 创建用户绑定记录
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO, userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        }else{
            BeanUtils.copyProperties(userBindVO, userBind);
            baseMapper.updateById(userBind);
        }


        // 组装表单
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);

        paramMap.put("idCard", userBindVO.getIdCard());
        paramMap.put("personalName", userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());

        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);

        paramMap.put("timeStamp", RequestHelper.getTimestamp());
        paramMap.put("sign",RequestHelper.getSign(paramMap));


        return FormHelper.buildForm(HfbConst.USERBIND_URL,paramMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> paramMap) {
        // 获取数据
        String bindCode = (String)paramMap.get("bindCode");
        String agentUserId = (String)paramMap.get("agentUserId");

        // 更新用户绑定表
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper
                .eq("user_id", agentUserId);

        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);

        // 更新用户信息表
        UserInfo userInfo = userInfoMapper.selectById(agentUserId);
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfoMapper.updateById(userInfo);
    }
}
