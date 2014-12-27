package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.CertificateOutcomeStub;

public class CertificateOutcomeStubBuilder extends AbstractWillowStubBuilder<CertificateOutcome, CertificateOutcomeStub>  {

    @Override
    protected CertificateOutcomeStub createFullStub(CertificateOutcome entity) {
        CertificateOutcomeStub stub = new CertificateOutcomeStub();
        stub.setCertificateId(entity.getCertificate().getId());
        stub.setOutcomeId(entity.getOutcome().getId());
        stub.setModified(entity.getModified());
        return stub;
    }
}
