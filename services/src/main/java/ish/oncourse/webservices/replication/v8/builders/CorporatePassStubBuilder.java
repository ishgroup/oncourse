package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CorporatePass;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.CorporatePassStub;

public class CorporatePassStubBuilder extends AbstractWillowStubBuilder<CorporatePass, CorporatePassStub> {

	@Override
	protected CorporatePassStub createFullStub(CorporatePass entity) {
		CorporatePassStub stub = new CorporatePassStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setExpiryDate(entity.getExpiryDate());
		stub.setInvoiceEmail(entity.getInvoiceEmail());
		stub.setPassword(entity.getPassword());
		Contact contact = entity.getContact();
		stub.setContactId(contact != null ? entity.getContact().getId() : null);
		return stub;
	}

}
