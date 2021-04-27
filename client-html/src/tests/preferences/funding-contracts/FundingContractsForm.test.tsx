import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import FundingContractsForm
  from "../../../js/containers/preferences/containers/funding-contracts/components/FundingContractsForm";

describe("Virtual rendered FundingContractsForm", () => {
  defaultComponents({
    entity: "FundingContractsForm",
    View: props => <FundingContractsForm {...props} />,
    record: mockedApi => mockedApi.db.fundingContracts,
    defaultProps: ({ initialValues }) => {

      const values = { fundingContracts: initialValues };

      return {
        form: "FundingContractsForm",
        initialValues: values,
        values,
        onSubmit: jest.fn(),
        showConfirm: jest.fn(),
        fundingContracts: initialValues
      };
    },
    render: (wrapper, initialValues) => {
      initialValues.forEach((fundingContract, index) => {
        expect(wrapper.find(`#funding-contracts-item-${index} div[id='fundingContracts[${index}].name'] input`).getDOMNode().value).toEqual(
          fundingContract.name
        );

        expect(wrapper.find(`#funding-contracts-item-${index} div[id='fundingContracts[${index}].flavour'] input`).getDOMNode().value).toEqual(
          fundingContract.flavour
        );

        expect(wrapper.find(`#funding-contracts-item-${index} input[type='checkbox']`).props().checked).toEqual(fundingContract.active);
      });
    }
  });
});
