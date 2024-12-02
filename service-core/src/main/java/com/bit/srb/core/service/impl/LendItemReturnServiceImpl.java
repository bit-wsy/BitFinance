package com.bit.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.srb.core.mapper.*;
import com.bit.srb.core.pojo.entity.*;
import com.bit.srb.core.service.LendItemReturnService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务实现类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@Service
public class LendItemReturnServiceImpl extends ServiceImpl<LendItemReturnMapper, LendItemReturn> implements LendItemReturnService {

    @Resource
    private LendMapper lendMapper;

    @Resource
    private LendReturnMapper lendReturnMapper;

    @Resource
    private LendItemMapper lendItemMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public List<LendItemReturn> getLendItemReturnByUserIdAndLendId(Long userId, Long lendId) {
        QueryWrapper<LendItemReturn> lendItemReturnQueryWrapper = new QueryWrapper<>();
        lendItemReturnQueryWrapper
                .eq("invest_user_id", userId)
                .eq("lend_id", lendId)
                .orderByAsc("current_period");
        return baseMapper.selectList(lendItemReturnQueryWrapper);
    }

    @Override
    public List<Map<String, Object>> getLendItemListByLendReturnId(Long lendReturnId) {
        QueryWrapper<LendItemReturn> lendItemReturnQueryWrapper = new QueryWrapper<>();
        lendItemReturnQueryWrapper.eq("lend_return_id", lendReturnId);
        List<LendItemReturn> lendItemReturnList = baseMapper.selectList(lendItemReturnQueryWrapper);

        LendReturn lendReturn = lendReturnMapper.selectById(lendReturnId);
        Lend lend = lendMapper.selectById(lendReturn.getLendId());


        List<Map<String, Object>> data = new ArrayList<>();
        lendItemReturnList.forEach( lendItemReturn -> {
            LendItem lendItem = lendItemMapper.selectById(lendItemReturn.getLendItemId());
            UserInfo toUserInfo = userInfoMapper.selectById(lendItem.getInvestUserId());

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("agentProjectCode",lend.getLendNo());
            paramMap.put("voteBillNo",lendItem.getLendItemNo());
            paramMap.put("toBindCode",toUserInfo.getBindCode());
            paramMap.put("transitAmt",lendItemReturn.getTotal());
            paramMap.put("baseAmt",lendItemReturn.getPrincipal());
            paramMap.put("benifitAmt",lendItemReturn.getInterest());
            paramMap.put("feeAmt",new BigDecimal(0));

            data.add(paramMap);
        });

        return data;
    }
}
