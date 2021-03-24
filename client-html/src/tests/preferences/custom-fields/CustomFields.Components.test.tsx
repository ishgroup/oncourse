import * as React from "react";
import getTimestamps from "../../../js/common/utils/timestamps/getTimestamps";
import CustomFieldsForm from "../../../js/containers/preferences/containers/custom-fields/components/CustomFieldsForm";
import { defaultComponents } from "../../common/Default.Components";

describe("Virtual rendered CustomFieldTypes", () => {
  defaultComponents({
    entity: "CustomFieldType",
    View: props => <CustomFieldsForm {...props} />,
    record: mockedApi => mockedApi.db.customFields,
    defaultProps: ({ initialValues }) => ({
      form: "CustomFieldsForm",
      data: { types: initialValues },
      customFields: initialValues,
      timestamps: getTimestamps(initialValues),
      fetch: false
    }),
    render: (wrapper, initialValues) => {
      initialValues.forEach((type, key) => {
        const warpperId = `#custom-field-${key}`;
        expect(wrapper.find(`${warpperId} div[id="types[${key}].name"]`).text()).toContain(type.name);
        expect(wrapper.find(`${warpperId} div[id="types[${key}].fieldKey"]`).text()).toContain(type.fieldKey);
        expect(wrapper.find(`${warpperId} div[id="types[${key}].dataType"]`).text()).toContain(type.dataType);
        expect(wrapper.find(`${warpperId} div[id="types[${key}].entityType"]`).text()).toContain(type.entityType);
        expect(wrapper.find(`${warpperId} input[type="checkbox"]`).at(0).props().checked).toEqual(type.mandatory);
      });
    }
  });
});
