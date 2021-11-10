import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import GradingTypesForm from "../../../js/containers/preferences/containers/grading/components/GradingTypesForm";

// TODO Enable test on fix

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
    render: (wrapper, initialValues, shallow) => {
      initialValues.forEach((type, index) => {


        // expect(wrapper.find(`div[id='types[${index}].name'] input`).val()).toContain(type.name);
        //
        //
        // expect(wrapper.find(`div[id='types[${index}].entryType'] input`).val()).toEqual(type.entryType);
        //
        // if (type.entryType === 'number') {
        //   expect(wrapper.find(`div[id='types[${index}].minValue'] input`).val()).toContain(type.minValue);
        //   expect(wrapper.find(`div[id='types[${index}].maxValue'] input`).val()).toContain(type.maxValue);
        // }
        //
        // type.gradingItems.forEach((gradingItem, gIndex) => {
        //   expect(wrapper.find(`div[id='types[${index}].gradingItems[${gIndex}].name'] input`).val()).toContain(gradingItem.name);
        //   expect(wrapper.find(`div[id='types[${index}].gradingItems[${gIndex}].lowerBound'] input`).val()).toContain(gradingItem.lowerBound);
        // });
      });
    }
  });
});
