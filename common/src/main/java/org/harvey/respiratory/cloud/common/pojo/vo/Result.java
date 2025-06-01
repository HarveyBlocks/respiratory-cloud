package org.harvey.respiratory.cloud.common.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回结果的封装
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 14:11
 */
@Data
@AllArgsConstructor
@ApiModel(description = "返回结果封装")
public class Result<T> implements Serializable {

    @ApiModelProperty(
            value = "是否成功",
            notes = "true,携带data;false,携带errorMsg")
    private Integer code;
    @ApiModelProperty(
            value = "错误信息", name = "error message",
            example = "发生了一个异常!")
    private String msg;
    @ApiModelProperty(
            value = "成功后响应的数据主体",
            notes = "成功后返回前端需要的数据.如果是列表, 则会携带total")
    private T data;

    public Result() {
        this.code = 200;
    }

    public Result(T data) {
        this.code = 200;
        this.data = data;
    }

    public Result(T data, String msg) {
        this.code = 200;
        this.data = data;
        this.msg = msg;
    }

    public Result(Integer code, String errorMsg) {
        this.code = code;
        this.msg = errorMsg;
    }

    public static Result<NullPlaceholder> ok() {
        return new Result<>(200, null, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, null, data);
    }

    public static Result<NullPlaceholder> error(int code, String message) {
        return new Result<>(code, message);
    }
}
