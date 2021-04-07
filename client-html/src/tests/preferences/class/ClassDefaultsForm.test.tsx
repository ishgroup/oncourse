import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import ClassDefaults from "../../../js/containers/preferences/containers/class/ClassDefaults";

describe("Virtual rendered ClassDefaultsForm", () => {
  defaultComponents({
    entity: "ClassDefaultsForm",
    View: props => <ClassDefaults {...props} />,
    record: () => ({}),
    defaultProps: () => ({
      form: "ClassDefaultsForm",
      history: jest.fn()
    }),
    render: wrapper => {
      expect(wrapper.find("#courseclass_default_minimumPlaces").text()).toContain(
        mockedAPI.db.preference[PreferencesModel.ClassMinPlaces.uniqueKey]
      );

      expect(wrapper.find("#courseclass_default_deliveryMode").text()).toContain("No value");

      expect(wrapper.find("#courseclass_default_maximumPlaces").text()).toContain(
        mockedAPI.db.preference[PreferencesModel.ClassMaxPlaces.uniqueKey]
      );

      expect(wrapper.find("#courseclass_default_fundingSource").text()).toContain("No value");
    }
  });
});
