package ish.oncourse.admin.services.ntis.trainingcomponent;

import ish.oncourse.model.Qualification;
import junit.framework.TestCase;
import org.junit.Test;

public class QualificationNTISUpdaterTest extends TestCase {
	
	@Test
	public void testSetQualifiactionTitleAndLevel() {
		
		String title;
		Qualification q;
		
		title = "Certificate III in Business";
		q = new Qualification();
		QualificationNTISUpdater.setQualificationTitleAndLevel(q, title);
		assertEquals("Business", q.getTitle());
		assertEquals("Certificate III in", q.getLevel());
		
		title = "Certificate III in  Business"; // extra space
		q = new Qualification();
		QualificationNTISUpdater.setQualificationTitleAndLevel(q, title);
		assertEquals("Business", q.getTitle());
		assertEquals("Certificate III in", q.getLevel());
		
		title = "Aardvaark in Business";
		q = new Qualification();
		QualificationNTISUpdater.setQualificationTitleAndLevel(q, title);
		assertEquals("Aardvaark in Business", q.getTitle());
		assertEquals("", q.getLevel());
		
		title = "Diploma of";
		q = new Qualification();
		QualificationNTISUpdater.setQualificationTitleAndLevel(q, title);
		assertEquals("", q.getTitle());
		assertEquals("Diploma of", q.getLevel());
		
		title = "Certificate IIII in Business";
		q = new Qualification();
		QualificationNTISUpdater.setQualificationTitleAndLevel(q, title);
		assertEquals("Certificate IIII in Business", q.getTitle());
		assertEquals("", q.getLevel());
	}
	
}
