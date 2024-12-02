package com.bit.srb.core.service;

import com.bit.srb.core.pojo.bo.TransFlowBO;
import com.bit.srb.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface TransFlowService extends IService<TransFlow> {
    void saveTransFlowByBO(TransFlowBO transFlowBO);

    boolean isTransFlowSaved(String agentBillNo);

    List<TransFlow> getTransFlowList(Long userId);
}
