import * as React from 'react';
// must be imported before DataCollectionForm: it loads the String prototype extensions (capitalize)
// that CollectionFormField uses at module level
import { defaultComponents } from '../../common/Default.Components';
import DataCollectionForm, {
  DATA_COLLECTION_FORM,
  parseDataCollectionFormData
} from '../../../js/containers/preferences/containers/data-collection-forms/components/DataCollectionForm';
import type { CollectionFormField } from '../../../js/model/preferences/data-collection-forms/collectionFormSchema';

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

      const fields = items.filter(i => i.baseType === "field") as CollectionFormField[];

      // switches are queried as inputs: tooltips of the card header icons are exposed as aria-label too
      const mandatorySwitches = screen.getAllByLabelText("Mandatory", { selector: "input" }) as HTMLInputElement[];
      expect(mandatorySwitches).toHaveLength(fields.length);
      expect(mandatorySwitches.filter(s => s.checked)).toHaveLength(fields.filter(f => f.mandatory).length);

      if (form.type === "Survey") {
        dataCollections['form.deliverySchedule'] = form.deliverySchedule;
        dataCollections['form.tutorDeliverySchedule'] = form.tutorDeliverySchedule;

        const tutorSwitches = screen.getAllByLabelText("Visible to tutors", { selector: "input" }) as HTMLInputElement[];
        expect(tutorSwitches).toHaveLength(fields.length);
        expect(tutorSwitches.filter(s => s.checked)).toHaveLength(fields.filter(f => f.isVisibleToTutor).length);
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

      // TODO fix later
      // expect(screen.getByRole(DATA_COLLECTION_FORM)).toHaveFormValues(dataCollections);

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
