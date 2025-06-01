package org.harvey.respiratory.cloud.di.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.api.service.DoctorInterviewService;
import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;
import org.harvey.respiratory.cloud.common.pojo.dto.InterviewDto;
import org.harvey.respiratory.cloud.common.pojo.dto.UserDto;
import org.harvey.respiratory.cloud.common.pojo.vo.NullPlaceholder;
import org.harvey.respiratory.cloud.common.pojo.vo.Result;
import org.harvey.respiratory.cloud.common.utils.UserHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 医生问诊
 * 由于涉及好多其他模块的工作, 所以单独提出来
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-14 13:27
 */
@Slf4j
@RestController
@Api(tags = {"医生问诊"})
@RequestMapping("/api")
public class DoctorInterviewController {
    @Resource
    private DoctorInterviewService doctorInterviewService;

    @PostMapping("/execute")
    @ApiOperation(
            "问诊. 1. 更新就诊信息 2. 生成多条费用单 3. 计算费用价格 4. 插入多条具体药物使用 5. 插入多个症状的一系列操作具有原子性")
    public Result<NullPlaceholder> interview(
            @RequestBody @ApiParam("interview") InterviewDto interviewDto) {
        UserDto currentUser = UserHolder.getUser();
        if (currentUser == null) {
            throw new UnauthorizedException("未登录, 无权限");
        }
        String identityCardId = currentUser.getIdentityCardId();
        if (identityCardId == null || identityCardId.isEmpty()) {
            throw new UnauthorizedException("未实名, 无权限");
        }
        doctorInterviewService.interview(interviewDto, currentUser);
        return Result.ok();
    }

}
