/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.common.types.AccountType
import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.DefaultAccount
import ish.oncourse.server.CayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.dao.AccountDao
import ish.oncourse.server.api.v1.model.AccountDTO
import ish.oncourse.server.api.v1.model.AccountTypeDTO
import ish.oncourse.server.cayenne.*
import ish.util.PaymentMethodUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import java.time.ZoneOffset
import java.util.stream.Collectors

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.TaxFunctions.toRestTaxMinimized
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

class AccountApiService extends EntityApiService<AccountDTO, Account, AccountDao>{

    @Inject
    private CayenneService cayenneService


    @Override
    Class<Account> getPersistentClass() {
        return Account
    }

    @Override
    AccountDTO toRestModel(Account account) {
        new AccountDTO().with { accountDTO ->
            accountDTO.id = account.id
            accountDTO.accountCode = account.accountCode
            accountDTO.isEnabled = account.isEnabled
            accountDTO.description = account.description
            accountDTO.type = AccountTypeDTO.fromValue(account.type.displayName)
            accountDTO.isDefaultAccount = isDefaultAccount(account)
            accountDTO.createdOn = account.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            accountDTO.modifiedOn = account.modifiedOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            if (account.tax) {
                accountDTO.tax = toRestTaxMinimized(account.tax)
            }
            accountDTO
        }
    }


    List<AccountDTO> getDepositAccounts() {
        return (ObjectSelect.query(Account.class)
                .where(Account.PAYMENTS.dot(PaymentIn.BANKING).isNull())
                .and(Account.PAYMENTS.dot(PaymentIn.STATUS).eq(PaymentStatus.SUCCESS))
                .and(Account.PAYMENTS.dot(PaymentIn.AMOUNT).nin(Money.ZERO))
                .and(Account.PAYMENTS.dot(PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.TYPE)).nin(PaymentMethodUtil.SYSTEM_TYPES))
                .select(cayenneService.newContext) +
                ObjectSelect.query(Account.class)
                        .where(Account.PAYMENTS_OUT.dot(PaymentOut.BANKING).isNull())
                        .and(Account.PAYMENTS_OUT.dot(PaymentOut.STATUS).eq(PaymentStatus.SUCCESS))
                        .and(Account.PAYMENTS_OUT.dot(PaymentOut.AMOUNT).gt(Money.ZERO))
                        .select(cayenneService.newContext))
                .unique { it.id }
                .stream()
                .map{ toRestModel(it) }
                .collect(Collectors.toList())
    }

    private static boolean isDefaultAccount(Account account) {
        List<Long> usedDefaultAccounts = new ArrayList<>()
        for (DefaultAccount acc : DefaultAccount.values()) {
            usedDefaultAccounts.add(PreferenceController.getController().getDefaultAccountId(acc.getPreferenceName()))
        }
        return usedDefaultAccounts.contains(account.getId())
    }

    @Override
    Account toCayenneModel(AccountDTO accountDTO, Account account) {
        account.accountCode = trimToNull(accountDTO.accountCode)
        account.isEnabled = accountDTO.isEnabled
        account.description = trimToNull(accountDTO.description)
        account.type = AccountType.values().find {it.displayName.equals(accountDTO.type.toString())}
        if (accountDTO.tax?.id) {
            account.tax = getRecordById(account.context, Tax, accountDTO.tax.id)
        }
        if ((account.tax != null) && account.type != AccountType.INCOME) {
            account.tax = null
        }
        account
    }

    @Override
    void validateModelBeforeSave(AccountDTO accountDTO, ObjectContext context, Long id) {
        if (isBlank(accountDTO.accountCode)) {
            validator.throwClientErrorException(accountDTO?.id,  'accountCode', 'Code is required.')
        } else if (trimToNull(accountDTO.accountCode).size() > 40) {
            validator.throwClientErrorException(accountDTO?.id,  'accountCode', 'Code cannot be more than 40 chars.')
        }

        def account = ObjectSelect.query(Account)
                .where(Account.ACCOUNT_CODE.eq(trimToNull(accountDTO.accountCode)))
                .selectOne(context)

        if (account && account.id != id) {
            validator.throwClientErrorException(accountDTO?.id,  'accountCode', 'Code must be unique.')
        }

        if (accountDTO.isEnabled == null) {
            validator.throwClientErrorException(accountDTO?.id,  'isEnabled', 'Enabled value is required.')
        }

        if (isBlank(accountDTO.description)) {
            validator.throwClientErrorException(accountDTO?.id,  'accountCode', 'Description is required.')
        } else if (trimToNull(accountDTO.description).size() > 500) {
            validator.throwClientErrorException(accountDTO?.id,  'accountCode', 'Description cannot be more than 500 chars.')
        }

        if (!accountDTO.type) {
            validator.throwClientErrorException(accountDTO?.id,  'type', 'Account type is required.')
        } else if (account && accountDTO.type != AccountTypeDTO.fromValue(account.type.displayName)) {
            validator.throwClientErrorException(accountDTO?.id,  'type', 'Cannot change account type.')
        }
        if ((account != null) && (isDefaultAccount(account)) && !accountDTO.isEnabled) {
            validator.throwClientErrorException(accountDTO?.id,  'isEnabled', 'Cannot disable default account.')
        }

        if (accountDTO.type == AccountTypeDTO.INCOME) {
            if (!accountDTO.tax?.id) {
                validator.throwClientErrorException(accountDTO?.id,  'type', 'Tax is required for income account.')
            } else if (!getRecordById(context, Tax, accountDTO.tax.id)) {
                validator.throwClientErrorException(accountDTO?.id,  'type', "Cannot find tax with id = ${accountDTO.tax.id}.")
            }
        }

    }

    @Override
    void validateModelBeforeRemove(Account account) {
        if (!account.transactions.empty) {
            validator.throwClientErrorException(account?.id, 'id', 'Cannot delete account with transactions.')
        }
        if (!account.bankings.empty) {
            validator.throwClientErrorException(account?.id, 'id', 'Cannot delete account assigned to banking.')
        }
        if (!account.courseClasses.empty) {
            validator.throwClientErrorException(account?.id, 'id', 'Cannot delete account assigned to classes.')
        }
        if (!account.discounts.empty) {
            validator.throwClientErrorException(account?.id, 'id', 'Cannot delete account assigned to discounts.')
        }
        if (!account.invoices.empty || !account.invoiceLines.empty || !account.prepaidInvoiceLines.empty) {
            validator.throwClientErrorException(account?.id, 'id', 'Cannot delete account assigned to invoices.')
        }
        if (!account.payableTaxes.empty || !account.receivableTaxes.empty) {
            validator.throwClientErrorException(account?.id, 'id', 'Cannot delete account assigned to taxes.')
        }
        if (!account.paymentInLines.empty || !account.payments.empty) {
            validator.throwClientErrorException(account?.id, 'id', 'Cannot delete account assigned to payments in.')
        }
        if (!account.paymentMethods.empty) {
            validator.throwClientErrorException(account?.id, 'id', 'Cannot delete account assigned to payment methods.')
        }
        if (!account.paymentOutLines.empty || !account.paymentsOut.empty) {
            validator.throwClientErrorException(account?.id, 'id', 'Cannot delete account assigned to payments out.')
        }
        if (!account.products.empty || !account.vouchers.empty) {
            validator.throwClientErrorException(account?.id, 'id', 'Cannot delete account assigned to vouchers/products.')
        }
        if (isDefaultAccount(account)) {
            validator.throwClientErrorException(account?.id, 'id', 'Cannot delete default account.')
        }
    }
}
