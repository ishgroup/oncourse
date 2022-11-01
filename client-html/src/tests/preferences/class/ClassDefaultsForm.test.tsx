import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import ClassDefaults from "../../../js/containers/preferences/containers/class/ClassDefaults";
import { preferencesFormRole } from "../../../js/containers/preferences/containers/FormContainer";
import { dashedCase } from "../../common/utils";

describe("Virtual rendered ClassDefaultsForm", () => {
  defaultComponents({
    entity: "ClassDefaultsForm",
    View: props => <ClassDefaults {...props} />,
    record: () => ({}),
    defaultProps: () => ({
      form: "ClassDefaultsForm",
      history: jest.fn()
    }),
    render: ({ screen }) => {
      const classFormData = dashedCase({
        [PreferencesModel.ClassMinPlaces.uniqueKey]: mockedAPI.db.preference[PreferencesModel.ClassMinPlaces.uniqueKey],
        [PreferencesModel.ClassDeliveryMode.uniqueKey]: mockedAPI.db.preference[PreferencesModel.ClassDeliveryMode.uniqueKey].toString(),

        [PreferencesModel.ClassMaxPlaces.uniqueKey]: mockedAPI.db.preference[PreferencesModel.ClassMaxPlaces.uniqueKey],
        [PreferencesModel.ClassFundingSourcePreference.uniqueKey]: mockedAPI.db.preference[PreferencesModel.ClassFundingSourcePreference.uniqueKey].toString()
      });

      setTimeout(() => {
        expect(screen.getByRole(preferencesFormRole)).toHaveFormValues(classFormData);
      }, 500);
    }
  });
});
