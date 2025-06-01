package org.harvey.respiratory.cloud.sudr.server.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.api.service.SpecificUsingDrugRecordService;
import org.harvey.respiratory.cloud.common.constants.ConstantsInitializer;
import org.harvey.respiratory.cloud.common.constants.ServerConstants;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.ResourceNotFountException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.DrugDto;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.SpecificUsingDrugRecord;
import org.harvey.respiratory.cloud.common.pojo.vo.NullPlaceholder;
import org.harvey.respiratory.cloud.common.pojo.vo.RangeDate;
import org.harvey.respiratory.cloud.common.pojo.vo.Result;
import org.harvey.respiratory.cloud.common.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;

/**
 * 药物使用
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-14 20:57
 */
@Slf4j
@RestController
@Api(tags = "针对某一问诊的药物具体使用")
@RequestMapping("/api")
public class SpecificUsingDrugController {
    @Resource
    private SpecificUsingDrugRecordService specificUsingDrugRecordService;

    @DeleteMapping("{id}")
    @ApiOperation("删除某一问诊的某一具体用药")
    public Result<NullPlaceholder> del(
            @PathVariable("id") @ApiParam(value = "使用方法id", required = true) Long id) {
        // 由于药留证据, 所以是逻辑删除
        // 当亲visitDoctor的医生可以删除这个具体用药记录, 即使是主管医生/开发者直接过
        SpecificUsingDrugRecord record;


        try {
            record = specificUsingDrugRecordService.queryById(id);
        } catch (ResourceNotFountException e) {
            throw new ResourceNotFountException("不存在的用药id");
        }
        specificUsingDrugRecordService.validOnWrite(UserHolder.getUser(), record.getVisitDoctorId());
        // visitDoctorId的medical provider才有权限删除
        specificUsingDrugRecordService.logicDelete(id);
        return Result.ok();
    }

    @DeleteMapping("{visitDoctorId}/{drugId}")
    @ApiOperation("删除某一问诊的某一具体用药")
    public Result<NullPlaceholder> del(
            @PathVariable("visitDoctorId") @ApiParam(value = "问诊id", required = true) Long visitDoctorId,
            @PathVariable("drugId") @ApiParam(value = "药品id", required = true) Long drugId) {
        // 由于药留证据, 所以是逻辑删除
        // 当亲visitDoctor的医生可以删除这个具体用药记录, 即使是主管医生/开发者直接过
        specificUsingDrugRecordService.validOnWrite(UserHolder.getUser(), visitDoctorId);
        // visitDoctorId的medical provider才有权限删除
        specificUsingDrugRecordService.logicDelete(visitDoctorId, drugId);
        return Result.ok();
    }

    @PutMapping("/")
    @ApiOperation("更新某一问诊的需要用药")
    @ApiResponse(code = 200, message = "新的记录的id")
    public Result<Long> updatePatientDrugHistory(
            @RequestBody SpecificUsingDrugRecord specificUsing) {
        // 更新药品使用
        // 医生可以更新, 用户不行
        // 为了保留证据, 旧信息逻辑删除, 本信息保持不变

        // visitDoctorId的medical provider才有权限
        Long oldVersionId = specificUsing.getId();
        if (oldVersionId == null) {
            throw new BadRequestException("要更新的目标的id不能为null");
        }
        specificUsing.setId(null);
        UserDto user = UserHolder.getUser();
        if (user == null) {
            throw new UnauthorizedException("权限不足, 需要登录");
        }
        return Result.success(specificUsingDrugRecordService.updateRetainTrace(user, oldVersionId, specificUsing));
    }


    @GetMapping("/visit/{id}")
    @ApiOperation(value = "查询抓药信息")
    @ApiResponses({@ApiResponse(code = 200, message = "ok")})
    public Result<List<DrugDto>> queryDrugInVisit(
            @PathVariable("id") @ApiParam(value = "问诊号", required = true) Long visitId) {
        // 存在patient上的关系, 可以查询
        specificUsingDrugRecordService.validOnVisitRead(UserHolder.getUser(), visitId);
        return Result.success(specificUsingDrugRecordService.queryDrugInVisit(visitId));
    }


    @GetMapping(value = {"history/patient/{id}/{name}", "history/patient/{id}"})
    @ApiOperation("查询病人既往用药史")
    public Result<List<DrugDto>> queryPatientDrugHistoryByDrug(
            @PathVariable("id") @ApiParam(value = "病患id", required = true) Long patientId,
            @PathVariable(value = "name", required = false) @ApiParam(value = "药品名") String name) {
        // 病人可以查, 医生可以查
        specificUsingDrugRecordService.validOnPatientRead(UserHolder.getUser(), patientId);
        return Result.success(specificUsingDrugRecordService.queryHistoryDrugUsingByName(patientId, name));
    }


    @GetMapping(value = {"history/patient/{id}/{start}/{end}", "history/patient/{id}/{start}", "history/patient/{id}"})
    @ApiOperation("查询病人既往用药史")
    public Result<List<DrugDto>> queryPatientDrugHistoryByDate(
            @PathVariable("id") @ApiParam(value = "病患id", required = true) Long patientId,
            @PathVariable(value = "start", required = false)
            @ApiParam(value = ServerConstants.DEFAULT_DATE_FORMAT_STRING + ", 0补前") String startDate,
            @PathVariable(value = "end", required = false)
            @ApiParam(value = ServerConstants.DEFAULT_DATE_FORMAT_STRING + ", 0补前", defaultValue = "当前日期")
            String endDate) {
        specificUsingDrugRecordService.validOnPatientRead(UserHolder.getUser(), patientId);
        // 病人可以查自己登记过的病人, 医生可以查自己问诊的病人, 主管医生可以查任何病人
        RangeDate rangeDate;
        try {
            rangeDate = ConstantsInitializer.initDateRange(startDate, endDate);
        } catch (ParseException e) {
            throw new BadRequestException("时间格式错误, 应该为: " + ServerConstants.DEFAULT_DATE_FORMAT_STRING);
        }
        return Result.success(specificUsingDrugRecordService.queryHistoryDrugUsingByDate(patientId, rangeDate));
    }


}
