/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.assistant.model;

import com.anthropic.bedrock.backends.BedrockBackend;
import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.core.Timeout;
import com.anthropic.core.http.StreamResponse;
import com.anthropic.helpers.MessageAccumulator;
import com.anthropic.models.messages.CacheControlEphemeral;
import com.anthropic.models.messages.ContentBlock;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.anthropic.models.messages.RawMessageStreamEvent;
import com.anthropic.models.messages.TextBlockParam;
import com.anthropic.models.messages.Usage;
import ish.oncourse.server.assistant.AssistantConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import java.time.Duration;
import java.util.Collections;

/**
 * Claude on Amazon Bedrock (bedrock-runtime) through the Anthropic Java SDK. The client is created on the first
 * message, so the server starts without touching AWS. The reply is streamed, so the read timeout limits the wait
 * between chunks rather than the whole answer, and returned whole.
 */
public class BedrockModelProvider implements AssistantModelProvider, AutoCloseable {

    private static final Logger logger = LogManager.getLogger();

    private final AssistantConfig config;
    private volatile AnthropicClient client;

    public BedrockModelProvider(AssistantConfig config) {
        this.config = config;
    }

    @Override
    public ModelReply reply(ModelRequest request) throws AssistantModelException {
        long start = System.nanoTime();
        Message message;
        try {
            MessageAccumulator accumulator = MessageAccumulator.create();
            try (StreamResponse<RawMessageStreamEvent> stream = getClient().messages().createStreaming(buildParams(config, request))) {
                stream.stream().forEach(accumulator::accumulate);
            }
            message = accumulator.message();
        } catch (RuntimeException e) {
            // AnthropicException, timeouts, missing AWS credentials and the like
            throw new AssistantModelException("Bedrock call failed: " + e.getClass().getSimpleName(), e);
        }

        StringBuilder text = new StringBuilder();
        for (ContentBlock block : message.content()) {
            // thinking blocks are skipped
            block.text().ifPresent(t -> text.append(t.text()));
        }
        Usage usage = message.usage();
        ModelReply reply = new ModelReply(text.toString(),
                message.stopReason().map(String::valueOf).orElse(null),
                usage.inputTokens(),
                usage.outputTokens(),
                usage.cacheReadInputTokens().orElse(0L),
                usage.cacheCreationInputTokens().orElse(0L));

        // the prompt and the reply may contain student data, never log them
        logger.info("onCourse Assistant model {}: stop {}, {} ms, input {}, output {}, cache read {}, cache write {} tokens",
                config.getModelId(), reply.getStopReason(), (System.nanoTime() - start) / 1_000_000,
                reply.getInputTokens(), reply.getOutputTokens(), reply.getCacheReadTokens(), reply.getCacheWriteTokens());

        if (StringUtils.isBlank(reply.getText())) {
            throw new AssistantModelException("empty reply, stop reason " + reply.getStopReason());
        }
        return reply;
    }

    /**
     * Builds the Messages API request: effort low, default (adaptive) thinking, no tools, and a cache breakpoint
     * on the system prompt so that later messages read it from the prompt cache.
     */
    public static MessageCreateParams buildParams(AssistantConfig config, ModelRequest request) {
        MessageCreateParams.Builder builder = MessageCreateParams.builder()
                .model(config.getModelId())
                .maxTokens(config.getMaxTokens())
                .systemOfTextBlockParams(Collections.singletonList(TextBlockParam.builder()
                        .text(request.getSystemPrompt())
                        .cacheControl(CacheControlEphemeral.builder().build())
                        .build()))
                .outputConfig(OutputConfig.builder().effort(OutputConfig.Effort.LOW).build());
        for (ModelMessage message : request.getMessages()) {
            if (message.getRole() == ModelMessage.Role.USER) {
                builder.addUserMessage(message.getText());
            } else {
                builder.addAssistantMessage(message.getText());
            }
        }
        return builder.build();
    }

    private AnthropicClient getClient() {
        AnthropicClient result = client;
        if (result == null) {
            synchronized (this) {
                result = client;
                if (result == null) {
                    result = createClient();
                    client = result;
                }
            }
        }
        return result;
    }

    private AnthropicClient createClient() {
        // never BedrockBackend.fromEnv(): it reads the region and credentials from the environment and throws without them
        BedrockBackend backend = BedrockBackend.builder()
                .region(Region.of(config.getRegion()))
                .awsCredentialsProvider(credentialsProvider())
                .build();
        return AnthropicOkHttpClient.builder()
                .backend(backend)
                .timeout(Timeout.builder()
                        .connect(Duration.ofSeconds(config.getConnectTimeoutSeconds()))
                        .read(Duration.ofSeconds(config.getReadTimeoutSeconds()))
                        .request(Duration.ofSeconds(config.getRequestTimeoutSeconds()))
                        .build())
                .maxRetries(config.getMaxRetries())
                .build();
    }

    private AwsCredentialsProvider credentialsProvider() {
        if (StringUtils.isNotBlank(config.getAccessKeyId())) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(config.getAccessKeyId(), config.getSecretAccessKey()));
        }
        if (StringUtils.isNotBlank(config.getProfile())) {
            return ProfileCredentialsProvider.create(config.getProfile());
        }
        // environment, system properties, ~/.aws profile, then the EC2 instance profile
        return DefaultCredentialsProvider.builder().build();
    }

    @Override
    public void close() {
        AnthropicClient result = client;
        if (result != null) {
            result.close();
        }
    }
}
