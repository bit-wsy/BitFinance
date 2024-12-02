package com.bit.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.srb.core.enums.TransTypeEnum;
import com.bit.srb.core.mapper.TransFlowMapper;
import com.bit.srb.core.mapper.UserInfoMapper;
import com.bit.srb.core.pojo.bo.TransFlowBO;
import com.bit.srb.core.pojo.entity.TransFlow;
import com.bit.srb.core.pojo.entity.UserInfo;
import com.bit.srb.core.service.TransFlowService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {

    @Resource
    private UserInfoMapper userInfoMapper;
    @Override
    public void saveTransFlowByBO(TransFlowBO transFlowBO) {
        TransFlow transFlow = new TransFlow();

        String bindCode = transFlowBO.getBindCode();
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("bind_code", bindCode);
        UserInfo userInfo = userInfoMapper.selectOne(userInfoQueryWrapper);

        transFlow.setUserId(userInfo.getId());
        transFlow.setUserName(userInfo.getName());

        transFlow.setTransNo(transFlowBO.getTransNo());
        transFlow.setTransType(transFlowBO.getTransType());
        transFlow.setTransTypeName(TransTypeEnum.getTransTypeName(transFlowBO.getTransType()));
        transFlow.setTransAmount(transFlowBO.getTransAmount());
        transFlow.setMemo(transFlowBO.getMemo());

        baseMapper.insert(transFlow);
    }

    @Override
    public boolean isTransFlowSaved(String agentBillNo) {
        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper.eq("trans_no", agentBillNo);
        Long count = baseMapper.selectCount(transFlowQueryWrapper);
        return count > 0;
    }

    @Override
    public List<TransFlow> getTransFlowList(Long userId) {
        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper
                .eq("user_id", userId)
                .orderByDesc("id");
        return baseMapper.selectList(transFlowQueryWrapper);
    }
}
