/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap;

import org.junit.After;
import org.junit.Before;

/**
 * User: akoiro
 * Date: 24/8/17
 */
public class AbstractServerTest {
	private TestServer server;

	@Before
	public void before() {
		start();
	}

	public TestServer getServer() {
		return server;
	}

	public TestServer start() {
		if (server == null) {
			server = new TestServer().start();
		}
		return server;
	}

	public void stop() {
		this.server.stop();
	}

	@After
	public void after() {
		server.stop();
	}
}
