/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.reference.v5.builders;

import ish.oncourse.model.Qualification;
import ish.oncourse.webservices.reference.services.IReferenceStubBuilder;
import ish.oncourse.webservices.v5.stubs.reference.QualificationStub;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

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
		stub.setIsAccreditedCourse((record.getIsAccreditedCourse() == null) ? null : record.getIsAccreditedCourse().getDatabaseValue() );
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
