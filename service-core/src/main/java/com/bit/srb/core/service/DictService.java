package com.bit.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.srb.core.pojo.dto.ExcelDictDTO;
import com.bit.srb.core.pojo.entity.Dict;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
public interface DictService extends IService<Dict> {

    void importData(InputStream inputStream);

    List<ExcelDictDTO> listDictData();

    List<Dict> listByParentId(Long parentId);

    List<Dict> findByDictCode(String dictCode);

    String getNameByValueAndDictCode(Integer value, String dictCode);

}
