package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.EnrolmentFieldConfiguration;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedRecordAction;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.SupportedVersions;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class EnrolmentWillowStubBuilderTest {
    @Test
    public void convertTest() throws Exception {

        EnrolmentFieldConfiguration entity = createEntity();
        QueuedRecord queuedRecord = createQueuedRecord(entity);

        IWillowStubBuilder builder = createStubBuilder(entity, queuedRecord);

        GenericReplicationStub stub = builder.convert(queuedRecord, SupportedVersions.V17);

        Mockito.verify(stub, times(1)).setEntityIdentifier("EnrolmentFieldConfiguration");
        Mockito.verify(stub).setEntityIdentifier("EnrolmentFieldConfiguration");
    }

    private QueuedRecord createQueuedRecord(Queueable entity) {
        QueuedRecord qr = mock(QueuedRecord.class);
        when(qr.getAction()).thenReturn(QueuedRecordAction.UPDATE);
        when(qr.getLinkedRecord()).thenReturn(entity);
        return qr;
    }

    private EnrolmentFieldConfiguration createEntity() {
        EnrolmentFieldConfiguration fc = mock(EnrolmentFieldConfiguration.class);
        when(fc.getId()).thenReturn(1L);
        when(fc.getAngelId()).thenReturn(5L);
        when(fc.getCreated()).thenReturn(new Date());
        return fc;
    }

    private AbstractWillowStubBuilder createStubBuilder(EnrolmentFieldConfiguration entity, QueuedRecord queuedRecord) {
        GenericReplicationStub stub = spy(GenericReplicationStub.class);

        AbstractWillowStubBuilder builder = mock(AbstractWillowStubBuilder.class);
        when(builder.convert(any(QueuedRecord.class), any(SupportedVersions.class))).thenCallRealMethod();
        when(builder.convert(any(Queueable.class), any(SupportedVersions.class))).thenReturn(stub);
        return builder;
    }
}
