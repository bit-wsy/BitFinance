package com.bit.srb.core.controller.api;

import com.bit.common.result.R;
import com.bit.srb.core.pojo.entity.Dict;
import com.bit.srb.core.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "dict-controller", description = "数据字典")
@RestController
@RequestMapping("/api/core/dict")
@Slf4j
public class DictController {

    @Resource
    private DictService dictService;

    @Operation(description = "根据dictcode获取数据字典")
    @GetMapping("/findByDictCode/{dictCode}")
    public R dictList(
            @Parameter(description = "字典code")
            @PathVariable String dictCode
    ){
        List<Dict> dictList =  dictService.findByDictCode(dictCode);
        return R.ok().data("dictList",dictList);
    }
}
