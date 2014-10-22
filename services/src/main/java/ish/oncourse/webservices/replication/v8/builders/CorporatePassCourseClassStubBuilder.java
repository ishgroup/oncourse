package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.CorporatePassCourseClass;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.CorporatePassCourseClassStub;

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
