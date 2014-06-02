package ish.oncourse.webservices.replication.v7.builders;

import ish.oncourse.model.Certificate;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v7.stubs.replication.CertificateStub;

public class CertificateStubBuilder extends AbstractWillowStubBuilder<Certificate, CertificateStub> {

    @Override
    protected CertificateStub createFullStub(Certificate entity) {
        CertificateStub stub = new CertificateStub();
        stub.setCertificateNumber(entity.getCertificateNumber());
        stub.setEndDate(entity.getEndDate());
        stub.setFundingSource(entity.getFundingSource());
        stub.setPrintedWhen(entity.getPrintedWhen());
        stub.setPrivateNotes(entity.getPrivateNotes());
        stub.setPublicNotes(entity.getPublicNotes());
        stub.setQualification(entity.getIsQualification());
        stub.setQualificationId(entity.getQualification().getId());
        stub.setRevokedWhen(entity.getRevokedWhen());
        stub.setStudentFirstName(entity.getStudentFirstName());
        stub.setStudentLastName(entity.getStudentLastName());
        stub.setStudentId(entity.getStudent().getId());
        stub.setModified(entity.getModified());
        return stub;
    }
}
