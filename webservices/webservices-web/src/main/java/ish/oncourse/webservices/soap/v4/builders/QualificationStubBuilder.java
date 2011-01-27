/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.soap.v4.builders;

import ish.oncourse.model.Qualification;
import ish.oncourse.webservices.v4.stubs.reference.QualificationStub;

/**
 *
 * @author marek
 */
public class QualificationStubBuilder {

	public static final QualificationStub convert(Qualification record) {

		QualificationStub stub = new QualificationStub();

		stub.setWillowId((Long) record.readProperty(Qualification.ID_PK_COLUMN));
		stub.setAnzsco(record.getAnzsco());
		stub.setAnzsic(record.getAnzsic());
		stub.setAsco(record.getAsco());
		stub.setCreated(record.getCreated());
		stub.setFieldOfEducation(record.getFieldOfEducation());
		stub.setFieldOfStudy(record.getFieldOfStudy());
		Boolean isAccreditedCourse = (record.getIsAccreditedCourse() == null) ? null
				: (record.getIsAccreditedCourse() == 1);
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
