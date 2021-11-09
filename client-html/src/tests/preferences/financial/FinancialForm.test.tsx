import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import Financial from "../../../js/containers/preferences/containers/financial/Financial";

// TODO Enable test on fix

describe("Virtual rendered FinancialForm", () => {
  defaultComponents({
    entity: "FinancialForm",
    View: props => <Financial {...props} />,
    record: () => ({}),
    defaultProps: () => ({}),
    render: wrapper => {
      // expect(wrapper.find("#college-paymentInfo textarea").val()).toEqual(
      //   mockedAPI.db.preference[PreferencesModel.PaymentInfo.uniqueKey].toString()
      // );
      //
      // expect(wrapper.find("#account-default-debtors-id input").val()).toEqual(
      //   mockedAPI.db.preference[PreferencesModel.AccountDebtors.uniqueKey].toString()
      // );
      //
      // expect(wrapper.find("#account-default-bank-id input").val()).toEqual(
      //   mockedAPI.db.preference[PreferencesModel.AccountBank.uniqueKey].toString()
      // );
      //
      // expect(wrapper.find("#account-default-tax-id input").val()).toEqual(
      //   mockedAPI.db.preference[PreferencesModel.AccountTax.uniqueKey].toString()
      // );
      //
      // expect(wrapper.find("#account-default-studentEnrolments-id input").val()).toEqual(
      //   mockedAPI.db.preference[PreferencesModel.AccountStudentEnrolments.uniqueKey].toString()
      // );
      //
      // expect(wrapper.find("#account-prepaidFees-id input").val()).toEqual(
      //   mockedAPI.db.preference[PreferencesModel.AccountPrepaidFees.uniqueKey].toString()
      // );
      //
      // expect(wrapper.find("#account-prepaidFeesPostAt input").val()).toEqual(
      //   mockedAPI.db.preference[PreferencesModel.AccountPrepaidFeesPostAt.uniqueKey].toString()
      // );
      //
      // expect(wrapper.find("#account-default-voucherLiability-id input").val()).toEqual(
      //   mockedAPI.db.preference[PreferencesModel.AccountVoucherLiability.uniqueKey].toString()
      // );
      //
      // expect(wrapper.find("#account-default-voucherUnderpayment-id input").val()).toEqual(
      //   mockedAPI.db.preference[PreferencesModel.AccountVoucherUnderpayment.uniqueKey].toString()
      // );
      //
      // expect(wrapper.find("#default-currency input").val()).toEqual(
      //   mockedAPI.db.preference[PreferencesModel.AccountDefaultCurrency.uniqueKey].toString()
      // );
      //
      // expect(wrapper.find("#account-invoice-terms input").val()).toEqual(
      //   mockedAPI.db.preference[PreferencesModel.AccountInvoiceTerms.uniqueKey].toString()
      // );
    }
  });
});
