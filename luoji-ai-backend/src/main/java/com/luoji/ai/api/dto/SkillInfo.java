/*
 * SkillInfo - 技能信息数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 技能元数据信息对象，包含技能ID、名称、描述、参数定义等
 */
package com.luoji.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillInfo {
    private String id;               // 技能唯一标识
    private String name;             // 技能名称
    private String description;      // 技能描述
    private String category;         // 技能分类
    private String version;          // 版本号
    private List<ParameterInfo> parameters;  // 参数定义列表
    private String returnType;       // 返回类型
    private boolean isAsync;         // 是否异步执行

    /**
     * 技能参数信息内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParameterInfo {
        private String name;          // 参数名
        private String type;          // 参数类型
        private String description;   // 参数描述
        private boolean required;     // 是否必填
        private Object defaultValue;  // 默认值
    }
}
