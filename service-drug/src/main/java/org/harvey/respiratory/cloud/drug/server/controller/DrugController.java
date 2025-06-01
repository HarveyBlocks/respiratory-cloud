package org.harvey.respiratory.cloud.drug.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.harvey.respiratory.cloud.api.service.DrugService;
import org.harvey.respiratory.cloud.common.constants.ConstantsInitializer;
import org.harvey.respiratory.cloud.common.constants.ServerConstants;
import org.harvey.respiratory.cloud.common.pojo.entity.Drug;
import org.harvey.respiratory.cloud.common.pojo.vo.NullPlaceholder;
import org.harvey.respiratory.cloud.common.pojo.vo.Result;
import org.harvey.respiratory.cloud.common.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 具体药物
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-14 21:01
 */
@Slf4j
@RestController
@Api(tags = {"具体药物"})
@RequestMapping("/api")
public class DrugController {
    @Resource
    private DrugService drugService;

    @PostMapping("/")
    @ApiOperation("增加某一药品")
    public Result<NullPlaceholder> register(
            @RequestBody @ApiParam(value = "药品", required = true) Drug drug) {
        // 必须要有主管医生权限
        drugService.writeValid(UserHolder.getUser());
        drugService.saveDrug(drug);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除某一药品")
    public Result<NullPlaceholder> del(
            @PathVariable("id") @ApiParam(value = "药品id", required = true) Long drugId) {
        // 必须要有主管医生权限
        drugService.writeValid(UserHolder.getUser());
        drugService.deleteById(drugId);
        return Result.ok();
    }


    @GetMapping("/{id}")
    @ApiOperation("查询药物具体信息")
    public Result<Drug> queryById(
            @PathVariable("id") @ApiParam(value = "药物id", required = true) Long id) {
        // 病人可以查, 医生可以查, 费用可以查吗?
        return Result.success(drugService.queryById(id));
    }

    @GetMapping(value = {"/name/{name}/{page}/{limit}", "/name/{name}/{page}", "/name/{name}"})
    @ApiOperation("用名字模糊查询药物具体信息")
    public Result<List<Drug>> queryByName(
            @PathVariable("name") @ApiParam(value = "药物名", required = true) String name,
            @PathVariable(value = "page", required = false) @ApiParam(value = "页码, 从1开始", defaultValue = "1")
            Integer page,
            @PathVariable(value = "limit", required = false)
            @ApiParam(value = "页长", defaultValue = ServerConstants.DEFAULT_PAGE_SIZE_MSG) Integer limit) {
        // 用药物名模糊查询药物
        return Result.success(drugService.queryByName(name, ConstantsInitializer.initPage(page, limit)));
    }


}
