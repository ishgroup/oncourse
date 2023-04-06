import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import GradingTypesForm, { GRADING_FORM } from "../../../js/containers/preferences/containers/grading/components/GradingTypesForm";

describe("Virtual rendered GradingTypesForm", () => {
  defaultComponents({
    entity: "GradingForm",
    View: props => <GradingTypesForm {...props} />,
    record: mockedApi => mockedApi.db.getGradingTypes(),
    defaultProps: ({ initialValues }) => ({
      form: GRADING_FORM,
      gradingTypes: initialValues
    }),
    render: ({ screen, initialValues, fireEvent }) => {
      const gradingTypes = {};

      initialValues.forEach((type, key) => {
        gradingTypes[`types[${key}].name`] = type.name;
        gradingTypes[`types[${key}].entryType`] = type.entryType;

        if (type.entryType === 'number') {
          gradingTypes[`types[${key}].minValue`] = type.minValue;
          gradingTypes[`types[${key}].maxValue`] = type.maxValue;
        }

        type.gradingItems.forEach((gradingItem, gKey) => {
          gradingTypes[`types[${key}].gradingItems[${gKey}].name`] = gradingItem.name;
          gradingTypes[`types[${key}].gradingItems[${gKey}].lowerBound`] = gradingItem.lowerBound;
        });
      });

      expect(screen.getByRole(GRADING_FORM)).toHaveFormValues(gradingTypes);

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
