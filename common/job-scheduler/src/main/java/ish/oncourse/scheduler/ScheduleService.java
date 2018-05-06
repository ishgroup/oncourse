/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.scheduler;

import com.cronutils.model.Cron;
import com.cronutils.model.time.ExecutionTime;
import ish.oncourse.scheduler.job.IJob;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: akoiro
 * Date: 6/5/18
 */
public class ScheduleService implements Closeable {
	private static final Logger logger = LogManager.getLogger();
	private ScheduledExecutorService executorService;
	private LeaderLatch leaderLatch;
	private Listener listener;

	private List<IJob> jobs = new LinkedList<>();

	public ScheduleService(CuratorFramework zClient, String mutexPath) {
		leaderLatch = new LeaderLatch(zClient, mutexPath);
		listener = new Listener(this);
	}

	public ScheduleService start() {
		leaderLatch.addListener(listener);
		try {
			leaderLatch.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	private void schedule() {
		logger.debug("ScheduleService is leader, scheduling all jobs");
		executorService = Executors.newScheduledThreadPool(jobs.size());

		jobs.forEach((j) -> {
					long[] period = ScheduleService.toPeriod(j.getCron());
					logger.debug(String.format("Schedule job %s with delay: %d, and period: %d", j.getClass().getSimpleName(), period[0], period[1]));
					executorService.scheduleAtFixedRate(j, period[0],
							period[1],
							TimeUnit.MILLISECONDS);
				}
		);
	}

	private void unschedule() {
		logger.debug("ScheduleService lost leadership, shutdown all scheduled jobs.");
		if (executorService != null) {
			executorService.shutdown();
			executorService = null;
		}
	}

	public boolean isAlive() {
		return leaderLatch.getState() != LeaderLatch.State.CLOSED;
	}

	public void close() {
		logger.debug("ScheduleService is closing ....");
		unschedule();
		if (leaderLatch != null) {
			leaderLatch.removeListener(listener);
			try {
				leaderLatch.close();
			} catch (IOException e) {
				logger.catching(e);
			}
		}
	}

	public ScheduleService addJob(IJob job) {
		this.jobs.add(job);
		return this;
	}


	public static long[] toPeriod(Cron cron) {
		ZonedDateTime now = ZonedDateTime.now();
		ExecutionTime executionTime = ExecutionTime.forCron(cron);
		ZonedDateTime lastExecution = executionTime.lastExecution(now).orElse(now);
		ZonedDateTime nextExecution = executionTime.nextExecution(now).orElse(now.plusDays(1));
		Duration timeToNextExecution = executionTime.timeToNextExecution(now).orElse(Duration.between(now, now.plusDays(1)));
		long delay = timeToNextExecution.toMillis();
		long period = Duration.between(lastExecution, nextExecution).toMillis();
		return new long[]{delay, period};
	}


	private static class Listener implements LeaderLatchListener {
		private ScheduleService scheduledService;


		private Listener(ScheduleService scheduledService) {
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


