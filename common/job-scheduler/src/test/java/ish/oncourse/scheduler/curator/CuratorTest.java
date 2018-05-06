package ish.oncourse.scheduler.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CuratorTest {
    private TestingServer server;
    private RetryPolicy retryPolicy;
    private ExecutorService service;

    @Before
    public void before() throws Exception {
        server = new TestingServer(10181);
        retryPolicy = new ExponentialBackoffRetry(1000, 3);
        service = Executors.newCachedThreadPool();
    }

    @Test
    @Ignore
    public void test() throws Exception {
        List<Future> futures = new LinkedList<>();
        futures.add(test_LeaderLatch("client1"));
        futures.add(test_LeaderLatch("client2"));
        futures.add(test_LeaderLatch("client3"));
        futures.add(test_LeaderLatch("client4"));
        futures.add(test_LeaderLatch("client5"));
        futures.add(test_LeaderLatch("client6"));

        while (true) {
            final boolean done[] = new boolean[]{true};
            futures.forEach((f) -> done[0] = done[0] && f.isDone());

            if (done[0]) break;
            else Thread.sleep(100);
        }
    }

    private Future test_LeaderLatch(String name) {
        return service.submit(() -> {
            try {
                CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:10181", retryPolicy);
                client.start();

                LeaderLatch latch = new LeaderLatch(client, "/test");
                latch.start();
                latch.await();

                System.out.println(name + " leader");

                Thread.sleep(1000);
                latch.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void test_InterProcessMutex(long wait, String name) {
        service.execute(() -> {
            CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:10181", retryPolicy);
            client.start();
            InterProcessMutex lock = new InterProcessMutex(client, "/test");
            try {
                if (lock.acquire(wait, TimeUnit.SECONDS)) {
                    System.out.println(name + " acquired " + new Date());
                    try {

                        Thread.sleep(wait * 1000);
                    } finally {
                        System.out.println(name + " released " + new Date());
                        lock.release();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @After
    public void after() throws IOException {
        server.stop();
    }
}
