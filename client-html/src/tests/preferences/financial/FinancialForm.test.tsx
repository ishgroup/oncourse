import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import Financial from "../../../js/containers/preferences/containers/financial/Financial";
import { preferencesFormRole } from "../../../js/containers/preferences/containers/FormContainer";
import { dashedCase } from "../../common/utils";

describe("Virtual rendered FinancialForm", () => {
  defaultComponents({
    entity: "FinancialForm",
    View: props => <Financial {...props} />,
    record: () => ({}),
    defaultProps: () => ({}),
    render: ({ screen, fireEvent }) => {
      const avetmissFormData = dashedCase({
        [PreferencesModel.PaymentInfo.uniqueKey]: mockedAPI.db.preference[PreferencesModel.PaymentInfo.uniqueKey].toString(),
        [PreferencesModel.AccountDebtors.uniqueKey]: mockedAPI.db.preference[PreferencesModel.AccountDebtors.uniqueKey].toString(),
        [PreferencesModel.AccountBank.uniqueKey]: mockedAPI.db.preference[PreferencesModel.AccountBank.uniqueKey].toString(),
        [PreferencesModel.AccountTax.uniqueKey]: mockedAPI.db.preference[PreferencesModel.AccountTax.uniqueKey].toString(),
        [PreferencesModel.AccountStudentEnrolments.uniqueKey]: mockedAPI.db.preference[PreferencesModel.AccountStudentEnrolments.uniqueKey].toString(),
        [PreferencesModel.AccountPrepaidFees.uniqueKey]: mockedAPI.db.preference[PreferencesModel.AccountPrepaidFees.uniqueKey].toString(),
        [PreferencesModel.AccountPrepaidFeesPostAt.uniqueKey]: mockedAPI.db.preference[PreferencesModel.AccountPrepaidFeesPostAt.uniqueKey].toString(),
        [PreferencesModel.AccountVoucherLiability.uniqueKey]: mockedAPI.db.preference[PreferencesModel.AccountVoucherLiability.uniqueKey].toString(),
        [PreferencesModel.AccountVoucherUnderpayment.uniqueKey]: mockedAPI.db.preference[PreferencesModel.AccountVoucherUnderpayment.uniqueKey].toString(),
        [PreferencesModel.AccountDefaultCurrency.uniqueKey]: mockedAPI.db.preference[PreferencesModel.AccountDefaultCurrency.uniqueKey].toString(),
        [PreferencesModel.AccountInvoiceTerms.uniqueKey]: Number(mockedAPI.db.preference[PreferencesModel.AccountInvoiceTerms.uniqueKey]),
      });
      
      fireEvent.click(screen.getByTestId('appbar-submit-button'));

      setTimeout(() => {
        expect(screen.getByRole(preferencesFormRole)).toHaveFormValues(avetmissFormData);
      }, 500);
    }
  });
});
