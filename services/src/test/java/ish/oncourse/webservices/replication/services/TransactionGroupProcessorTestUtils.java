package ish.oncourse.webservices.replication.services;

import ish.common.types.ContactDuplicateStatus;
import ish.oncourse.webservices.v14.stubs.replication.ContactDuplicateStub;
import ish.oncourse.webservices.v14.stubs.replication.ContactStub;
import ish.oncourse.webservices.v14.stubs.replication.DeletedStub;

import java.sql.Date;

/**
 * Created by pavel on 6/6/17.
 */
public class TransactionGroupProcessorTestUtils {

    public static ish.oncourse.webservices.v14.stubs.replication.ContactStub generateContactV14Stub(Long angelId,
                                                                                              Long willowId,
                                                                                              String givenName,
                                                                                              String familyName){
        ish.oncourse.webservices.v14.stubs.replication.ContactStub stub = new ContactStub();
        stub.setAngelId(angelId);
        stub.setWillowId(willowId);
        stub.setGivenName(givenName);
        stub.setFamilyName(familyName);
        stub.setEntityIdentifier("Contact");
        return stub;
    }

    public static ish.oncourse.webservices.v14.stubs.replication.DeletedStub generateDeleteV14Stub(String entityIdentifier,
                                                                                                   Long angelId,
                                                                                                   Long willowId){
        ish.oncourse.webservices.v14.stubs.replication.DeletedStub stub = new DeletedStub();
        stub.setAngelId(angelId);
        stub.setWillowId(willowId);
        stub.setEntityIdentifier(entityIdentifier);
        return stub;
    }

    public static ish.oncourse.webservices.v14.stubs.replication.ContactDuplicateStub generateContactDuplicateV14Stub(Long angelId,
                                                                                                                Long willowId,
                                                                                                                Long contactToUpdateAngelId,
                                                                                                                Long contactToDeleteAngelId,
                                                                                                                Long contactToDeleteWillowId){
        ish.oncourse.webservices.v14.stubs.replication.ContactDuplicateStub stub = new ContactDuplicateStub();
        stub.setAngelId(angelId);
        stub.setWillowId(willowId);
        stub.setContactToUpdateId(contactToUpdateAngelId);
        stub.setContactToDeleteAngelId(contactToDeleteAngelId);
        stub.setContactToDeleteWillowId(contactToDeleteWillowId);
        stub.setEntityIdentifier("ContactDuplicate");
        stub.setCreated(new Date(System.currentTimeMillis()));
        stub.setModified(new Date(System.currentTimeMillis()));
        stub.setStatus(ContactDuplicateStatus.PROCESSED.getDatabaseValue());
        return stub;
    }
}
