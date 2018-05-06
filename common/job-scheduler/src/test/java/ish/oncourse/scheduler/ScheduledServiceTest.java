/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.scheduler;

import com.cronutils.model.Cron;
import com.cronutils.parser.CronParser;
import ish.oncourse.scheduler.job.IJob;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryForever;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: akoiro
 * Date: 6/5/18
 */
public class ScheduledServiceTest {
	private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
	private ExecutorService executorService = Executors.newCachedThreadPool();

	@Before
	public void before() throws Exception {
	}

	@Test
	@Ignore
	public void test() throws InterruptedException {
		List<ScheduleService> services = new LinkedList<>();
		services.add(startService("instance1"));
		services.add(startService("instance2"));
		services.add(startService("instance3"));
		services.add(startService("instance4"));
		services.add(startService("instance5"));
		while (services.stream().filter(ScheduleService::isAlive).count() > 0) {
			Thread.sleep(100);
		}
	}

	private ScheduleService startService(String id) {
		CuratorFramework client = CuratorFrameworkFactory.newClient("ubuntu.home:2181", retryPolicy);
		client.start();
		ScheduleService scheduledService =
				new ScheduleService(client, "/willow/reindex");
		scheduledService.addJob(new IJob() {
			private int counter = 0;

			@Override
			public Cron getCron() {
				return new CronParser(DEFAULT_CRON_DEFINITION).parse("0/1 * * ? * *");
			}

			@Override
			public void run() {
				try {
					if (counter < 5) {
						System.out.println(String.format("%s-%s", id, "job1"));
						Thread.sleep(1000);
						counter++;
					} else {
						scheduledService.close();
					}
				} catch (InterruptedException e) {
				}
			}
		});

		return scheduledService.start();
	}

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.newClient("ubuntu.home:2181", new RetryForever(1000));
		client.start();
		ScheduleService scheduledService = new ScheduleService(client, "/willow/reindex").addJob(new IJob() {
			@Override
			public Cron getCron() {
				return new CronParser(DEFAULT_CRON_DEFINITION).parse("0/1 * * ? * *");
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
