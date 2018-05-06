/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.scheduler;

import ish.oncourse.scheduler.job.IJob;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * User: akoiro
 * Date: 6/5/18
 */
public class ScheduledService {
	private static final Logger logger = LogManager.getLogger();
	private ScheduledExecutorService executorService;
	private LeaderLatch leaderLatch;
	private Listener listener;

	private List<IJob> jobs = new LinkedList<>();

	public ScheduledService(CuratorFramework zClient, String mutexPath) {
		leaderLatch = new LeaderLatch(zClient, mutexPath);
		listener = new Listener(this);
	}

	public void start() {
		leaderLatch.addListener(listener);
		try {
			leaderLatch.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void schedule() {
		executorService = Executors.newScheduledThreadPool(jobs.size());
		jobs.forEach((j) -> executorService.scheduleAtFixedRate(j, j.getConfig().getInitialDelay(),
				j.getConfig().getPeriod(),
				j.getConfig().getTimeUnit()));
	}

	private void unschedule() {
		if (executorService != null) {
			executorService.shutdown();
			executorService = null;
		}
	}

	public ScheduledService close() {
		unschedule();
		if (leaderLatch != null) {
			leaderLatch.removeListener(listener);
			try {
				leaderLatch.close();
			} catch (IOException e) {
				logger.catching(e);
			}
		}
		return this;
	}

	public ScheduledService addJob(IJob job) {
		this.jobs.add(job);
		return this;
	}


	private static class Listener implements LeaderLatchListener {
		private ScheduledService scheduledService;


		private Listener(ScheduledService scheduledService) {
			this.scheduledService = scheduledService;
		}

		@Override
		public void isLeader() {
			this.scheduledService.schedule();
		}

		@Override
		public void notLeader() {
			this.scheduledService.unschedule();
		}
	}
}


