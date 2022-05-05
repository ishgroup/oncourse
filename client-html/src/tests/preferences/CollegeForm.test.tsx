import * as React from "react";
import { defaultComponents } from "../common/Default.Components";
import { dashedCase } from "../common/utils";
import College from "../../js/containers/preferences/containers/college/College";
import * as PreferencesModel from "../../js/model/preferences";
import { preferencesFormRole } from "../../js/containers/preferences/containers/FormContainer";

describe("Virtual rendered College Form component", () => {
  defaultComponents({
    entity: "CollegeForm",
    View: props => <College {...props} />,
    record: () => ({}),
    defaultProps: () => ({}),
    render: ({ screen, fireEvent, mockedApi }) => {
      const collegeFormData = dashedCase({
        [PreferencesModel.CollegeWebsite.uniqueKey]: mockedApi.db.preference[PreferencesModel.CollegeWebsite.uniqueKey],
        [PreferencesModel.CollegeName.uniqueKey]: mockedApi.db.preference[PreferencesModel.CollegeName.uniqueKey],
        [PreferencesModel.CollegeTimezone.uniqueKey]: mockedApi.db.preference[PreferencesModel.CollegeTimezone.uniqueKey],
        [PreferencesModel.CollegeABN.uniqueKey]: mockedApi.db.preference[PreferencesModel.CollegeABN.uniqueKey],
      });

      fireEvent.click(screen.getByTestId('appbar-submit-button'));

      setTimeout(() => {
        expect(screen.getByRole(preferencesFormRole)).toHaveFormValues(collegeFormData);
      }, 500);
    }
  });
});
