package ish.oncourse.utils;

import ish.oncourse.model.Session;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

public class SessionUtilsTest {

	@Test
	public void testStartEndTime() {
		SessionUtils.StartEndTime t1 = new SessionUtils.StartEndTime();
		t1.setStartTime(900);
		t1.setEndTime(1000);

		SessionUtils.StartEndTime t2 = new SessionUtils.StartEndTime();
		t1.setStartTime(900);
		t1.setEndTime(1100);

		SessionUtils.StartEndTime t3 = new SessionUtils.StartEndTime();
		t1.setStartTime(900);
		t1.setEndTime(1100);

		assertFalse(t1.equals(t2));

		assertEquals(1, t1.compareTo(t2));

		assertTrue(t2.equals(t3));
		assertEquals(0, t2.compareTo(t3));

	}

	@Test
	public void testValueOf() throws ParseException {
		Session session = new Session();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start = format.parse("2013-10-01 10:00:10");
		session.setStartDate(start);
		Date end = format.parse("2013-10-01 11:00:10");
		session.setEndDate(end);

		SessionUtils.StartEndTime t1 = SessionUtils.valueOf(session);
		assertNotNull(t1);

		assertEquals(SessionUtils.adjustDate2Time(start), t1.getStartTime());
		assertEquals(SessionUtils.adjustDate2Time(end), t1.getEndTime());

		assertEquals(60 * 60 * 1000, t1.getEndTime() - t1.getStartTime());
	}

	@Test
	public void testSameTime() throws ParseException
	{
		ArrayList<Session> sessions = new ArrayList<>();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

		Session session = new Session();
		session.setStartDate(format.parse("2013-10-01 10:00:10 +0100"));
		session.setEndDate(format.parse("2013-10-01 11:00:10 +0100"));
		sessions.add(session);

		session = new Session();
		session.setStartDate(format.parse("2013-10-01 09:00:10 +0000"));
		session.setEndDate(format.parse("2013-10-01 10:00:10 +0000"));
		sessions.add(session);

		session = new Session();
		session.setStartDate(format.parse("2013-10-01 08:00:10 -0100"));
		session.setEndDate(format.parse("2013-10-01 09:00:10 -0100"));
		sessions.add(session);

		assertTrue(SessionUtils.isSameTime(sessions));


		sessions.clear();


		session = new Session();
		session.setStartDate(format.parse("2013-10-01 08:00:10 -0100"));
		session.setEndDate(format.parse("2013-10-01 09:30:10 -0100"));
		sessions.add(session);

		session = new Session();
		session.setStartDate(format.parse("2013-10-01 10:00:10 +0100"));
		session.setEndDate(format.parse("2013-10-01 11:00:10 +0100"));
		sessions.add(session);

		session = new Session();
		session.setStartDate(format.parse("2013-10-01 09:00:10 +0000"));
		session.setEndDate(format.parse("2013-10-01 10:00:10 +0000"));
		sessions.add(session);


		assertFalse(SessionUtils.isSameTime(sessions));
	}
}
