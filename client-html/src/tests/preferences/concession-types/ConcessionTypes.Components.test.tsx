import * as React from "react";
import getTimestamps from "../../../js/common/utils/timestamps/getTimestamps";
import ConcessionTypesForm, { CONCESSION_TYPES_FORM }
  from "../../../js/containers/preferences/containers/concession-types/components/ConcessionTypesForm";
import { defaultComponents } from "../../common/Default.Components";

describe("Virtual rendered ConcessionTypes", () => {
  defaultComponents({
    entity: "ConcessionTypes",
    View: props => <ConcessionTypesForm {...props} />,
    record: mockedApi => mockedApi.db.concessionTypes,
    defaultProps: ({ initialValues }) => ({
      form: CONCESSION_TYPES_FORM,
      data: { types: initialValues },
      concessionTypes: initialValues,
      timestamps: getTimestamps(initialValues),
      fetch: false
    }),
    render: ({ screen, initialValues, fireEvent }) => {
      const consessionTypes = {};

      initialValues.forEach((types, index) => {
        consessionTypes[`types[${index}].name`] = types.name;
        consessionTypes[`types[${index}].allowOnWeb`] = types.allowOnWeb;
        consessionTypes[`types[${index}].requireNumber`] = types.requireNumber;
        consessionTypes[`types[${index}].requireExpary`] = types.requireExpary;
      });

      expect(screen.getByRole(CONCESSION_TYPES_FORM)).toHaveFormValues(consessionTypes);

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
