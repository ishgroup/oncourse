package ish.oncourse.admin.services.ntis;

import org.junit.Test;

import junit.framework.TestCase;

public class NTISUpdaterImplTest extends TestCase {
	
	@Test
	public void testParseQualificationTitle() {
		NTISUpdaterImpl updater = new NTISUpdaterImpl();
		
		assertEquals("Security Operations", 
				updater.parseQualificationTitle("Certificate III in Security Operations"));
		assertEquals("Telecommunications Cabling", 
				updater.parseQualificationTitle("Certificate I in Telecommunications Cabling"));
		assertEquals("Security Operations", 
				updater.parseQualificationTitle("Certificate IV Security Operations"));
		assertEquals("Security Operations", 
				updater.parseQualificationTitle("Security Operations"));
	}
}
