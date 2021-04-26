import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import GradingTypesForm from "../../../js/containers/preferences/containers/grading/components/GradingTypesForm";

describe("Virtual rendered GradingTypesForm", () => {
  defaultComponents({
    entity: "GradingForm",
    View: props => <GradingTypesForm {...props} />,
    record: mockedApi => mockedApi.db.getGradingTypes(),
    defaultProps: ({ initialValues }) => {
      return {
        form: "GradingForm",
        gradingTypes: initialValues
      };
    },
    render: (wrapper, initialValues) => {
      initialValues.forEach((type, index) => {
        expect(wrapper.find(`div[id='types[${index}].name']`).text()).toContain(type.name);
        expect(wrapper.find(`div[id='types[${index}].entryType'] input`).getDOMNode().value).toEqual(type.entryType);

        if (type.entryType === 'number') {
          expect(wrapper.find(`div[id='types[${index}].minValue']`).text()).toContain(type.minValue);
          expect(wrapper.find(`div[id='types[${index}].maxValue']`).text()).toContain(type.maxValue);
        }

        type.gradingItems.forEach((gradingItem, gIndex) => {
          expect(wrapper.find(`div[id='types[${index}].gradingItems[${gIndex}].name']`).text()).toContain(gradingItem.name);
          expect(wrapper.find(`div[id='types[${index}].gradingItems[${gIndex}].lowerBound']`).text()).toContain(gradingItem.lowerBound);
        });
      });
    }
  });
});
