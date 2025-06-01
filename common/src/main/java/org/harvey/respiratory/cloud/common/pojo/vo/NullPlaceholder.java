package org.harvey.respiratory.cloud.common.pojo.vo;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;

/**
 * 就是空
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-01 18:44
 */
@ApiModel(description = "啥都没有,就是null,只是个记号")
public class NullPlaceholder implements Serializable {
    private NullPlaceholder() {
    }
}
