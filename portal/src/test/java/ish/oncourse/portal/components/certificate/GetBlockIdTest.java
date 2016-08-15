/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.certificate;

import ish.common.types.QualificationType;
import ish.oncourse.portal.certificate.Model;
import ish.oncourse.portal.certificate.Model.Qualification;
import org.junit.Test;

import java.util.Date;

import static ish.oncourse.portal.components.certificate.Title.BlockId;
import static ish.oncourse.portal.components.certificate.Title.GetBlockId;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: akoiro
 * Date: 15/08/2016
 */
public class GetBlockIdTest {
	@Test
	public void testRevoked() {
		Model model = mock(Model.class);
		when(model.getRevoked()).thenReturn(new Date());
		assertEquals(BlockId.revoked, GetBlockId.valueOf(model).getBlockId());
	}

	@Test
	public void testStatement() {

		Model model = mock(Model.class);
		when(model.getRevoked()).thenReturn(null);
		assertEquals(BlockId.statement, GetBlockId.valueOf(model).getBlockId());


		Qualification qualification = mock(Qualification.class);
		model = mock(Model.class);
		when(model.getQualification()).thenReturn(qualification);
		assertEquals(BlockId.statement, GetBlockId.valueOf(model).getBlockId());
	}

	@Test
	public  void testSkillSet() {
		assertQualificationType(QualificationType.SKILLSET_LOCAL_TYPE, true);
		assertQualificationType(QualificationType.SKILLSET_TYPE, true);
	}

	@Test
	public  void testQualification() {
		assertQualificationType(QualificationType.COURSE_TYPE, false);
		assertQualificationType(QualificationType.HIGHER_TYPE, false);
		assertQualificationType(QualificationType.QUALIFICATION_TYPE, false);
	}


	private void assertQualificationType(QualificationType type, boolean isSkillset) {
		Qualification qualification = mock(Qualification.class);
		when(qualification.getType()).thenReturn(type);
		when(qualification.isQualification()).thenReturn(true);
		Model model = mock(Model.class);
		when(model.getQualification()).thenReturn(qualification);
		assertEquals(isSkillset ? BlockId.skillset: BlockId.qualification, GetBlockId.valueOf(model).getBlockId());
	}
}
