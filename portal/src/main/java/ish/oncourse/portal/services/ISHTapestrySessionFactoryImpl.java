package ish.oncourse.portal.services;

import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.SessionImpl;
import org.apache.tapestry5.internal.services.SessionLock;
import org.apache.tapestry5.internal.services.TapestrySessionFactory;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.services.SessionPersistedObjectAnalyzer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ISHTapestrySessionFactoryImpl implements TapestrySessionFactory {
	private boolean clustered;

	private final SessionPersistedObjectAnalyzer analyzer;

	private final HttpServletRequest request;

	private final PerthreadManager perthreadManager;

	private final boolean sessionLockingEnabled;

	private final Lock mapLock = new ReentrantLock();

	private final Map<HttpSession, SessionLock> sessionToLock = new WeakHashMap<>();

	@Inject
	private ICayenneService cayenneService;


	private final SessionLock NO_OP_LOCK = new SessionLock() {
		public void acquireReadLock() {
		}

		public void acquireWriteLock() {
		}
	};

	private class SessionLockImpl implements SessionLock {

		private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

		private boolean isReadLocked() {
			return lock.getReadHoldCount() != 0;
		}

		private boolean isWriteLocked() {
			return lock.isWriteLockedByCurrentThread();
		}

		public void acquireReadLock() {
			if (isReadLocked() || isWriteLocked()) {
				return;
			}

			lock.readLock().lock();

			perthreadManager.addThreadCleanupCallback(new Runnable() {
				public void run() {
					// The read lock may have been released, if upgraded to a write lock.
					if (isReadLocked()) {
						lock.readLock().unlock();
					}
				}
			});
		}

		public void acquireWriteLock() {
			if (isWriteLocked()) {
				return;
			}

			if (isReadLocked()) {
				lock.readLock().unlock();
			}

			// During this window, no lock is held, and the next call may block.

			lock.writeLock().lock();

			perthreadManager.addThreadCleanupCallback(new Runnable() {
				public void run() {
					// This is the only way a write lock is unlocked, so no check is needed.
					lock.writeLock().unlock();
				}
			});
		}
	}

	public ISHTapestrySessionFactoryImpl(
			@Symbol(SymbolConstants.CLUSTERED_SESSIONS)
					boolean clustered,
			SessionPersistedObjectAnalyzer analyzer,
			HttpServletRequest request,
			PerthreadManager perthreadManager,
			@Symbol(SymbolConstants.SESSION_LOCKING_ENABLED)
					boolean sessionLockingEnabled) {
		this.clustered = clustered;
		this.analyzer = analyzer;
		this.request = request;
		this.perthreadManager = perthreadManager;
		this.sessionLockingEnabled = sessionLockingEnabled;
	}

	public Session getSession(boolean create) {
		final HttpSession httpSession = request.getSession(create);

		if (httpSession == null) {
			return null;
		}

		SessionLock lock = lockForSession(httpSession);

		if (clustered) {
			return new ISHClusteredSessionImpl(request, httpSession, lock, analyzer, cayenneService);
		}

		return new SessionImpl(request, httpSession, lock);
	}

	private SessionLock lockForSession(HttpSession session) {
		if (!sessionLockingEnabled) {
			return NO_OP_LOCK;
		}

		// Because WeakHashMap does not look thread safe to me, we use an exclusive
		// lock.
		mapLock.lock();

		try {
			SessionLock result = sessionToLock.get(session);

			if (result == null) {
				result = new SessionLockImpl();
				sessionToLock.put(session, result);
			}

			return result;
		} finally {
			mapLock.unlock();
		}
	}
}
