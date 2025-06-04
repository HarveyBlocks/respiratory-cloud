package org.harvey.respiratory.cloud.common.pojo.follow.enums;


import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * (轻度, 中度, 重度)
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-04 15:30
 */
public enum SeverityLevel implements IEnum<String> {
 LEVEL_1("轻度"), LEVEL_2("中度"), LEVEL_3("重度"),LEVEL_4("极重度");
 private final String description;

 SeverityLevel(String description) {
  this.description = description;
 }

 @Override
 public String getValue() {
  return description;
 }
}
