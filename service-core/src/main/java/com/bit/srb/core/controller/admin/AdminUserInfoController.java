package com.bit.srb.core.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bit.common.result.R;
import com.bit.srb.core.pojo.entity.UserInfo;
import com.bit.srb.core.pojo.query.UserInfoQuery;
import com.bit.srb.core.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author Shell
 * @since 2024-07-11
 */
@RestController
@RequestMapping("/admin/core/userInfo")
@Tag(name = "admin-user-controller", description = "会员管理")
//@CrossOrigin
@Slf4j
public class AdminUserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @Operation(description = "会员查询")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(
            @Parameter (description = "当前页码", required = true)
            @PathVariable Long page,
            @Parameter (description = "每页数量", required = true)
            @PathVariable Long limit,
            @Parameter (description = "查询对象")
            UserInfoQuery userInfoQuery){
        Page<UserInfo> pageParam = new Page<>(page, limit);
        // 默认的分页语句返回IPage对象。提供查询条件和分页条件
        IPage<UserInfo> pageModel = userInfoService.listPage(pageParam, userInfoQuery);

        return R.ok().data("pageModel",pageModel);
    }

    @Operation(description = "会员锁定与解锁")
    @PutMapping("/lock/{id}/{status}")
    public R lock(
            @Parameter (description = "用户ID", required = true)
            @PathVariable Long id,
            @Parameter (description = "更改状态", required = true)
            @PathVariable Integer status){
        userInfoService.lock(id, status);
        return R.ok().message(status == 1? "解锁成功":"锁定成功");
    }

}

