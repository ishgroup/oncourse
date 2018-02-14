package ish.oncourse.webservices.replication.services;

import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.enrol.IEnrolmentService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.paymentexpress.PaymentExpressGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.util.payment.CompleteInTransactionPayments;
import ish.oncourse.util.payment.PaymentInAbandon;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.util.payment.PaymentInSucceed;
import ish.oncourse.utils.PaymentInUtil;
import ish.oncourse.utils.SessionIdGenerator;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.GENERIC_EXCEPTION;

public class PaymentServiceImpl implements InternalPaymentService {

    private static final Logger logger = LogManager.getLogger();

    private final ITransactionGroupProcessor groupProcessor;

    private final IPaymentGatewayService paymentGatewayService;

    private final ICayenneService cayenneService;

    private final IPaymentService paymentInService;

    private final IVoucherService voucherService;

    private final IEnrolmentService enrolService;

    private final IUSIVerificationService usiVerificationService;

    private final ITransactionStubBuilder transactionBuilder;

    private final SessionIdGenerator idGenerator;

    private final IWillowStubBuilder stubBuilder;

    private final PreferenceControllerFactory prefsFactory;

    private final IWebSiteService webSiteService;

    @Inject
    public PaymentServiceImpl(ITransactionGroupProcessor groupProcessor, IPaymentGatewayService paymentGatewayService,
                              ICayenneService cayenneService, IPaymentService paymentInService, IEnrolmentService enrolService, IVoucherService voucherService,
                              IUSIVerificationService usiVerificationService, ITransactionStubBuilder transactionBuilder, IWillowStubBuilder stubBuilder,
                              PreferenceControllerFactory prefsFactory,
                              IWebSiteService webSiteService
                              ) {
        super();
        this.groupProcessor = groupProcessor;
        this.paymentGatewayService = paymentGatewayService;
        this.cayenneService = cayenneService;
        this.paymentInService = paymentInService;
        this.enrolService = enrolService;
        this.voucherService = voucherService;
        this.usiVerificationService = usiVerificationService;
        this.transactionBuilder = transactionBuilder;
        this.stubBuilder = stubBuilder;
        this.prefsFactory = prefsFactory;
        this.idGenerator = new SessionIdGenerator();
        this.webSiteService = webSiteService;
    }

    @Override
    public GenericTransactionGroup processPayment(GenericTransactionGroup transaction, GenericParametersMap parametersMap) throws InternalReplicationFault {
        ObjectContext newContext = null;
        PaymentInModel paymentInModel = null;
        try {
            List<GenericReplicatedRecord> replicatedRecords = groupProcessor.processGroup(transaction);

            newContext = cayenneService.newContext();

            paymentInModel = parametersMap == null ? PaymentInModelFromReplicatedRecordsBuilder.valueOf(replicatedRecords, newContext).build().getModel():
                    PaymentInModelFromParametersMapBuilder.valueOf(parametersMap, replicatedRecords, webSiteService.getCurrentCollege(), newContext).build().getModel();
        } catch (Exception e) {
            logger.error("Unable to process payment in.", e);
            throw new InternalReplicationFault("Unable to process payment in.", GENERIC_EXCEPTION,
                    String.format("Unable to process payment in. Willow exception: %s", e.getMessage()));
        }

        try {
            List<PaymentIn> updatedPayments = processModel(paymentInModel);

            newContext.commitChanges();

            return createResponse(transaction, updatedPayments);
        } catch (Exception e) {
            logger.error("Exception happened after paymentIn: {} was saved. ", paymentInModel.getPaymentIn().getId(), e);
            return plainPaymentEnrolmentResponse(paymentInModel, PortHelper.getVersionByTransactionGroup(transaction));
        }
    }

    private List<PaymentIn> processModel(PaymentInModel paymentInModel) {
        List<PaymentIn> updatedPayments = new LinkedList<>();
        updatedPayments.add(paymentInModel.getPaymentIn());
        updatedPayments.addAll(PaymentInUtil.getRelatedVoucherPayments(paymentInModel.getPaymentIn()));

		CompleteInTransactionPayments.valueOf(cayenneService.newContext(), paymentInModel.getPaymentIn(), paymentInModel.getPaymentIn().getContact(), paymentInService).complite();

		PaymentInModelValidator validator = PaymentInModelValidator.valueOf(paymentInModel, prefsFactory).validate();

        if (validator.getError() == null) {
            if (paymentInModel.getPaymentIn().getType() == PaymentType.CREDIT_CARD &&
                    paymentInModel.getPaymentIn().getAmount().compareTo(Money.ZERO) != 0) {
                String sessionId = idGenerator.generateSessionId();
                paymentInModel.getPaymentIn().setSessionId(sessionId);
                for (Invoice invoice: paymentInModel.getInvoices()) {
                    invoice.setSessionId(sessionId);
                }
            } else {
                PaymentInSucceed.valueOf(paymentInModel).perform();
            }
        } else {
            switch (validator.getError()) {
                case activeEnrolmentExists:
                case noLicenseCCProcessing:
                    paymentInModel.getPaymentIn().setStatus(PaymentStatus.FAILED);
                    break;
                case noPlacesAvailable:
                    paymentInModel.getPaymentIn().setStatus(PaymentStatus.FAILED_NO_PLACES);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            paymentInModel.getPaymentIn().setStatusNotes(validator.getErrorMessage());
            updatedPayments.addAll(PaymentInAbandon.valueOf(paymentInModel,
                    keepInvoice(paymentInModel.getPaymentIn())).perform().getRefundPayments());
        }
        return updatedPayments;
    }

    private boolean keepInvoice(PaymentIn paymentIn) {
        return PaymentInUtil.hasSuccessEnrolments(paymentIn) || PaymentInUtil.hasSuccessProductItems(paymentIn);
    }


    private GenericTransactionGroup createResponse(GenericTransactionGroup transaction, List<PaymentIn> updatedPayments) {
        GenericTransactionGroup response = PortHelper.createTransactionGroup(transaction);
        Set<GenericReplicationStub> updatedStubs = transactionBuilder.createPaymentInTransaction(updatedPayments, PortHelper.getVersionByTransactionGroup(transaction));
        response.getGenericAttendanceOrBinaryDataOrBinaryInfo().addAll(updatedStubs);
        return response;
    }

    /**
     * Process paymentIn and enrolments. Firstly, saves paymentIn and related
     * objects, then check available enrolment places. If no places available
     * abandons payment, generates session id for credit card payments.
     */
    @Override
    public GenericTransactionGroup processPayment(GenericTransactionGroup transaction) throws InternalReplicationFault {
        return processPayment(transaction, null);
    }

    /**
     * Processes refunds.
     */
    @Override
    public GenericTransactionGroup processRefund(GenericTransactionGroup refundGroup) throws InternalReplicationFault {
        try {
            // check if paymentOut group is empty
            if (refundGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo() == null
                    || refundGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
                throw new Exception("Got an empty paymentOut transaction group from angel.");
            }

            // save payment out to database
            List<GenericReplicatedRecord> replicatedRecords = groupProcessor.processGroup(refundGroup);

            if (!StubUtils.hasSuccessStatus(replicatedRecords.get(0))) {
                throw new Exception("Willow was unable to save paymentOut transaction group.");
            }

            GenericReplicatedRecord paymentOutRecord = ReplicationUtils.replicatedPaymentOutRecord(replicatedRecords);
            PaymentOut paymentOutToProcess = paymentInService.paymentOutByAngelId(paymentOutRecord.getStub().getAngelId());

            if (paymentOutToProcess == null) {
                throw new Exception("The paymentOut record with angelId \"" + paymentOutRecord.getStub().getAngelId()
                        + "\" wasn't saved during the refund group processing.");
            }

            ObjectContext context = cayenneService.newNonReplicatingContext();
            PaymentOut paymentOut = context.localObject(paymentOutToProcess);

            paymentGatewayService.performGatewayOperation(paymentOut);

            // commit modifications to payment in nonreplicating context and immediatelly send transaction group back to angel

            context.commitChanges();

            GenericTransactionGroup group = PortHelper.createTransactionGroup(refundGroup);
            group.getGenericAttendanceOrBinaryDataOrBinaryInfo().addAll(transactionBuilder.createRefundTransaction(paymentOut,
                    PortHelper.getVersionByTransactionGroup(refundGroup)));

            return group;
        } catch (Exception e) {
            logger.error("Unable to process refund.", e);
            throw new InternalReplicationFault("Unable to process refund.", GENERIC_EXCEPTION,
                    String.format("Unable to process refund. Willow exception: %s", e.getMessage()));
        }
    }

    /**
     * Looks for paymentIn by sessionId, if payment is completed success/failed,
     * then returns soap stubs for paymentIn and related objects. Returns an
     * empty result otherwise.
     */
    @Override
    public GenericTransactionGroup getPaymentStatus(String sessionId, final SupportedVersions version) throws InternalReplicationFault {
        try {
            GenericTransactionGroup tGroup = PortHelper.createTransactionGroup(version);

            List<PaymentIn> pList = paymentInService.getPaymentsBySessionId(sessionId);
            List<PaymentIn> voucherPaymentsList = new ArrayList<PaymentIn>();

            boolean shouldWait = false;

            for (PaymentIn p : pList) {
                if (!p.isAsyncReplicationAllowed() && !PaymentExpressGatewayService.UNKNOW_RESULT_PAYMENT_IN.equals(p.getStatusNotes())) {
                    shouldWait = true;
                    break;
                } else {
                    voucherPaymentsList.addAll(PaymentInUtil.getRelatedVoucherPayments(p));
                }
            }

            if (!shouldWait) {
                //we should also add the voucher payments in this list
                pList.addAll(voucherPaymentsList);
                tGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().addAll(transactionBuilder.createPaymentInTransaction(pList, version));
            }

            return tGroup;
        } catch (Exception e) {
            logger.error("Unable to get payment status.", e);
            throw new InternalReplicationFault("Unable to get payment status.", GENERIC_EXCEPTION,
                    String.format("Unable to process refund. Willow exception: %s", e.getMessage()));
        }
    }

    /**
     * Retrieves vouchers matching voucher stubs received in request from willow db and returns back transaction group
     * consisting of voucher stubs generated for these freshly fetched stubs to enable angel to check if vouchers
     * being redeemed there are in consistent state with willow data. Vouchers which are currently in use by willow
     * (have linked payments in non final state) are not included into response transaction group.
     *
     * @throws InternalReplicationFault
     */
    @Override
    public GenericTransactionGroup getVouchers(GenericTransactionGroup transactionGroup, SupportedVersions version) throws InternalReplicationFault {
        try {
            List<Long> voucherIds = new ArrayList<>();
            for (GenericReplicationStub stub : transactionGroup.getReplicationStub()) {
                if (Voucher.class.getSimpleName().equalsIgnoreCase(stub.getEntityIdentifier())) {
                    voucherIds.add(stub.getWillowId());
                }
            }

            List<ProductItem> vouchers = voucherService.loadProductItemsByIds(voucherIds);

            GenericTransactionGroup response = PortHelper.createTransactionGroup(version);

            for (ProductItem productItem : vouchers) {
                Voucher voucher = (Voucher) productItem;
                if (!voucher.isInUse()) {
                    response.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(stubBuilder.convert(voucher, version));
                }
            }

            return response;
        } catch (Exception e) {
            logger.error("Unable to get voucher info.", e);
            throw new InternalReplicationFault("Unable to get voucher info.", GENERIC_EXCEPTION,
                    String.format("Unable to get voucher info. Willow exception: %s", e.getMessage()));
        }
    }

    /**
     * Transaction group which contains paymentIn and related enrolments only.
     *
     * @param model
     * @return
     */
    private GenericTransactionGroup plainPaymentEnrolmentResponse(PaymentInModel model, final SupportedVersions version) {
        GenericTransactionGroup response = PortHelper.createTransactionGroup(version);
        response.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(stubBuilder.convert(model.getPaymentIn(), version));
        for (Enrolment enrl : model.getEnrolments()) {
            response.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(stubBuilder.convert(enrl, version));
        }
        return response;
    }

    @Override
    public GenericParametersMap verifyUSI(GenericParametersMap parametersMap) throws InternalReplicationFault {
        USIVerificationRequest request = USIVerificationUtil.parseVerificationRequest(parametersMap);

        USIVerificationResult result = usiVerificationService.verifyUsi(request);

        SupportedVersions version = PortHelper.getVersionByParametersMap(parametersMap);

        return USIVerificationUtil.createVerificationResultParametersMap(result, version);
    }
}
