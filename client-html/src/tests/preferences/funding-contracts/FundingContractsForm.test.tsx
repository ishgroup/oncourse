import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import FundingContractsForm, { FUNDING_CONTRACTS_FORM }
  from "../../../js/containers/preferences/containers/funding-contracts/components/FundingContractsForm";

describe("Virtual rendered FundingContractsForm", () => {
  defaultComponents({
    entity: FUNDING_CONTRACTS_FORM,
    View: props => <FundingContractsForm {...props} />,
    record: mockedApi => mockedApi.db.fundingContracts,
    defaultProps: ({ initialValues }) => {
      const values = { fundingContracts: initialValues };
      return {
        form: FUNDING_CONTRACTS_FORM,
        initialValues: values,
        values,
        onSubmit: jest.fn(),
        showConfirm: jest.fn(),
        fundingContracts: initialValues
      };
    },
    render: ({ screen, initialValues, fireEvent }) => {
      const fundingContracts = {};

      initialValues.forEach((fundingContract, key) => {
        fundingContracts[`fundingContracts[${key}].name`] = fundingContract.name;
        fundingContracts[`fundingContracts[${key}].flavour`] = fundingContract.flavour;
        fundingContracts[`fundingContracts[${key}].active`] = fundingContract.active;
      });

      expect(screen.getByRole(FUNDING_CONTRACTS_FORM)).toHaveFormValues(fundingContracts);

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
