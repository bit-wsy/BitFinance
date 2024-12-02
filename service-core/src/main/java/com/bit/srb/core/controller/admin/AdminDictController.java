package com.bit.srb.core.admin;


import com.alibaba.excel.EasyExcel;
import com.bit.common.exception.BusinessException;
import com.bit.common.result.R;
import com.bit.common.result.ResponseEnum;
import com.bit.srb.core.pojo.dto.ExcelDictDTO;
import com.bit.srb.core.pojo.entity.Dict;
import com.bit.srb.core.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@RestController
//@CrossOrigin
@Tag(name = "admin-dict-controller", description = "数据字典管理")
@RequestMapping("/admin/core/dict")
public class AdminDictController {

    @Resource
    DictService dictService;

    @Operation(summary = "Excel批量导入")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //向服务端传输数据
    public R batchImport(
            @Parameter(description = "Excel数据字典文件", required = true)
            @RequestParam("file")
            MultipartFile file){

        try {
            InputStream inputStream = file.getInputStream();
            dictService.importData(inputStream);
            return R.ok().message("数据字典批量导入成功");
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }

    @Operation(summary = "Excel数据导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException{
        response.setContentType("application/vnd.ms-excel"); //设定响应类型
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("mydict","UTF-8").replaceAll("\\+","%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        //设置以附件的形式下载到本地
        EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.listDictData());
    }

    // 根据父ID加载类别
    @Operation(summary = "根据上级id获取子节点数据列表")
    @GetMapping("/listByParentId/{parentId}")
    public R listByParentId(
            @Parameter(description = "上级节点id", required = true)
            @PathVariable Long parentId){
        List<Dict> dictList = dictService.listByParentId(parentId);
        return R.ok().data("list",dictList);
    }
}

