package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Certificate;
import ish.oncourse.model.College;
import ish.oncourse.webservices.v4.stubs.replication.CertificateStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class CertificateUpdater extends AbstractWillowUpdater<CertificateStub, Certificate> {
	
	public CertificateUpdater(College college, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, next);
	}

	@Override
	protected void updateEntity(CertificateStub stub, Certificate entity, List<ReplicatedRecord> relationStubs) {
		
	}
}
