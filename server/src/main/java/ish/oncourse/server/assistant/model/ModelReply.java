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

import java.util.Objects;

/**
 * The AI model's reply with its token usage.
 */
public final class ModelReply {

    private final String text;
    private final String stopReason;
    private final long inputTokens;
    private final long outputTokens;
    private final long cacheReadTokens;
    private final long cacheWriteTokens;

    public ModelReply(String text) {
        this(text, null, 0, 0, 0, 0);
    }

    public ModelReply(String text, String stopReason, long inputTokens, long outputTokens,
                      long cacheReadTokens, long cacheWriteTokens) {
        this.text = Objects.requireNonNull(text);
        this.stopReason = stopReason;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.cacheReadTokens = cacheReadTokens;
        this.cacheWriteTokens = cacheWriteTokens;
    }

    public String getText() {
        return text;
    }

    public String getStopReason() {
        return stopReason;
    }

    public long getInputTokens() {
        return inputTokens;
    }

    public long getOutputTokens() {
        return outputTokens;
    }

    public long getCacheReadTokens() {
        return cacheReadTokens;
    }

    public long getCacheWriteTokens() {
        return cacheWriteTokens;
    }
}
