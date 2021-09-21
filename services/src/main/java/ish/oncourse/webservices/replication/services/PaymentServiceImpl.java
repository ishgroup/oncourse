/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.webservices.replication.services;

import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.enrol.IEnrolmentService;
import ish.oncourse.services.payment.IPaymentService;
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
import ish.oncourse.webservices.CheckoutValidationResult;
import ish.oncourse.webservices.ICheckoutVerificationService;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;
import java.util.stream.Collectors;

import static ish.oncourse.webservices.replication.services.ReplicationUtils.GENERIC_EXCEPTION;

public class PaymentServiceImpl implements InternalPaymentService {

    private static final Logger logger = LogManager.getLogger();

    private final IVoucherService voucherService;

    private final ICheckoutVerificationService checkoutVerificationService;

    private final IWillowStubBuilder stubBuilder;

    @Inject
    public PaymentServiceImpl(IVoucherService voucherService, ICheckoutVerificationService checkoutVerificationService, IWillowStubBuilder stubBuilder) {
        super();
        this.voucherService = voucherService;
        this.checkoutVerificationService = checkoutVerificationService;
        this.stubBuilder = stubBuilder;
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

    

    @Override
    public GenericParametersMap verifyCheckout(GenericParametersMap verificationRequest, SupportedVersions version) throws InternalReplicationFault {

        Map<Long, List<Long>> classContacts = new HashMap<>();

        verificationRequest.getEntry().forEach(entry -> {
            classContacts.put(Long.valueOf(entry.getName()), Arrays.stream(entry.getValue().split(",")).map(Long::valueOf).collect(Collectors.toList()));
        });

        List<CheckoutValidationResult> result = checkoutVerificationService.verify(classContacts);
        GenericParametersMap response = PortHelper.createParametersMap(version);

        result.forEach( error -> {
            GenericParameterEntry entry = PortHelper.createParameterEntry(version);
            entry.setName(String.format("%d-%d",error.getCourseClassId(), error.getContactId()));
            entry.setValue(error.getError());
            response.getGenericEntry().add(entry);

        });

        return response;
    }
}
