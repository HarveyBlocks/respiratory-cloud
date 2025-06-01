package org.harvey.respiratory.cloud.common.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 滚动分页查询
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 14:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "滚动分页查询的封装")
public class ScrollResult<T> implements Serializable {
    @ApiModelProperty("分页结果")
    private List<T> list;
    @ApiModelProperty("上一次查询的最后一个实体的标识, 下一次请求就再把这个标识传给后端")
    private Long lastId;
    @ApiModelProperty(value = "偏移量, 下一次请求就再把这个偏移量传给后端",
            notes = "有时候,同一标识可能存在多个实体, 那么就需要这个偏移量,告诉后端:" +
                    "在这个标识之下, 有几个实体是已经被查到了的, 有几个实体是还未查到的")
    private Integer offset;
}
