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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A fake AI model for tests and local development (assistant provider "fake"). It answers with the scripted replies
 * in turn, starting again after the last one, or echoes the user's message when there are none. It never calls
 * a real model.
 */
public class ScriptedModelProvider implements AssistantModelProvider {

    public static final String ECHO_PREFIX = "You said: ";

    private final List<String> replies;
    private final AtomicInteger next = new AtomicInteger();
    private final AtomicReference<Exception> failure = new AtomicReference<>();

    /**
     * Every request the model was asked, in order.
     */
    public final List<ModelRequest> requests = new CopyOnWriteArrayList<>();

    public ScriptedModelProvider(List<String> replies) {
        this.replies = replies == null ? new ArrayList<>() : new ArrayList<>(replies);
    }

    /**
     * Makes the next call throw the exception instead of replying.
     */
    public void failNext(AssistantModelException e) {
        failure.set(e);
    }

    /**
     * Makes the next call throw the unexpected exception instead of replying, like a provider with a bug.
     */
    public void failNext(RuntimeException e) {
        failure.set(e);
    }

    @Override
    public ModelReply reply(ModelRequest request) throws AssistantModelException {
        requests.add(request);
        Exception e = failure.getAndSet(null);
        if (e instanceof AssistantModelException) {
            throw (AssistantModelException) e;
        }
        if (e != null) {
            throw (RuntimeException) e;
        }
        if (replies.isEmpty()) {
            return new ModelReply(ECHO_PREFIX + request.getLastUserText());
        }
        return new ModelReply(replies.get(Math.floorMod(next.getAndIncrement(), replies.size())));
    }
}
