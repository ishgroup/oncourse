/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.reference.builders;

import java.util.Date;

import ish.common.types.QualificationType;
import org.apache.commons.lang.StringUtils;

import ish.oncourse.model.Qualification;
import ish.oncourse.webservices.v4.stubs.reference.QualificationStub;

/**
 *
 * @author marek
 */
public final class QualificationStubBuilder implements IReferenceStubBuilder<Qualification> {

	public QualificationStub convert(Qualification record) {
		
		QualificationStub stub = new QualificationStub();

		stub.setWillowId(record.getId());
		stub.setAnzsco(record.getAnzsco());
		//stub.setAnzsic(record.getAnzsic());
		//stop to pass this deprecated field because task#12152.
		stub.setAnzsic(StringUtils.EMPTY);
		stub.setAsco(record.getAsco());
		if (record.getCreated() == null) {
			record.setCreated(new Date());
		}
		if (record.getModified() == null) {
			record.setModified(new Date());
		}
		stub.setCreated(record.getCreated());
		stub.setFieldOfEducation(record.getFieldOfEducation());
		stub.setFieldOfStudy(record.getFieldOfStudy());
		Boolean isAccreditedCourse = (record.getIsAccreditedCourse() == null) ? null
				: (QualificationType.COURSE_TYPE.equals(record.getIsAccreditedCourse()));
		stub.setIsAccreditedCourse(isAccreditedCourse);
		stub.setIshVersion(record.getIshVersion());
		stub.setLevel(record.getLevel());
		stub.setModified(record.getModified());
		stub.setNationalCode(record.getNationalCode());
		stub.setNewApprentices(record.getNewApprenticeship());
		stub.setNominalHours(record.getNominalHours());
		stub.setReviewDate(record.getReviewDate());
		stub.setTitle(record.getTitle());
		stub.setTrainingPackageId(record.getTrainingPackageId());

		return stub;
	}
}
