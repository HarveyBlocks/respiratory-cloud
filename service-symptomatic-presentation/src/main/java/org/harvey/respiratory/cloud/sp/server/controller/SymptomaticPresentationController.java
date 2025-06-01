package org.harvey.respiratory.cloud.sp.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.api.service.SymptomaticPresentationService;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.SymptomaticPresentation;
import org.harvey.respiratory.cloud.common.pojo.vo.NullPlaceholder;
import org.harvey.respiratory.cloud.common.pojo.vo.Result;
import org.harvey.respiratory.cloud.common.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 症状
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-14 21:57
 */
@Slf4j
@RestController
@Api(tags = "症状")
@RequestMapping("/api")
public class SymptomaticPresentationController {
    @Resource
    private SymptomaticPresentationService symptomaticPresentationService;

    @DeleteMapping("/{id}")
    @ApiOperation("删除某一问诊的某一具体用药")
    public Result<NullPlaceholder> del(
            @PathVariable("id") @ApiParam(value = "症状id", required = true) Long id) {
        // 由于症状留证据, 所以是逻辑删除
        // 依据id删除
        // 只有这次问诊的执行的医生有权限删除
        UserDto user = UserHolder.getUser();
        if (user == null) {
            throw new UnauthorizedException("未登录, 无权限");
        }
        String currentUserIdentityCardId = user.getIdentityCardId();
        symptomaticPresentationService.logicDelete(id, currentUserIdentityCardId);
        return Result.ok();
    }


    @PutMapping("/")
    @ApiOperation("更新症状")
    @ApiResponse(code = 200, message = "对症状更新后的新ID")
    public Result<Long> updatePatientSymptomaticPresentation(
            @RequestBody @ApiParam("symptomaticPresentation") SymptomaticPresentation symptomaticPresentation) {
        // 更新
        UserDto user = UserHolder.getUser();
        if (user == null) {
            throw new UnauthorizedException("未登录, 无权限");
        }
        String currentUserIdentityCardId = user.getIdentityCardId();
        Long oldVersionId = symptomaticPresentation.getId();
        if (oldVersionId == null) {
            throw new BadRequestException("缺少更新的目标id");
        }
        symptomaticPresentation.setId(null);
        return Result.success(symptomaticPresentationService.updateRetainTrace(currentUserIdentityCardId, oldVersionId,
                symptomaticPresentation
        ));
    }

    @GetMapping("visit/{id}")
    @ApiOperation("查询该问诊的有关症状")
    public Result<List<SymptomaticPresentation>> queryVisitDrug(
            @PathVariable("id") @ApiParam(value = "问诊号", required = true) Long visitId) {
        // 查询未逻辑删除的
        return Result.success(symptomaticPresentationService.selectByVisitId(visitId));
    }


}
