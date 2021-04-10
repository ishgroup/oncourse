import * as React from "react";
import { AccountType } from "@api/model";
import { defaultComponents } from "../../common/Default.Components";
import PaymentTypesForm from "../../../js/containers/preferences/containers/payment-types/components/PaymentTypesForm";
import { sortDefaultSelectItems } from "../../../js/common/utils/common";
import getTimestamps from "../../../js/common/utils/timestamps/getTimestamps";
import { AccountExtended } from "../../../js/model/entities/Account";

describe("Virtual rendered PaymentTypesForm", () => {
  defaultComponents({
    entity: "PaymentTypesForm",
    View: props => <PaymentTypesForm {...props} />,
    record: mockedApi => mockedApi.db.paymentTypes,
    defaultProps: ({ initialValues, mockedApi }) => {
      const values = { paymentTypes: initialValues, types: initialValues };
      const accounts = mockedApi.db.getPlainAccounts();
      const items: AccountExtended[] = [];
      const timestamps = getTimestamps(initialValues);

      for (const row of accounts.rows) {
        items.push({
          id: Number(row.values[0]),
          description: row.values[1],
          accountCode: row.values[2],
          type: row.values[3] as AccountType,
          taxId: row.values[4]
        });
      }

      items.sort((a, b) => (a.description[0] > b.description[0] ? 1 : -1));

      const created = timestamps && timestamps[0];
      const modified = timestamps && timestamps[1];

      const assetAccounts = items
        && items
          .filter(account => account.type === "asset")
          .map(item => ({
            value: Number(item.id),
            label: `${item.description} ${item.accountCode}`
          }));

      assetAccounts.sort(sortDefaultSelectItems);

      return {
        form: "PaymentTypesForm",
        initialValues: values,
        values,
        data: values,
        paymentTypes: initialValues,
        assetAccounts,
        created,
        modified,
        fetch: false,
        onUpdate: jest.fn(),
        onDelete: jest.fn()
      };
    },
    render: (wrapper, initialValues) => {
      initialValues.forEach((value, index) => {
        expect(wrapper.find(`#payment-type-item-${index} div[id='types[${index}].name'] input`).getDOMNode().value)
          .toEqual(value.name);

        expect(wrapper.find(`#payment-type-item-${index} input[type='checkbox']`).at(0).props().checked).toEqual(value.active);
        expect(wrapper.find(`#payment-type-item-${index} input[type='checkbox']`).at(1).props().checked).toEqual(value.bankedAuto);
        expect(wrapper.find(`#payment-type-item-${index} input[type='checkbox']`).at(2).props().checked).toEqual(value.reconcilable);

        if (!value.systemType) {
          expect(wrapper.find(`#payment-type-item-${index} div[id='types[${index}].type'] input`).getDOMNode().value)
            .toEqual(value.type);
          expect(wrapper.find(`#payment-type-item-${index} div[id='types[${index}].undepositAccountId'] input`).getDOMNode().value)
            .toEqual(value.undepositAccountId.toString());
          expect(wrapper.find(`#payment-type-item-${index} div[id='types[${index}].accountId'] input`).getDOMNode().value)
            .toEqual(value.accountId.toString());
        }
      });
    }
  });
});
