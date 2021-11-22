package ish.oncourse.function;

import ish.oncourse.model.College;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.services.IReplicationService;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.json.JSONObject;

import java.util.LinkedHashSet;
import java.util.Set;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.GENERIC_EXCEPTION;
import static ish.oncourse.webservices.replication.services.ReplicationUtils.getEntityClass;

public class GetEntityTransactionByInstruction {

    private static final String INSTRUCTION_TYPE = "type";
    private static final String INSTRUCTION_VALUE = "value";

    private static final String PAYMENT_TRANSACTION_TYPE = "PAYMENT_TRANSACTION";

    private static final Logger logger = LogManager.getLogger();

    private ObjectContext context;
    private IWillowStubBuilder queueableTransactionBuilder;
    private ITransactionStubBuilder paymentTransactionBuilder;
    private College college;
    private String instructionString;
    private SupportedVersions version;

    private GetEntityTransactionByInstruction() {}

    public static GetEntityTransactionByInstruction valueOf(ObjectContext context, IWillowStubBuilder queueableTransactionBuilder, ITransactionStubBuilder paymentTransactionBuilder, College college, String instructionString, SupportedVersions version) {
        GetEntityTransactionByInstruction obj = new GetEntityTransactionByInstruction();
        obj.context = context;
        obj.queueableTransactionBuilder = queueableTransactionBuilder;
        obj.paymentTransactionBuilder = paymentTransactionBuilder;
        obj.college = college;
        obj.instructionString = instructionString;
        obj.version = version;
        return obj;
    }

    public GenericTransactionGroup get() throws IReplicationService.InternalReplicationFault {
        String type;
        Long angelId;

        try {
            JSONObject jsonInstruction = new JSONObject(instructionString);
            type = jsonInstruction.getString(INSTRUCTION_TYPE);
            angelId = jsonInstruction.getLong(INSTRUCTION_VALUE);
        } catch (Exception ex) {
            throw new IReplicationService.InternalReplicationFault("Unable to parse instruction. Instruction should contain type and value.", GENERIC_EXCEPTION,
                    String.format("Unable to parse instruction. Instruction should contain type and value. Willow exception: %s", ex.getMessage()));
        }

        try {
            Set<GenericReplicationStub> stubs = null;
            switch (type) {
                case PAYMENT_TRANSACTION_TYPE: {
                    PaymentIn paymentIn = (PaymentIn) getEntityByAngelId(context, college, PaymentIn.class.getSimpleName(), angelId);
                    if (paymentIn != null) {
                        stubs = paymentTransactionBuilder.createPaymentInTransaction(paymentIn, version);
                    }
                }
                break;
                default: {
                    Queueable entity = getEntityByAngelId(context, college, type, angelId);
                    if (entity != null) {
                        stubs = new LinkedHashSet<>();
                        stubs.add(queueableTransactionBuilder.convert(entity, version));
                    }
                }
            }

            GenericTransactionGroup group = PortHelper.createTransactionGroup(version);
            if (stubs != null) {
                group.getGenericAttendanceOrBinaryInfo().addAll(stubs);
            }
            return group;
        } catch (Exception ex) {
            String errorMessage = String.format("Unable to execute instruction: %s.", instructionString);
            logger.error(errorMessage);
            throw new IReplicationService.InternalReplicationFault(errorMessage, GENERIC_EXCEPTION,
                    String.format("%s Willow exception: %s", errorMessage, ex.getMessage()));
        }
    }

    private Queueable getEntityByAngelId(ObjectContext context, College college, String entityIdentifier, Long angelId) {
        return ObjectSelect.query(getEntityClass(context, entityIdentifier))
                .where(ExpressionFactory.matchDbExp("angelId", angelId).andExp(ExpressionFactory.matchExp("college", college)))
                .selectFirst(context);
    }
}
