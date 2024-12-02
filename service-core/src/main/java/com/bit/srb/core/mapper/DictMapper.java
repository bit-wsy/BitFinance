package com.bit.srb.core.mapper;

import com.bit.srb.core.pojo.dto.ExcelDictDTO;
import com.bit.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface DictMapper extends BaseMapper<Dict> {

    void insertBatch(List<ExcelDictDTO> list);
}
