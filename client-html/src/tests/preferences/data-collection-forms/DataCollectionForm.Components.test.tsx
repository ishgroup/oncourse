import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import DataCollectionForm, { parseDataCollectionFormData }
  from "../../../js/containers/preferences/containers/data-collection-forms/components/DataCollectionForm";

describe("Virtual rendered DataCollectionForm", () => {
  defaultComponents({
    entity: "DataCollectionForm",
    View: props => <DataCollectionForm {...props} />,
    record: mockedApi => mockedApi.db.dataCollectionForms,
    defaultProps: ({ initialValues }) => {
      const values = {
        form: initialValues[0],
        items: parseDataCollectionFormData(initialValues[0])
      };

      return {
        form: "DataCollectionForm",
        initialValues: values,
        values,
        collectionForms: initialValues,
        fetch: false,
        match: {
          params: {
            action: "edit",
            id: values.form.id,
            type: values.form.type
          }
        },
        history: {
          listen: jest.fn()
        }
      };
    },
    render: (wrapper, initialValues) => {
      const form = initialValues[0];
      const items = parseDataCollectionFormData(form);

      expect(wrapper.find("h6[id='form.type']").text()).toContain(form.type === "Survey" ? "Student Feedback" : form.type);

      if (form.type === "Survey") {
        expect(wrapper.find("div[id='form.deliverySchedule']").text()).toContain(form.deliverySchedule);
      }

      items.forEach((field, key) => {
        const warpperId = `#data-collection-form-${key}`;
        if (field.baseType === "field") {
          expect(wrapper.find(`${warpperId} div[id="items[${key}].label"]`).text()).toContain(field.label);
          expect(wrapper.find(`${warpperId} div[id="items[${key}].helpText"]`).text()).toContain(field.helpText);
          expect(wrapper.find(`${warpperId} h6[id="items[${key}].type"]`).text()).toContain(field.type.label);
          expect(wrapper.find(`${warpperId} input[type="checkbox"]`).at(0).props().checked).toEqual(field.mandatory);
        } else {
          expect(wrapper.find(`${warpperId} div[id="items[${key}].name"]`).text()).toContain(field.name);
          expect(wrapper.find(`${warpperId} div[id="items[${key}].description"]`).text()).toContain(field.description);
        }
      });
    }
  });
});
