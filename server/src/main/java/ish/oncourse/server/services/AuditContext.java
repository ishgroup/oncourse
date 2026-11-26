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

package ish.oncourse.server.services;

import ish.oncourse.types.AuditSource;

/**
 * Where the changes made on the current thread come from, for the audit log.
 * <p>
 * Every audit entry submitted through {@link AuditService} while a context is open carries its source,
 * request text and change set reference. A context that records values also makes the audit listeners
 * store the before/after values of created, updated and deleted records (see {@link AuditValues}).
 * Without a context these columns stay empty.
 * <pre>
 * try (AuditContext.Scope ignored = AuditContext.open(AuditContext.assistant(requestText, changeSetRef))) {
 *     // changes committed here are audited as made by the assistant
 * }
 * </pre>
 * In Groovy: {@code AuditContext.open(context).withCloseable { ... }}.
 * <p>
 * Contexts nest: closing a scope brings back the context that was open before it. The context belongs
 * to the thread, so work handed over to another thread has to open its own.
 * {@code ApiFilter} opens a source-only context (user or API token)
 * for every authorised API request.
 */
public final class AuditContext {

    private static final ThreadLocal<AuditContext> CURRENT = new ThreadLocal<>();

    private final AuditSource source;
    private final String requestText;
    private final String changeSetRef;
    private final boolean recordValues;

    /**
     * @param source where the changes come from
     * @param requestText text of the request that causes the changes, e.g. the message sent to the assistant
     * @param changeSetRef reference to the change set the changes belong to
     * @param recordValues whether the audit listeners record before/after values
     */
    public AuditContext(AuditSource source, String requestText, String changeSetRef, boolean recordValues) {
        this.source = source;
        this.requestText = requestText;
        this.changeSetRef = changeSetRef;
        this.recordValues = recordValues;
    }

    /**
     * Source only, no values recorded: the default for user and API token requests.
     */
    public static AuditContext of(AuditSource source) {
        return new AuditContext(source, null, null, false);
    }

    /**
     * Changes made by the assistant for the request text, recorded with their before/after values.
     */
    public static AuditContext assistant(String requestText, String changeSetRef) {
        return new AuditContext(AuditSource.ASSISTANT, requestText, changeSetRef, true);
    }

    /**
     * @return the context open on this thread, or null
     */
    public static AuditContext current() {
        return CURRENT.get();
    }

    /**
     * Opens the context on this thread until the returned scope is closed.
     */
    public static Scope open(AuditContext context) {
        Scope scope = new Scope(CURRENT.get());
        CURRENT.set(context);
        return scope;
    }

    public AuditSource getSource() {
        return source;
    }

    public String getRequestText() {
        return requestText;
    }

    public String getChangeSetRef() {
        return changeSetRef;
    }

    public boolean isRecordValues() {
        return recordValues;
    }

    /**
     * Closing it brings back the context that was open before.
     */
    public static final class Scope implements AutoCloseable {

        private final AuditContext previous;
        private boolean closed;

        private Scope(AuditContext previous) {
            this.previous = previous;
        }

        @Override
        public void close() {
            if (closed) {
                return;
            }
            closed = true;
            if (previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(previous);
            }
        }
    }
}
