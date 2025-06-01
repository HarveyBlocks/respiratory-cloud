package org.harvey.respiratory.cloud.mpj.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.api.service.MedicalProviderJobService;
import org.harvey.respiratory.cloud.common.constants.ConstantsInitializer;
import org.harvey.respiratory.cloud.common.constants.RoleConstant;
import org.harvey.respiratory.cloud.common.constants.ServerConstants;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.MedicalProviderJob;
import org.harvey.respiratory.cloud.common.pojo.vo.NullPlaceholder;
import org.harvey.respiratory.cloud.common.pojo.vo.Result;
import org.harvey.respiratory.cloud.common.utils.RoleUtil;
import org.harvey.respiratory.cloud.common.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 医疗提供者和医生职务
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-13 23:20
 */
@Slf4j
@RestController
@Api(tags = {"医生职务"})
@RequestMapping("/api")
public class MedicalProviderJobController {
    @Resource
    private MedicalProviderJobService medicalProviderJobService;

    @PostMapping("/register")
    @ApiOperation("登记医生职业信息")
    @ApiResponse(code = 200, message = "回复新增后的职业id")
    public Result<Integer> register(
            @RequestBody @ApiParam("新增时, 除id, 和医保相关外, 字段都不得为null.") MedicalProviderJob job) {
        // 当前用户必须是主管/开发者
        UserDto user = UserHolder.getUser();
        if (user == null) {
            throw new UnauthorizedException("登录后可使用");
        }
        RoleUtil.validOnRole(user.getRole(), RoleConstant.MEDICAL_PROVIDER_UPDATE);
        return Result.success(medicalProviderJobService.register(job));
    }


    @PutMapping("/")
    @ApiOperation("更新医生职业信息")
    public Result<NullPlaceholder> update(
            @RequestBody @ApiParam("更新字段可以为null, 表示保留原有; 也可以不为null, 即使字段值没有发生变化")
            MedicalProviderJob job) {
        // 当前用户必须是主管/开发者
        UserDto user = UserHolder.getUser();
        if (user == null) {
            throw new UnauthorizedException("登录后可使用");
        }
        RoleUtil.validOnRole(user.getRole(), RoleConstant.MEDICAL_PROVIDER_UPDATE);
        medicalProviderJobService.update(job);
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
        medicalProviderJobService.delete(id);
        return Result.ok();
    }


    @GetMapping("/self")
    @ApiOperation("查询医疗提供者自己的职业信息")
    public Result<MedicalProviderJob> query() {
        // 任何医疗提供者可执行
        UserDto user = UserHolder.getUser();
        if (user == null) {
            throw new UnauthorizedException("登录后可使用");
        }
        RoleUtil.validOnRole(user.getRole(), RoleConstant.MEDICAL_PROVIDER_READ);
        return Result.success(medicalProviderJobService.querySelf(user.getId()));
    }


    @GetMapping({"/all/{page}/{limit}", "/all/{page}/", "/all"})
    @ApiOperation("查询医生职业信息")
    public Result<List<MedicalProviderJob>> query(
            @PathVariable(value = "page", required = false) @ApiParam(value = "页码, 从1开始", defaultValue = "1")
            Integer page,
            @PathVariable(value = "limit", required = false)
            @ApiParam(value = "页面长度", defaultValue = ServerConstants.DEFAULT_PAGE_SIZE_MSG) Integer limit) {
        return Result.success(medicalProviderJobService.queryAny(ConstantsInitializer.initPage(page, limit)));
    }


    @GetMapping("/one/{id}")
    @ApiOperation("查询医生职业信息")
    public Result<MedicalProviderJob> query(
            @PathVariable(value = "id") @ApiParam(value = "医生职业的id", required = true) Integer job) {
        return Result.success(medicalProviderJobService.queryById(job));
    }

    @GetMapping("/name/{name}")
    @ApiOperation("依据名字查询医生职业信息")
    public Result<List<MedicalProviderJob>> queryByName(
            @PathVariable(value = "name") @ApiParam(value = "依据姓名模糊查询医生职业")
            String name) {
        if (name.isEmpty()) {
            throw new BadRequestException("name can not be empty");
        }
        return Result.success(medicalProviderJobService.queryByName(name));
    }
}
