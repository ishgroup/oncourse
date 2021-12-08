import * as React from "react";
import getTimestamps from "../../../js/common/utils/timestamps/getTimestamps";
import CustomFieldsForm from "../../../js/containers/preferences/containers/custom-fields/components/CustomFieldsForm";
import { defaultComponents } from "../../common/Default.Components";

// TODO Enable test on fix

describe.skip("Virtual rendered CustomFieldTypes", () => {
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
    render: (wrapper, initialValues, shallow) => {
      initialValues.forEach((type, key) => {
        const warpperId = `#custom-field-${key}`;
        shallow.find(`${warpperId} #expand-button-custom-field-${key}`).simulate("click");

        expect(wrapper.find(`${warpperId} div[id="types[${key}].name"] input`).val()).toContain(type.name);
        expect(wrapper.find(`${warpperId} div[id="types[${key}].fieldKey"] input`).val()).toContain(type.fieldKey);
        expect(wrapper.find(`${warpperId} div[id="types[${key}].dataType"] input`).val()).toContain(type.dataType);
        expect(wrapper.find(`${warpperId} div[id="types[${key}].entityType"] input`).val()).toContain(type.entityType);
      });
    }
  });
});
