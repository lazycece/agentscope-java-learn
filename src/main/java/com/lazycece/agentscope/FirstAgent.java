package com.lazycece.agentscope;

/**
 * @author lazycece
 * @date 2026/9/6
 */

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.UserMessage;
import io.agentscope.harness.agent.HarnessAgent;
import java.nio.file.Paths;

public class FirstAgent {

    public static void main(String[] args) {
        HarnessAgent agent = HarnessAgent.builder()
                .name("assistant")
                .sysPrompt("You are a helpful AI assistant.")
                // ModelRegistry resolves the string and reads the matching
                // API-key env var (e.g. OPENAI_API_KEY or DEEPSEEK_API_KEY)
                // automatically.
                // Examples: "openai:gpt-4.1", "openai:o3",
                // "deepseek:deepseek-v4-flash", "dashscope:qwen-plus",
                // "anthropic:claude-sonnet-4-7", "ollama:llama3"
                .model("deepseek:deepseek-v4-flash")
                // Or pass a ChatModel object directly:
                // .model(OpenAIChatModel.builder().model("gpt-4.1").build())
                .workspace(Paths.get(".agentscope/workspace"))
                .build();

        RuntimeContext ctx = RuntimeContext.builder()
                .sessionId("123").userId("alice").build();

        // Blocking call
        agent.call(new UserMessage("今天天气怎么样"), ctx).block();

        // Or stream events for real-time UI rendering
        agent.streamEvents(new UserMessage("agentscope的定位是什么？"), ctx)
                .doOnNext(event -> {
                    switch (event.getType()) {
                        case TEXT_BLOCK_DELTA -> System.out.print(
                                ((io.agentscope.core.event.TextBlockDeltaEvent) event).getDelta());
                        case TOOL_CALL_START -> System.out.println(
                                "\n[tool] "
                                        + ((io.agentscope.core.event.ToolCallStartEvent) event).getToolCallName());
                        default -> {
                        }
                    }
                })
                .blockLast();
    }
}