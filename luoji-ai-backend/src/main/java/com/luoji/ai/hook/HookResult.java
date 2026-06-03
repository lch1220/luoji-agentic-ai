/*
 * HookResult - 钩子执行结果
 *
 * @author luoji
 * @date 2026-05-28
 * @description 钩子执行结果对象，包含执行是否成功、消息、可选的数据修改和标记
 */
package com.luoji.ai.hook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HookResult {
    private boolean success;                  // 是否成功
    private String message;                  // 结果消息
    private Map<String, Object> data;        // 返回数据
    private boolean modified;                // 是否修改了原始数据
}
