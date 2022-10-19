import { PreferenceSchema } from "./PreferencesSchema";

export const AccountBank: PreferenceSchema = {
  uniqueKey: "account.default.bank.id",
  mandatory: false,
  editable: true
};

export const AccountDebtors: PreferenceSchema = {
  uniqueKey: "account.default.debtors.id",
  mandatory: false,
  editable: true
};

export const AccountDefaultCurrency: PreferenceSchema = {
  uniqueKey: "default.currency",
  mandatory: false,
  editable: true
};

export const AccountInvoiceTerms: PreferenceSchema = {
  uniqueKey: "account.invoice.terms",
  mandatory: false,
  editable: true
};

export const AccountPrepaidFees: PreferenceSchema = {
  uniqueKey: "account.prepaidFees.id",
  mandatory: false,
  editable: true
};

export const AccountPrepaidFeesPostAt: PreferenceSchema = {
  uniqueKey: "account.prepaidFeesPostAt",
  mandatory: false,
  editable: true
};

export const AccountStudentEnrolments: PreferenceSchema = {
  uniqueKey: "account.default.studentEnrolments.id",
  mandatory: false,
  editable: true
};

export const AccountTax: PreferenceSchema = {
  uniqueKey: "account.default.tax.id",
  mandatory: false,
  editable: true
};

export const AccountVoucherLiability: PreferenceSchema = {
  uniqueKey: "account.default.voucherLiability.id",
  mandatory: false,
  editable: true
};

export const AccountVoucherUnderpayment: PreferenceSchema = {
  uniqueKey: "account.default.voucherUnderpayment.id",
  mandatory: false,
  editable: true
};

export const PaymentInfo: PreferenceSchema = {
  uniqueKey: "college.paymentInfo",
  mandatory: false,
  editable: true
};
