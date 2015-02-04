package ish.oncourse.webservices.replication.v9.builders;

import ish.oncourse.model.CorporatePassCourseClass;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v9.stubs.replication.CorporatePassCourseClassStub;

public class CorporatePassCourseClassStubBuilder extends AbstractWillowStubBuilder<CorporatePassCourseClass, CorporatePassCourseClassStub> {

	@Override
	protected CorporatePassCourseClassStub createFullStub(CorporatePassCourseClass entity) {
		CorporatePassCourseClassStub stub = new CorporatePassCourseClassStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setCorporatePassId(entity.getCorporatePass().getId());
		stub.setCourseClassId(entity.getCourseClass().getId());
		return stub;
	}
	
}
