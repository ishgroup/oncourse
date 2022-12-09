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

package ish.oncourse.server.api.v1.function

import ish.common.types.AccountType
import ish.oncourse.DefaultAccount
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.BidiMap
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.TaxFunctions.toRestTaxMinimized
import ish.oncourse.server.api.v1.model.AccountDTO
import ish.oncourse.server.api.v1.model.AccountTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.Tax
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.ZoneOffset

class AccountFunctions {

    private static final BidiMap<AccountType, AccountTypeDTO> accountTypeMap = new BidiMap<AccountType, AccountTypeDTO>() {{
        put(AccountType.ASSET, AccountTypeDTO.ASSET)
        put(AccountType.LIABILITY, AccountTypeDTO.LIABILITY)
        put(AccountType.EQUITY, AccountTypeDTO.EQUITY)
        put(AccountType.INCOME, AccountTypeDTO.INCOME)
        put(AccountType.COS, AccountTypeDTO.COS)
        put(AccountType.EXPENSE, AccountTypeDTO.EXPENSE)
    }}

    static AccountDTO toRestAccount(Account account) {
        new AccountDTO().with { accountDTO ->
            accountDTO.id = account.id
            accountDTO.accountCode = account.accountCode
            accountDTO.isEnabled = account.isEnabled
            accountDTO.description = account.description
            accountDTO.type = accountTypeMap.get(account.type)
            accountDTO.isDefaultAccount = isDefaultAccount(account)
            accountDTO.createdOn = account.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            accountDTO.modifiedOn = account.modifiedOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            if (account.tax) {
                accountDTO.tax = toRestTaxMinimized(account.tax)
            }
            accountDTO
        }
    }

    static AccountDTO toRestAccountMinimized(Account account) {
        new AccountDTO().with { accountDTO ->
            accountDTO.id = account.id
            accountDTO.accountCode = account.accountCode
            accountDTO.description = account.description
            accountDTO
        }
    }

    static ValidationErrorDTO validateForDelete(Account account) {
        if (!account.transactions.empty) {
            return new ValidationErrorDTO(account?.id?.toString(), 'id', 'Cannot delete account with transactions.')
        }
        if (!account.bankings.empty) {
            return new ValidationErrorDTO(account?.id?.toString(), 'id', 'Cannot delete account assigned to banking.')
        }
        if (!account.courseClasses.empty) {
            return new ValidationErrorDTO(account?.id?.toString(), 'id', 'Cannot delete account assigned to classes.')
        }
        if (!account.discounts.empty) {
            return new ValidationErrorDTO(account?.id?.toString(), 'id', 'Cannot delete account assigned to discounts.')
        }
        if (!account.invoices.empty || !account.invoiceLines.empty || !account.prepaidInvoiceLines.empty) {
            return new ValidationErrorDTO(account?.id?.toString(), 'id', 'Cannot delete account assigned to invoices.')
        }
        if (!account.payableTaxes.empty || !account.receivableTaxes.empty) {
            return new ValidationErrorDTO(account?.id?.toString(), 'id', 'Cannot delete account assigned to taxes.')
        }
        if (!account.paymentInLines.empty || !account.payments.empty) {
            return new ValidationErrorDTO(account?.id?.toString(), 'id', 'Cannot delete account assigned to payments in.')
        }
        if (!account.paymentMethods.empty) {
            return new ValidationErrorDTO(account?.id?.toString(), 'id', 'Cannot delete account assigned to payment methods.')
        }
        if (!account.paymentOutLines.empty || !account.paymentsOut.empty) {
            return new ValidationErrorDTO(account?.id?.toString(), 'id', 'Cannot delete account assigned to payments out.')
        }
        if (!account.products.empty || !account.vouchers.empty) {
            return new ValidationErrorDTO(account?.id?.toString(), 'id', 'Cannot delete account assigned to vouchers/products.')
        }
        if (isDefaultAccount(account)) {
            return new ValidationErrorDTO(account?.id?.toString(), 'id', 'Cannot delete default account.')
        }
        null
    }

    static ValidationErrorDTO validateForSave(AccountDTO accountDTO, ObjectContext context, Account account = null) {
        if (isBlank(accountDTO.accountCode)) {
            return new ValidationErrorDTO(accountDTO?.id?.toString(),  'accountCode', 'Code is required.')
        } else if (trimToNull(accountDTO.accountCode).size() > 40) {
            return new ValidationErrorDTO(accountDTO?.id?.toString(),  'accountCode', 'Code cannot be more than 40 chars.')
        }
        Long accountId = ObjectSelect.query(Account)
                .where(Account.ACCOUNT_CODE.eq(trimToNull(accountDTO.accountCode)))
                .selectOne(context)?.id
        if (accountId && accountId != account?.id) {
            return new ValidationErrorDTO(accountDTO?.id?.toString(),  'accountCode', 'Code must be unique.')
        }

        if (accountDTO.isIsEnabled() == null) {
            return new ValidationErrorDTO(accountDTO?.id?.toString(),  'isEnabled', 'Enabled value is required.')
        }
        if (isBlank(accountDTO.description)) {
            return new ValidationErrorDTO(accountDTO?.id?.toString(),  'accountCode', 'Description is required.')
        } else if (trimToNull(accountDTO.description).size() > 500) {
            return new ValidationErrorDTO(accountDTO?.id?.toString(),  'accountCode', 'Description cannot be more than 500 chars.')
        }
        if (!accountDTO.type) {
            return new ValidationErrorDTO(accountDTO?.id?.toString(),  'type', 'Account type is required.')
        } else if (account && account.type != accountTypeMap.getByValue(accountDTO.type)) {
            return new ValidationErrorDTO(accountDTO?.id?.toString(),  'type', 'Cannot change account type.')
        }
        if ((account != null) && (isDefaultAccount(account)) && !accountDTO.isIsEnabled()) {
            return new ValidationErrorDTO(accountDTO?.id?.toString(),  'isEnabled', 'Cannot disable default account.')
        }

        if (accountDTO.type == AccountTypeDTO.INCOME) {
            if (!accountDTO.tax?.id) {
                return new ValidationErrorDTO(accountDTO?.id?.toString(),  'type', 'Tax is required for income account.')
            } else if (!getRecordById(context, Tax, accountDTO.tax.id)) {
                return new ValidationErrorDTO(accountDTO?.id?.toString(),  'type', "Cannot find tax with id = ${accountDTO.tax.id}.")
            }
        }

        null
    }

    static boolean isDefaultAccount(Account account) {
        List<Long> usedDefaultAccounts = new ArrayList<>()
        for (DefaultAccount acc : DefaultAccount.values()) {
            usedDefaultAccounts.add(PreferenceController.getController().getDefaultAccountId(acc.getPreferenceName()))
        }
        return usedDefaultAccounts.contains(account.getId())
    }

    static Account toDbAccount(AccountDTO accountDTO, Account account, ObjectContext context) {
        account.accountCode = trimToNull(accountDTO.accountCode)
        account.isEnabled = accountDTO.isIsEnabled()
        account.description = trimToNull(accountDTO.description)
        account.type = accountTypeMap.getByValue(accountDTO.type)
        if (accountDTO.tax?.id) {
            account.tax = getRecordById(context, Tax, accountDTO.tax.id)
        }
        if ((account.tax != null) && account.type != AccountType.INCOME) {
            account.tax = null
        }
        account
    }
}
