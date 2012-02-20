package ish.oncourse.admin.services.ntis;

import ish.oncourse.model.Qualification;

import org.junit.Test;

import junit.framework.TestCase;

public class NTISUpdaterImplTest extends TestCase {
	
	@Test
	public void testSetQualifiactionTitleAndLevel() {
		NTISUpdaterImpl updater = new NTISUpdaterImpl();
		
		String title;
		Qualification q;
		
		title = "Certificate III in Business";
		q = new Qualification();
		updater.setQualificationTitleAndLevel(q, title);
		assertEquals("Business", q.getTitle());
		assertEquals("Certificate III in", q.getLevel());
		
		title = "Certificate III in  Business"; // extra space
		q = new Qualification();
		updater.setQualificationTitleAndLevel(q, title);
		assertEquals("Business", q.getTitle());
		assertEquals("Certificate III in", q.getLevel());
		
		title = "Aardvaark in Business";
		q = new Qualification();
		updater.setQualificationTitleAndLevel(q, title);
		assertEquals("Aardvaark in Business", q.getTitle());
		assertEquals("", q.getLevel());
		
		title = "Diploma of";
		q = new Qualification();
		updater.setQualificationTitleAndLevel(q, title);
		assertEquals("", q.getTitle());
		assertEquals("Diploma of", q.getLevel());
		
		title = "Certificate IIII in Business";
		q = new Qualification();
		updater.setQualificationTitleAndLevel(q, title);
		assertEquals("Certificate IIII in Business", q.getTitle());
		assertEquals("", q.getLevel());
	}
	
}
