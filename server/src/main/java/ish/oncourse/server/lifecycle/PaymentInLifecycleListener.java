/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.lifecycle;

import com.google.inject.Inject;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.util.AccountUtil;
import org.apache.cayenne.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

/**
 */
public class PaymentInLifecycleListener {

	private static final Logger logger = LogManager.getLogger();

	private final PreferenceController prefController;

	@Inject
	public PaymentInLifecycleListener(PreferenceController prefController) {
		this.prefController = prefController;
	}

	@PostAdd(value = PaymentIn.class)
	public void postAdd(PaymentIn entity) {
		var aContext = entity.getContext();

		if (entity.getAccountIn() == null) {
			entity.setAccountIn(AccountUtil.getDefaultBankAccount(aContext, Account.class));
		}
	}

    /**
     * The code sets correct account for willow payments which are created during voucher redemption process on willow side.
     */
    @PrePersist(value = PaymentIn.class)
    public void prePersist(PaymentIn paymentIn) {
        if (PaymentType.VOUCHER.equals(paymentIn.getPaymentMethod().getType()) &&
                PaymentSource.SOURCE_WEB.equals(paymentIn.getSource())) {

            /**
             * The PaymentIn should have at least VoucherPaymentIn. So if  the PaymentIn does not have VoucherPaymentIn the code throw
             * RuntimeException and these changes will not be added to the angel db.
             */
			var vp = paymentIn.getVoucherPayments().get(0);

            /**
             * A PaymentIn can have one or many VoucherPaymentIn but all of them (VoucherPayments) are linked to the same voucher.
             */
            paymentIn.setAccountIn(vp.getVoucher().getVoucherProduct().getLiabilityAccount());
        }

		PaymentHelper.tryToAssignBankingAutomatically(paymentIn);
    }

    /**
     * @see org.apache.cayenne.LifecycleListener#postPersist(Object)
     */
    @PostPersist(value = PaymentIn.class)
    public void postPersist(PaymentIn entity) {
        try {
            if (PaymentStatus.NEW.equals(entity.getStatus())) {
                if (!prefController.getReplicationEnabled()) {
                    entity.succeed();
                }
            }
            // touch each related invoice to update amount owing
            entity.updateAmountsOwing(entity.getContext());
            entity.getContext().commitChanges();
        } catch (Throwable e) {
            // surround with 'try catch' and add exception logging here
            // because we have a case when paymentIn was commited in db without status (status == NULL, postPersist()  method tripped not properly) and no error logs - see #23799.
            logger.error("Some unexpected was happened. PaymentIn with ID:{} has not been processed completely. Please verify status for payment manually", entity.getId(), e);
        }
    }

	/**
	 * @see org.apache.cayenne.LifecycleListener#preUpdate(Object)
	 */
	@PreUpdate(value = PaymentIn.class)
	public void preUpdate(PaymentIn paymentIn) {
		var date = new Date();
		// touch all payment lines, in case the status of the payment is set to success, then the account transactions have to be created in relevant trigger
		if (paymentIn.getPaymentInLines() != null) {
			for (var pinl : paymentIn.getPaymentInLines()) {
				pinl.setModifiedOn(date);
			}
		}

		PaymentHelper.tryToAssignBankingAutomatically(paymentIn);
	}

	/**
	 * @see org.apache.cayenne.LifecycleListener#postUpdate(Object)
	 */
	@PostUpdate(value = PaymentIn.class)
	public void postUpdate(PaymentIn paymentIn) {

		// touch each related invoice to update amount owing
		paymentIn.updateAmountsOwing(paymentIn.getContext());
		paymentIn.getContext().commitChanges();
	}
}
