package org.harvey.respiratory.cloud.mpd.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.api.service.MedicalProviderDepartmentService;
import org.harvey.respiratory.cloud.common.constants.ConstantsInitializer;
import org.harvey.respiratory.cloud.common.constants.RoleConstant;
import org.harvey.respiratory.cloud.common.constants.ServerConstants;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProviderDepartment;
import org.harvey.respiratory.cloud.common.pojo.vo.NullPlaceholder;
import org.harvey.respiratory.cloud.common.pojo.vo.Result;
import org.harvey.respiratory.cloud.common.utils.RoleUtil;
import org.harvey.respiratory.cloud.common.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 医疗提供者和医院科室
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 23:20
 */
@Slf4j
@RestController
@Api(tags = {"医院科室"})
@RequestMapping("/api")
public class MedicalProviderDepartmentController {
    @Resource
    private MedicalProviderDepartmentService medicalProviderDepartmentService;


    @PostMapping("/register")
    @ApiOperation("登记医院科室信息")
    public Result<Integer> register(
            @RequestBody @ApiParam("新增时, 除id, 和医保相关外, 字段都不得为null.")
            MedicalProviderDepartment department) {
        // 当前用户必须是主管/开发者
        UserDto user = UserHolder.getUser();
        if (user == null) {
            throw new UnauthorizedException("登录后可使用");
        }
        RoleUtil.validOnRole(user.getRole(), RoleConstant.MEDICAL_PROVIDER_UPDATE);
        return Result.success(medicalProviderDepartmentService.register(department));
    }


    @PutMapping("/")
    @ApiOperation("更新医院科室信息")
    public Result<NullPlaceholder> update(
            @RequestBody @ApiParam("更新字段可以为null, 表示保留原有; 也可以不为null, 即使字段值没有发生变化")
            MedicalProviderDepartment department) {
        // 当前用户必须是主管/开发者
        UserDto user = UserHolder.getUser();
        if (user == null) {
            throw new UnauthorizedException("登录后可使用");
        }
        RoleUtil.validOnRole(user.getRole(), RoleConstant.MEDICAL_PROVIDER_UPDATE);
        medicalProviderDepartmentService.update(department);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除医疗提供者信息")
    public Result<NullPlaceholder> del(@PathVariable("id") @ApiParam("医疗提供者 id") Long id) {
        // 当前用户必须是主管/开发者
        UserDto user = UserHolder.getUser();
        if (user == null) {
            throw new UnauthorizedException("登录后可使用");
        }
        RoleUtil.validOnRole(user.getRole(), RoleConstant.MEDICAL_PROVIDER_UPDATE);
        medicalProviderDepartmentService.delete(id);
        return Result.ok();
    }


    @GetMapping("/self")
    @ApiOperation("查询医疗提供者自己的科室信息")
    public Result<MedicalProviderDepartment> query() {
        // 任何医疗提供者可执行
        UserDto user = UserHolder.getUser();
        if (user == null) {
            throw new UnauthorizedException("登录后可使用");
        }
        RoleUtil.validOnRole(user.getRole(), RoleConstant.MEDICAL_PROVIDER_READ);
        return Result.success(medicalProviderDepartmentService.querySelf(user.getId()));
    }


    @GetMapping({"/all/{page}/{limit}", "/all/{page}/", "/all"})
    @ApiOperation("查询医院科室信息")
    public Result<List<MedicalProviderDepartment>> query(
            @PathVariable(value = "page", required = false) @ApiParam(value = "页码, 从1开始", defaultValue = "1")
            Integer page,
            @PathVariable(value = "limit", required = false)
            @ApiParam(value = "页面长度", defaultValue = ServerConstants.DEFAULT_PAGE_SIZE_MSG) Integer limit) {
        return Result.success(medicalProviderDepartmentService.queryAny(ConstantsInitializer.initPage(page, limit)));
    }


    @GetMapping("/one/{id}")
    @ApiOperation("查询医院科室信息")
    public Result<MedicalProviderDepartment> query(
            @PathVariable(value = "id") @ApiParam(value = "医院科室的id", required = true) Integer department) {
        return Result.success(medicalProviderDepartmentService.queryById(department));
    }

    @GetMapping("/name/{name}")
    @ApiOperation("依据名字查询医院科室信息")
    public Result<List<MedicalProviderDepartment>> queryByName(
            @PathVariable(value = "name") @ApiParam(value = "依据姓名模糊查询医院科室")
            String name) {
        if (name.isEmpty()) {
            throw new BadRequestException("name can not be empty");
        }
        return Result.success(medicalProviderDepartmentService.queryByName(name));
    }
}
