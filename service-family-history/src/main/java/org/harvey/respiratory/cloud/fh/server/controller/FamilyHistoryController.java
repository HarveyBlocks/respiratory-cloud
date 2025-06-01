package org.harvey.respiratory.cloud.fh.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.harvey.respiratory.cloud.api.service.FamilyHistoryService;
import org.harvey.respiratory.cloud.api.service.FamilyRelationshipEntityService;
import org.harvey.respiratory.cloud.common.constants.ConstantsInitializer;
import org.harvey.respiratory.cloud.common.constants.ServerConstants;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.pojo.entity.FamilyHistory;
import org.harvey.respiratory.cloud.common.pojo.entity.FamilyRelationshipEntity;
import org.harvey.respiratory.cloud.common.pojo.vo.Result;
import org.harvey.respiratory.cloud.common.utils.UserHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 家族史接口
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-14 22:34
 */
@Slf4j
@RestController
@Api(tags = {"家庭病史"})
@RequestMapping("/api")
public class FamilyHistoryController {
    @Resource
    private FamilyHistoryService familyHistoryService;
    @DubboReference
    private FamilyRelationshipEntityService familyRelationshipEntityService;

    @PostMapping(value = {"/register"})
    @ApiOperation("家族病史, 用疾病名模糊查询")
    public Result<Long> register(@RequestBody @ApiParam("新增的entity") FamilyHistory familyHistory) {
        if (familyHistory == null) {
            throw new BadRequestException("need family history entity");
        }
        familyHistoryService.validRoleToRegister(UserHolder.getUser(), familyHistory.getPatientId());
        // 1. 验证user和patient存在依赖关系
        // 2. 添加
        return Result.success(familyHistoryService.register(familyHistory));
    }

    @GetMapping(value = {"/{patientId}/{page}/{limit}", "/{patientId/{page}/", "/{patientId}"})
    @ApiOperation("家族病史, 用patientId糊查询")
    public Result<List<FamilyHistory>> queryFamilyHistory(
            @PathVariable("patientId") @ApiParam(value = "病患id", required = true) Long patientId,
            @PathVariable(value = "page", required = false) @ApiParam(value = "页码, 从1开始", defaultValue = "1")
            Integer page,
            @PathVariable(value = "limit", required = false)
            @ApiParam(value = "页长", defaultValue = ServerConstants.DEFAULT_PAGE_SIZE_MSG) Integer limit) {
        // 查询某patientId的
        familyHistoryService.validRoleToRegister(UserHolder.getUser(), patientId);
        return Result.success(
                familyHistoryService.queryByPatient(patientId, ConstantsInitializer.initPage(page, limit)));
    }

    @GetMapping(value = {"/disease-name/{patientId}/{diseaseName}/{page}/{limit}",
            "/disease-name/{patientId}/{diseaseName}/{page}/", "/disease-name/{patientId}/{diseaseName}"})
    @ApiOperation("家族病史, 用疾病名模糊查询")
    public Result<List<FamilyHistory>> queryFamilyHistoryByName(
            @PathVariable("patientId") @ApiParam(value = "病患名", required = true) Long patientId,
            @PathVariable("diseaseName") @ApiParam(value = "疾病名模糊查询", required = true) String diseaseName,
            @PathVariable(value = "page", required = false) @ApiParam(value = "页码, 从1开始", defaultValue = "1")
            Integer page,
            @PathVariable(value = "limit", required = false)
            @ApiParam(value = "页长", defaultValue = ServerConstants.DEFAULT_PAGE_SIZE_MSG) Integer limit) {
        // 查询某patientId的, 用疾病名模糊查询
        familyHistoryService.validRoleToRegister(UserHolder.getUser(), patientId);
        return Result.success(familyHistoryService.queryByDiseaseName(patientId, diseaseName,
                ConstantsInitializer.initPage(page, limit)
        ));
    }

    @GetMapping(value = {"/disease-id/{patientId}/{diseaseId}/{page}/{limit}",
            "/disease-id/{patientId}/{diseaseId}/{page}/", "/disease-id/{patientId}/{diseaseId}"})
    @ApiOperation("家族病史, 用疾病id查询")
    public Result<List<FamilyHistory>> queryFamilyHistoryByDiseaseId(
            @PathVariable("patientId") @ApiParam(value = "病患名", required = true) Long patientId,
            @PathVariable("diseaseId") @ApiParam(value = "疾病id", required = true) Integer diseaseId,
            @PathVariable(value = "page", required = false) @ApiParam(value = "页码, 从1开始", defaultValue = "1")
            Integer page,
            @PathVariable(value = "limit", required = false)
            @ApiParam(value = "页长", defaultValue = ServerConstants.DEFAULT_PAGE_SIZE_MSG) Integer limit) {
        // 查询某patientId的, 用疾病id查询
        familyHistoryService.validRoleToRegister(UserHolder.getUser(), patientId);
        return Result.success(
                familyHistoryService.queryByDisease(patientId, diseaseId, ConstantsInitializer.initPage(page, limit)));
    }

    @GetMapping(value = {"/relationship/{patientId}/{relationship}/{page}/{limit}",
            "/relationship/{patientId}/{relationship}/{page}/", "/relationship/{patientId}/{relationship}"})
    @ApiOperation("家族病史, 用成员关系查询")
    public Result<List<FamilyHistory>> queryFamilyHistoryByRelationship(
            @PathVariable("patientId") @ApiParam(value = "病患名", required = true) Long patientId,
            @PathVariable("relationship")
            @ApiParam(value = "病患关系数组的id序列.用逗号分割", example = "`1,2,3`", required = true)
            String relationships,
            @PathVariable(value = "page", required = false) @ApiParam(value = "页码, 从1开始", defaultValue = "1")
            Integer page,
            @PathVariable(value = "limit", required = false)
            @ApiParam(value = "页长", defaultValue = ServerConstants.DEFAULT_PAGE_SIZE_MSG) Integer limit) {
        // 查询某patientId的, 用家族成员学习查询
        familyHistoryService.validRoleToRegister(UserHolder.getUser(), patientId);
        List<Integer> relationshipIds = splitRelationshipIds(relationships);
        return Result.success(familyHistoryService.queryByRelationship(patientId, relationshipIds,
                ConstantsInitializer.initPage(page, limit)
        ));
    }

    private List<Integer> splitRelationshipIds(String relationshipRaw) {
        if (relationshipRaw == null) {
            return null;
        }
        if (relationshipRaw.isEmpty()) {
            return Collections.emptyList();
        }
        String[] split = StringUtils.split(relationshipRaw, ",");
        if (split == null) {
            throw new BadRequestException("bad relationship");
        }
        return Arrays.stream(split).map(v -> {
            try {
                return Integer.parseInt(v);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Not a id of relationship: " + v, e);
            }
        }).collect(Collectors.toList());
    }

    @GetMapping(value = {"relationship/enum/{page}/{limit}"})
    @ApiOperation("家族病史, 用成员关系查询")
    public Result<List<FamilyRelationshipEntity>> queryFamilyRelationship(
            @PathVariable(value = "page", required = false) @ApiParam(value = "页码, 从1开始", defaultValue = "1")
            Integer page,
            @PathVariable(value = "limit", required = false)
            @ApiParam(value = "页长", defaultValue = ServerConstants.DEFAULT_PAGE_SIZE_MSG) Integer limit) {
        return Result.success(familyRelationshipEntityService.query(ConstantsInitializer.initPage(page, limit)));
    }
}
