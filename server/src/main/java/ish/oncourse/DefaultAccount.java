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

package ish.oncourse;

import ish.common.types.AccountType;
import ish.persistence.CommonPreferenceController;

import java.util.Arrays;
import java.util.List;

/**
 * Enum defining the default Accounts. It is used for database sanity checks only.
 */
public enum DefaultAccount {

	BANK("11100", "Cheque account", AccountType.ASSET, "account.default.bank.id"),
	DEBTORS("11500", "Trade debtors", AccountType.ASSET, "account.default.debtors.id"),
	GST("11600", "GST payable to suppliers", AccountType.ASSET, "account.default.gst.id"),


    PREPAID_FEES("21010", "Prepaid fees liability account", AccountType.LIABILITY, "account.prepaidFees.id"),
	VOUCHER_LIABILITY("21400", "Voucher liability", AccountType.LIABILITY, "account.default.voucherLiability.id"),
	TAX("21600", "GST receivable from customers", AccountType.LIABILITY, "account.default.tax.id"),


	STUDENT_ENROLMENTS("41000", "Student enrolments", AccountType.INCOME, "account.default.studentEnrolments.id"),
    VOUCHERS_EXPIRED("45387", "Vouchers expired", AccountType.INCOME, "account.default.vouchersExpired.id"),

	DISCOUNT("54020", "Discounts given", AccountType.COS, "account.discount.id"),

	VOUCHER_UNDERPAYMENT("65387", "Voucher underpayment", AccountType.EXPENSE, "account.default.voucherUnderpayment.id");

	private String code, description, preferenceName;
	private AccountType type;

	public static final List<String> defaultAccountPreferences = Arrays.asList(
			DefaultAccount.DEBTORS.getPreferenceName(),
			DefaultAccount.BANK.getPreferenceName(),
			DefaultAccount.TAX.getPreferenceName(),
			DefaultAccount.STUDENT_ENROLMENTS.getPreferenceName(),
			DefaultAccount.PREPAID_FEES.getPreferenceName(),
			DefaultAccount.VOUCHER_LIABILITY.getPreferenceName(),
			DefaultAccount.VOUCHER_UNDERPAYMENT.getPreferenceName(),
			DefaultAccount.VOUCHER_UNDERPAYMENT.getPreferenceName()
	);

	DefaultAccount(String code, String description, AccountType type, String preferenceName) {
		this.code = code;
		this.description = description;
		this.type = type;
		this.preferenceName = preferenceName;
	}

	public String  getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getPreferenceName() {
		return preferenceName;
	}

	public AccountType getType() {
		return type;
	}

	public static Long getDefaultAccountId(DefaultAccount defaultAccount) {
		return CommonPreferenceController.getController().getDefaultAccountId(defaultAccount.getPreferenceName());
	}

}
