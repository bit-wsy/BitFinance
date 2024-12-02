package com.bit.srb.core.admin;


import com.bit.common.exception.Assert;
import com.bit.common.result.R;
import com.bit.common.result.ResponseEnum;
import com.bit.srb.core.pojo.entity.IntegralGrade;
import com.bit.srb.core.service.IntegralGradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */

//@CrossOrigin
@Tag(name = "admin-integral-grade-controller", description = "描述：测试Admin积分关系接口")
@RestController
@RequestMapping("/admin/core/integralGrade")
public class AdminIntegralGradeController {

    @Resource
    private IntegralGradeService integralGradeService;

    @Operation(summary = "积分等级列表")
    @GetMapping("/list") // 该路径下的get由该方法处理
    public R listAll() {
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list",list).message("获取列表成功");
               
    }
    @Operation(summary = "根据id删除数据记录")
    @DeleteMapping("/remove/{id}")
        public R removeById(
            @Parameter( description = "数据ID")
            @PathVariable Integer id) {
        boolean result = integralGradeService.removeById(id);
        if(result){
            return R.ok().message("数据删除成功");
        }else{
            return R.error().message("数据删除失败");
        }
    }

    @Operation(summary = "新增积分等级")
    @PostMapping("/save") //向服务端传输数据
    public R save(
            @Parameter(description = "积分等级对象", required = true)
            @RequestBody IntegralGrade integralGrade) { // 绑定json数据
        // 抛出异常
//        if (integralGrade.getBorrowAmount() == null){
//            throw new BusinessException(ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
//        }
        Assert.notNull(integralGrade.getBorrowAmount(),ResponseEnum.BORROW_AMOUNT_NULL_ERROR);

        boolean result = integralGradeService.save(integralGrade);
        if(result){
            return R.ok().message("保存成功");
        }
        else {
            return R.error().message("保存失败");
        }
    }

    @Operation(summary = "根据id查询记录")
    @GetMapping("/get/{id}")
    public R getById(
            @Parameter(description = "需要查询的用户id", required = true)
            @PathVariable Long id)
    {
        IntegralGrade integralGrade = integralGradeService.getById(id);
        if(integralGrade != null){
            return R.ok().data("record",integralGrade);
        } else{
            return R.error().message("数据获取失败");
        }

    }

    @Operation(summary = "更新积分等级")
    @PutMapping("/update")
    public R updateById(
            @Parameter (description = "需要修改的对象", required = true)
            @RequestBody IntegralGrade integralGrade){
        System.out.println("进入了update");
        boolean result = integralGradeService.updateById(integralGrade);// 这里传入的是实体对象，不是ID
        if(result){
            return R.ok().message("更新成功");
        }
        else {
            return R.error().message("更新失败");
        }
    }
}

