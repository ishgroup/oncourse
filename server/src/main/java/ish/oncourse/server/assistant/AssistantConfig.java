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

package ish.oncourse.server.assistant;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import ish.oncourse.server.assistant.model.AssistantModelProvider;
import ish.oncourse.server.assistant.model.BedrockModelProvider;
import ish.oncourse.server.assistant.model.ScriptedModelProvider;
import ish.oncourse.server.assistant.model.UnconfiguredModelProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Server configuration of the onCourse Assistant's AI model, the {@code assistant:} section of onCourse.yml.
 * Without the section the assistant has no model and can't answer.
 */
@BQConfig("onCourse Assistant AI model")
public class AssistantConfig {

    private static final Logger logger = LogManager.getLogger();

    public static final String PROVIDER_BEDROCK = "bedrock";
    public static final String PROVIDER_FAKE = "fake";
    public static final String PROVIDER_NONE = "none";

    private String provider = PROVIDER_NONE;
    private String region = "ap-southeast-2";
    private String modelId = "au.anthropic.claude-sonnet-5-5";
    private String accessKeyId;
    private String secretAccessKey;
    private String profile;
    private long maxTokens = 4000;
    private long connectTimeoutSeconds = 10;
    private long readTimeoutSeconds = 60;
    private long requestTimeoutSeconds = 120;
    private int maxRetries = 1;
    private List<String> fakeReplies = new ArrayList<>();

    /**
     * @return the model provider this configuration chooses
     */
    public AssistantModelProvider createProvider() {
        String value = provider == null ? PROVIDER_NONE : provider.trim().toLowerCase(Locale.ROOT);
        switch (value) {
            case PROVIDER_BEDROCK:
                logger.info("onCourse Assistant uses {} on Amazon Bedrock in {}", modelId, region);
                return new BedrockModelProvider(this);
            case PROVIDER_FAKE:
                logger.warn("onCourse Assistant uses the scripted fake model");
                return new ScriptedModelProvider(fakeReplies);
            case PROVIDER_NONE:
            case "":
                return new UnconfiguredModelProvider();
            default:
                logger.error("Unknown onCourse Assistant provider '{}', expected bedrock, fake or none. The assistant has no model.", provider);
                return new UnconfiguredModelProvider();
        }
    }

    public String getProvider() {
        return provider;
    }

    @BQConfigProperty("AI model provider: bedrock, fake or none (default)")
    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getRegion() {
        return region;
    }

    @BQConfigProperty("AWS region of Amazon Bedrock, ap-southeast-2 by default")
    public void setRegion(String region) {
        this.region = region;
    }

    public String getModelId() {
        return modelId;
    }

    @BQConfigProperty("Bedrock model or inference profile id, au.anthropic.claude-sonnet-5-5 by default")
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    @BQConfigProperty("AWS access key id. Without it the profile or the default AWS credentials chain is used")
    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    @BQConfigProperty("AWS secret access key, used with accessKeyId")
    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    public String getProfile() {
        return profile;
    }

    @BQConfigProperty("AWS profile from ~/.aws, used when accessKeyId isn't set")
    public void setProfile(String profile) {
        this.profile = profile;
    }

    public long getMaxTokens() {
        return maxTokens;
    }

    @BQConfigProperty("Maximum tokens in a reply, 4000 by default")
    public void setMaxTokens(long maxTokens) {
        this.maxTokens = maxTokens;
    }

    public long getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    @BQConfigProperty("Connect timeout in seconds, 10 by default")
    public void setConnectTimeoutSeconds(long connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    public long getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    @BQConfigProperty("Longest wait for data from the model in seconds, 60 by default. The first call after a deploy can take 35 s")
    public void setReadTimeoutSeconds(long readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
    }

    public long getRequestTimeoutSeconds() {
        return requestTimeoutSeconds;
    }

    @BQConfigProperty("Timeout of a whole model call in seconds, 120 by default")
    public void setRequestTimeoutSeconds(long requestTimeoutSeconds) {
        this.requestTimeoutSeconds = requestTimeoutSeconds;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    @BQConfigProperty("Retries of a failed model call, 1 by default")
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public List<String> getFakeReplies() {
        return fakeReplies;
    }

    @BQConfigProperty("Replies of the fake provider, in turn. Without them it echoes the user's message")
    public void setFakeReplies(List<String> fakeReplies) {
        this.fakeReplies = fakeReplies;
    }
}
