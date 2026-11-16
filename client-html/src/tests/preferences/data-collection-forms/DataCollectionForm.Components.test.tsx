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

      // ToogleCheckbox renders "unchecked | checked" buttons around a hidden, unlabelled checkbox,
      // so the toggle is found by its checked-side button text and its state read from that checkbox
      const toggleInputs = (checkedLabel: string) => screen.getAllByText(checkedLabel)
        .map(label => label.closest(".MuiButtonGroup-root").querySelector<HTMLInputElement>("input[type=checkbox]"));

      const mandatoryToggles = toggleInputs("Mandatory");
      expect(mandatoryToggles).toHaveLength(fields.length);
      expect(mandatoryToggles.filter(s => s.checked)).toHaveLength(fields.filter(f => f.mandatory).length);

      if (form.type === "Survey") {
        dataCollections['form.deliverySchedule'] = form.deliverySchedule;
        dataCollections['form.tutorDeliverySchedule'] = form.tutorDeliverySchedule;

        const tutorToggles = toggleInputs("Tutor visible");
        expect(tutorToggles).toHaveLength(fields.length);
        expect(tutorToggles.filter(s => s.checked)).toHaveLength(fields.filter(f => f.isVisibleToTutor).length);
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
