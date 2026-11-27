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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * <p>
 * The values have to fit the {@code Audit} columns, otherwise the audit entry is lost. Auditing must never
 * break the operation it audits, so the constructor never throws: a change set reference longer than
 * {@link #CHANGE_SET_REF_MAX_LENGTH} is dropped with a warning, a request text longer than
 * {@link #REQUEST_TEXT_MAX_BYTES} UTF-8 bytes is truncated. Every {@link AuditSource} value fits its column,
 * which {@code AuditContextLimitsTest} checks.
 */
public final class AuditContext {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Length of the {@code Audit.source} column, VARCHAR(32)
     */
    public static final int SOURCE_MAX_LENGTH = 32;

    /**
     * Length of the {@code Audit.changeSetRef} column, VARCHAR(64)
     */
    public static final int CHANGE_SET_REF_MAX_LENGTH = 64;

    /**
     * Size of the {@code Audit.requestText} column, TEXT: 65535 bytes whatever the charset
     */
    public static final int REQUEST_TEXT_MAX_BYTES = 65_535;

    private static final ThreadLocal<AuditContext> CURRENT = new ThreadLocal<>();

    private final AuditSource source;
    private final String requestText;
    private final String changeSetRef;
    private final boolean recordValues;

    /**
     * @param source where the changes come from
     * @param requestText text of the request that causes the changes, e.g. the message sent to the assistant;
     *                    truncated to {@link #REQUEST_TEXT_MAX_BYTES} UTF-8 bytes
     * @param changeSetRef reference to the change set the changes belong to, at most {@link #CHANGE_SET_REF_MAX_LENGTH}
     *                     characters; a longer one is dropped
     * @param recordValues whether the audit listeners record before/after values
     */
    public AuditContext(AuditSource source, String requestText, String changeSetRef, boolean recordValues) {
        this.source = source;
        this.requestText = truncateUtf8(requestText, REQUEST_TEXT_MAX_BYTES);
        this.changeSetRef = fitChangeSetRef(changeSetRef);
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
     * Undo looks up the entries of a change set by this reference. A truncated reference could equal the
     * truncated reference of another change set with the same prefix, and undo would then revert changes
     * of both, so a reference that does not fit is dropped rather than truncated: the entries are still
     * stored, only without a change set to undo them by.
     */
    private static String fitChangeSetRef(String changeSetRef) {
        if (changeSetRef == null || changeSetRef.length() <= CHANGE_SET_REF_MAX_LENGTH) {
            return changeSetRef;
        }
        // log the length only, never the reference
        logger.warn("Change set reference of {} characters is longer than {}, audit entries are stored without it",
                changeSetRef.length(), CHANGE_SET_REF_MAX_LENGTH);
        return null;
    }

    /**
     * @return the text cut to at most maxBytes bytes in UTF-8, without splitting a character
     */
    static String truncateUtf8(String text, int maxBytes) {
        // a character takes at most 4 bytes in UTF-8 (a surrogate pair is 2 chars)
        if (text == null || text.length() <= maxBytes / 4) {
            return text;
        }
        int bytes = 0;
        int index = 0;
        while (index < text.length()) {
            int codePoint = text.codePointAt(index);
            int size = codePoint < 0x80 ? 1 : codePoint < 0x800 ? 2 : codePoint < 0x10000 ? 3 : 4;
            if (bytes + size > maxBytes) {
                return text.substring(0, index);
            }
            bytes += size;
            index += Character.charCount(codePoint);
        }
        return text;
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
