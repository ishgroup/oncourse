import * as React from "react";
import getTimestamps from "../../../js/common/utils/timestamps/getTimestamps";
import CustomFieldsForm, { CUSTOM_FIELDS_FORM } from
    "../../../js/containers/preferences/containers/custom-fields/components/CustomFieldsForm";
import { defaultComponents } from "../../common/Default.Components";

describe("Virtual rendered CustomFieldTypes", () => {
  defaultComponents({
    entity: "CustomFieldType",
    View: props => <CustomFieldsForm {...props} />,
    record: mockedApi => mockedApi.db.customFields,
    defaultProps: ({ initialValues }) => ({
      form: CUSTOM_FIELDS_FORM,
      data: { types: initialValues },
      customFields: initialValues,
      timestamps: getTimestamps(initialValues),
      fetch: false
    }),
    render: ({ screen, initialValues, fireEvent }) => {
      const customFields = {};

      initialValues.forEach((type, key) => {
        fireEvent.click(screen.getByTestId(`expand-button-custom-field-${key}`));

        customFields[`types[${key}].name`] = type.name;
        customFields[`types[${key}].fieldKey`] = type.fieldKey;
        customFields[`types[${key}].dataType`] = type.dataType;
        customFields[`types[${key}].entityType`] = type.entityType;
        customFields[`types[${key}].mandatory`] = type.mandatory;
      });

      fireEvent.click(screen.getByTestId('appbar-submit-button'));

      setTimeout(() => {
        expect(screen.getByRole(CUSTOM_FIELDS_FORM)).toHaveFormValues(customFields);
      }, 500);
    }
  });
});
