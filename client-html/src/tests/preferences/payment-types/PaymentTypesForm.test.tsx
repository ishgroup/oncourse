import * as React from "react";
import { AccountType } from "@api/model";
import { defaultComponents } from "../../common/Default.Components";
import PaymentTypesForm, { PAYMENT_TYPES_FORM } from
    "../../../js/containers/preferences/containers/payment-types/components/PaymentTypesForm";
import { sortDefaultSelectItems } from "../../../js/common/utils/common";
import getTimestamps from "../../../js/common/utils/timestamps/getTimestamps";
import { AccountExtended } from "../../../js/model/entities/Account";

describe("Virtual rendered PaymentTypesForm", () => {
  defaultComponents({
    entity: PAYMENT_TYPES_FORM,
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
        form: PAYMENT_TYPES_FORM,
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
    render: ({ screen, initialValues, fireEvent }) => {
      const paymentTypes = {};

      initialValues.forEach((value, key) => {
        paymentTypes[`types[${key}].name`] = value.name;
        paymentTypes[`types[${key}].active`] = value.active;
        paymentTypes[`types[${key}].bankedAuto`] = value.bankedAuto;
        paymentTypes[`types[${key}].reconcilable`] = value.reconcilable;

        if (!value.systemType) {
          paymentTypes[`types[${key}].type`] = value.type;
        }
      });

      expect(screen.getByRole(PAYMENT_TYPES_FORM)).toHaveFormValues(paymentTypes);

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
