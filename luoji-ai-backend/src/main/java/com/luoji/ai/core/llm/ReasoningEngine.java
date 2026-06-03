/*
 * ReasoningEngine - 推理引擎
 *
 * @author luoji
 * @date 2026-05-28
 * @description 推理引擎，实现多种推理策略包括链式思考(CoT)、思维树(ToT)、
 *              ReAct和反思(Reflection)，用于提升AI的问题解决能力
 */
package com.luoji.ai.core.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReasoningEngine {

    private final LlmClient llmClient;

    /**
     * 推理策略枚举
     */
    public enum ReasoningType {
        CHAIN_OF_THOUGHT,  // 链式思考
        TREE_OF_THOUGHTS,  // 思维树
        REACT,             // 推理+行动
        REFLECTION         // 反思
    }

    /**
     * 链式思考推理
     * 引导模型逐步思考问题
     * @param problem 问题描述
     * @return 推理结果
     */
    public Mono<String> reasonWithCoT(String problem) {
        log.debug("Performing Chain-of-Thought reasoning for: {}", problem);
        String prompt = """
                Think through this problem step by step:
                %s

                Please explain your reasoning process:
                """.formatted(problem);

        return llmClient.generate(prompt);
    }

    /**
     * 思维树推理
     * 探索多个可能的方法并评估
     * @param problem 问题描述
     * @param numBranches 分支数量
     * @return 推理结果
     */
    public Mono<String> reasonWithToT(String problem, int numBranches) {
        log.debug("Performing Tree-of-Thoughts reasoning with {} branches for: {}", numBranches, problem);
        String prompt = """
                Explore multiple possible approaches to solve this problem:

                Problem: %s

                For each approach:
                1. Explain the approach
                2. Analyze pros and cons
                3. Evaluate feasibility

                Finally, recommend the best approach with justification.
                """.formatted(problem);

        return llmClient.generate(prompt);
    }

    /**
     * ReAct推理
     * 结合推理和工具使用的框架
     * @param problem 问题描述
     * @param availableTools 可用工具列表
     * @return 推理结果
     */
    public Mono<String> reasonWithReAct(String problem, List<String> availableTools) {
        log.debug("Performing ReAct reasoning for: {}", problem);
        String toolsList = String.join(", ", availableTools);
        String prompt = """
                Solve this problem using the ReAct (Reason + Act) framework:

                Problem: %s

                Available tools: %s

                For each step:
                1. Thought: What are you thinking?
                2. Action: Which tool are you using (or "finish" if done)?
                3. Observation: What did you observe?

                Continue until you reach a solution.
                """.formatted(problem, toolsList);

        return llmClient.generate(prompt);
    }

    /**
     * 反思
     * 对响应进行质量评估和改进建议
     * @param response 待反思的响应
     * @return 反思结果
     */
    public Mono<String> reflect(String response) {
        log.debug("Performing reflection on response");
        String prompt = """
                Reflect on the following response and identify any issues or areas for improvement:

                Response: %s

                Provide feedback on:
                1. Accuracy
                2. Completeness
                3. Clarity
                4. Any potential errors
                """.formatted(response);

        return llmClient.generate(prompt);
    }

    /**
     * 根据指定类型执行推理
     * @param problem 问题描述
     * @param type 推理策略类型
     * @param context 上下文信息
     * @return 推理结果
     */
    public Mono<String> reason(String problem, ReasoningType type, List<String> context) {
        return switch (type) {
            case CHAIN_OF_THOUGHT -> reasonWithCoT(problem);
            case TREE_OF_THOUGHTS -> reasonWithToT(problem, 3);
            case REACT -> reasonWithReAct(problem, context);
            case REFLECTION -> reflect(problem);
        };
    }
}
