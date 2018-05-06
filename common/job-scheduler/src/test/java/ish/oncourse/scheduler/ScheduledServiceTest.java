/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.scheduler;

import ish.oncourse.scheduler.job.Config;
import ish.oncourse.scheduler.job.IJob;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.test.TestingServer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * User: akoiro
 * Date: 6/5/18
 */
public class ScheduledServiceTest {
	private TestingServer server;
	private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
	private ExecutorService executorService = Executors.newCachedThreadPool();


	@Before
	public void before() throws Exception {
		server = new TestingServer(10181);
	}

	@Test
	@Ignore
	public void test() throws InterruptedException {
		List<Future> futures = new LinkedList<>();

		futures.add(executorService.submit(() -> startService("instance1", 5)));
		futures.add(executorService.submit(() -> startService("instance2", 5)));
		futures.add(executorService.submit(() -> startService("instance3", 5)));
		futures.add(executorService.submit(() -> startService("instance4", 5)));
		futures.add(executorService.submit(() -> startService("instance5", 5)));

		while (futures.stream().filter((f) -> !f.isDone()).count() > 0) {
			Thread.sleep(100);
		}
	}

	private void startService(String id, long time) {
		CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:10181", retryPolicy);
		client.start();
		ScheduleService scheduledService =
				new ScheduleService(client, "/willow/reindex");
		scheduledService.addJob(new IJob() {
			@Override
			public Config getConfig() {
				return new Config(2, 2, TimeUnit.SECONDS);
			}

			@Override
			public void run() {
				try {
					System.out.println(String.format("%s-%s", id, "job1"));
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		});

		scheduledService.start();
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		scheduledService.close();
	}

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new RetryForever(1000));
		client.start();
		ScheduleService scheduledService = new ScheduleService(client, "/willow/reindex").addJob(new IJob() {
			@Override
			public Config getConfig() {
				return new Config(1, 1, TimeUnit.SECONDS);
			}

			@Override
			public void run() {
				System.out.println(String.format("%s:%s", new Date(), args[0]));
			}
		});
		scheduledService.start();

		while (true) {
			Thread.sleep(100);
		}
	}
}
