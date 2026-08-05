/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.lifecycle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * ONC-A2: lets work be deferred until after the outermost sync filter has finished, including its own
 * post-chain bookkeeping.
 * <p>
 * The problem this solves: entity/event Groovy scripts were submitted from
 * {@code ScriptTriggeringListener}'s {@code finally}, which unwinds while the replication plugin's
 * outer {@code QueueableLifecycleListener} frame is still open — its QueuedRecord/QueuedTransaction
 * rows exist only in memory at that point. Scripts run on a separate thread, take their own
 * ObjectContext and commit, producing their own QueuedTransaction. Nothing ordered that commit against
 * the frame commit on the request thread, so the script's QueuedTransaction could be assigned the
 * <em>lower</em> auto-increment id. willow pulls transactions in ascending id order, so it would try to
 * insert e.g. a TagRelation whose FK points at an Enrolment that does not exist yet and is not in the
 * same group — an FK violation, three retries, then permanently skipped.
 * <p>
 * Deferral is <b>opt-in</b>: it only activates once something has declared it will drain the queue (the
 * replication plugin does this at startup via {@link #enableDeferral()}). When nothing has opted in —
 * replication disabled, or a build without the plugin — {@link #isDeferralEnabled()} stays false and
 * callers run their work inline exactly as before. That keeps script triggering working regardless of
 * which modules are present.
 */
public final class PostSyncHooks {

    private static final Logger logger = LogManager.getLogger();
    private static final ThreadLocal<List<Runnable>> PENDING = new ThreadLocal<>();
    private static volatile boolean deferralEnabled = false;

    private PostSyncHooks() {
    }

    /**
     * Declares that the caller owns the outermost sync frame and will call {@link #runPending()} once
     * that frame has been committed or rolled back. Until this is called, {@link #defer(Runnable)}
     * refuses to defer so that no work is silently dropped.
     */
    public static void enableDeferral() {
        deferralEnabled = true;
        logger.info("Post-sync hook deferral enabled — entity script dispatch will be delayed until the replication frame is committed.");
    }

    /**
     * @return true when a drainer has registered and {@link #defer(Runnable)} will accept work
     */
    public static boolean isDeferralEnabled() {
        return deferralEnabled;
    }

    /**
     * Queues work to run after the outermost sync frame completes.
     *
     * @param hook work to defer
     * @return true if the hook was queued, false if deferral is not enabled and the caller must run
     *         the work itself
     */
    public static boolean defer(Runnable hook) {
        if (!deferralEnabled) {
            return false;
        }
        List<Runnable> pending = PENDING.get();
        if (pending == null) {
            pending = new ArrayList<>();
            PENDING.set(pending);
        }
        pending.add(hook);
        return true;
    }

    /**
     * Runs and clears everything queued on this thread. Safe to call when nothing is queued.
     * <p>
     * Each hook is isolated: a failure in one is logged and the rest still run, matching the previous
     * behaviour where one failing script could not prevent the others from being submitted. The
     * ThreadLocal is always removed so nothing is pinned on a pooled thread.
     */
    public static void runPending() {
        List<Runnable> pending = PENDING.get();
        if (pending == null) {
            return;
        }
        try {
            for (Runnable hook : pending) {
                try {
                    hook.run();
                } catch (Exception e) {
                    logger.error("Deferred post-sync hook failed.", e);
                }
            }
        } finally {
            PENDING.remove();
        }
    }
}
