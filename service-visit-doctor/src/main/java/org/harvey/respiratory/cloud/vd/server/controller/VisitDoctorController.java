package org.harvey.respiratory.cloud.vd.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.api.service.VisitDoctorService;
import org.harvey.respiratory.cloud.common.constants.ConstantsInitializer;
import org.harvey.respiratory.cloud.common.constants.ServerConstants;
import org.harvey.respiratory.cloud.common.exception.BadRequestException;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.TakeVisitNumberDto;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.entity.VisitDoctor;
import org.harvey.respiratory.cloud.common.pojo.vo.RangeDate;
import org.harvey.respiratory.cloud.common.pojo.vo.Result;
import org.harvey.respiratory.cloud.common.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;

/**
 * 医生进行问诊
 * <pre>{@code
 * 药->特殊用药 ↘
 *      ↓     问诊 ->费用->医保付费
 * (既往用药)   ↗ ↖
 * 症状   疾病->家族史
 * }</pre>
 * 8. 问诊visit_doctor
 * 9. 医保付费
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-12 23:40
 */
@Slf4j
@RestController
@Api(tags = "就诊")
@RequestMapping("/api")
public class VisitDoctorController {
    @Resource
    private VisitDoctorService visitDoctorService;

    @PostMapping("/take-number")
    @ApiOperation("取号")
    public Result<Long> createVisitDoctorId(
            @RequestBody @ApiParam("takeVisitNumber") TakeVisitNumberDto takeVisitNumberDto) {
        // 插入取号信息
        // 返回取号id
        Long patientId = takeVisitNumberDto.getPatientId();
        if (patientId == null) {
            throw new BadRequestException("patient id needed");
        }
        Long medicalProviderId = takeVisitNumberDto.getMedicalProviderId();
        if (medicalProviderId == null) {
            throw new BadRequestException("medical provider id needed");
        }
        return Result.success(visitDoctorService.createVisitDoctorId(patientId, medicalProviderId));
    }


    @GetMapping("/{id}")
    @ApiOperation("获取就诊信息")
    public Result<VisitDoctor> getById(@PathVariable("id") @ApiParam("就诊表的id") Long id) {
        // 药物医生能依据具体就诊号查询到需要的具体用药信息, 其余查不到, 也就是说, 这个方法药物医生是查不到任何东西的
        // 普通医生要进行校验
        // 主管医生可以进行查询
        return Result.success(visitDoctorService.querySimplyById(id));
    }


    @GetMapping(value = {"/doctor/role/{start-date}/{end-date}/{page}/{limit}",
            "/doctor/role/{start-date}/{end-date}/{page}", "/doctor/role/{start-date}/{end-date}"})
    @ApiOperation("查询当前医生相关就诊信息")
    public Result<List<VisitDoctor>> doctorQuery(
            @PathVariable(value = "start-date")
            @ApiParam(value = ServerConstants.DEFAULT_DATE_FORMAT_STRING + ", 0补前", required = true) String startDate,
            @PathVariable(value = "end-date")
            @ApiParam(value = ServerConstants.DEFAULT_DATE_FORMAT_STRING + ", 0补前", required = true) String endDate,
            @PathVariable(value = "page", required = false) @ApiParam(value = "页码, 从1开始", defaultValue = "1")
            Integer page,
            @PathVariable(value = "limit", required = false)
            @ApiParam(value = "页面长度", defaultValue = ServerConstants.DEFAULT_PAGE_SIZE_MSG) Integer limit) {
        UserDto user = UserHolder.getUser();
        if (user == null) {
            throw new UnauthorizedException("登录后可访问");
        }
        String identityCardId = user.getIdentityCardId();
        if (identityCardId == null) {
            throw new UnauthorizedException("不实名无权限");
        }
        RangeDate rangeDate;
        try {
            rangeDate = ConstantsInitializer.initDateRange(startDate, endDate);
        } catch (ParseException e) {
            throw new BadRequestException("错误的日期格式", e);
        }

        return Result.success(
                visitDoctorService.doctorQuery(identityCardId, rangeDate, ConstantsInitializer.initPage(page, limit)));
    }

    @GetMapping(value = {"/user/any/{start-date}/{end-date}/{page}/{limit}",
            "/user/any/{start-date}/{end-date}/{page}", "/user/any/{start-date}/{end-date}",})
    @ApiOperation("查询当前用户持有患者相关就诊信息")
    public Result<List<VisitDoctor>> userQuery(
            @PathVariable(value = "start-date") @ApiParam("YYYY-MM-DD, 0补前") String startDate,
            @PathVariable(value = "end-date") @ApiParam("YYYY-MM-DD, 0补前") String endDate,
            @PathVariable(value = "page", required = false) @ApiParam(value = "页码, 从1开始", defaultValue = "1")
            Integer page,
            @PathVariable(value = "limit", required = false)
            @ApiParam(value = "页面长度", defaultValue = ServerConstants.DEFAULT_PAGE_SIZE_MSG) Integer limit) {
        // 依据权限, 如果是患者查询, 则患者相关
        // 如果是医生查询, 就是医生相关
        Long currentUserId = UserHolder.currentUserId();
        RangeDate rangeDate;
        try {
            rangeDate = ConstantsInitializer.initDateRange(startDate, endDate);
        } catch (ParseException e) {
            throw new BadRequestException("错误的日期格式", e);
        }
        return Result.success(
                visitDoctorService.patientQuery(currentUserId, rangeDate, ConstantsInitializer.initPage(page, limit)));
    }


}
