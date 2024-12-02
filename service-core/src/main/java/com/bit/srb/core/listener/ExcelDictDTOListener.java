package com.bit.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.bit.srb.core.mapper.DictMapper;
import com.bit.srb.core.pojo.dto.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {

    // 数据列表
    private List<ExcelDictDTO> list = new ArrayList<ExcelDictDTO>();

    private static final int BATCH_COUNT = 5;

    // 由于监听器没有被Spring管理，因此不能使用Resource注入的方式
    public ExcelDictDTOListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }


    DictMapper dictMapper;

    @Override
    public void invoke(ExcelDictDTO data, AnalysisContext analysisContext) {

        log.info("解析到一条记录：{}",data);
        // 一般一次处理3000条
        list.add(data);
        if (list.size() >= BATCH_COUNT){
            saveData();
            list.clear();

        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        list.clear();
    }

    private void saveData(){
        // 调用mapper层的save方法
        dictMapper.insertBatch(list);
        log.info("数据存储成功");
    }
}
