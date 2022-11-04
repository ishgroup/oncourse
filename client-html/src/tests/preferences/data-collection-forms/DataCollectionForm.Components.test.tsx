import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import DataCollectionForm, { parseDataCollectionFormData, DATA_COLLECTION_FORM }
  from "../../../js/containers/preferences/containers/data-collection-forms/components/DataCollectionForm";

describe("Virtual rendered DataCollectionForm", () => {
  defaultComponents({
    entity: DATA_COLLECTION_FORM,
    View: props => <DataCollectionForm {...props} />,
    record: mockedApi => mockedApi.db.dataCollectionForms,
    defaultProps: ({ initialValues }) => {
      const values = {
        form: initialValues[0],
        items: parseDataCollectionFormData(initialValues[0])
      };

      return {
        form: DATA_COLLECTION_FORM,
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
    state: ({ viewProps }) => ({
      form: {
        [DATA_COLLECTION_FORM]: { values: viewProps.values }
      },
      preferences: {
        dataCollectionForms: viewProps.values.items
      }
    }),
    render: ({ screen, initialValues, fireEvent }) => {
      const form = initialValues[0];
      const items = parseDataCollectionFormData(form);

      expect(screen.getByText(form.type === "Survey" ? "Student Feedback" : form.type)).toBeTruthy();

      const dataCollections = {};

      if (form.type === "Survey") {
        dataCollections['form.deliverySchedule'] = form.deliverySchedule;
      }

      items.forEach((field, key) => {
        if (field.baseType === "field") {
          dataCollections[`items[${key}].label`] = field.label;
          dataCollections[`items[${key}].helpText`] = field.helpText;
          // dataCollections[`items[${key}].type`] = field.type.label;
        } else {
          dataCollections[`items[${key}].name`] = field.name;
          dataCollections[`items[${key}].description`] = field.description;
        }
      });

      expect(screen.getByRole(DATA_COLLECTION_FORM)).toHaveFormValues(dataCollections);

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
