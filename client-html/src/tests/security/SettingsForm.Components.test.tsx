import * as React from "react";
import { defaultComponents } from "../common/Default.Components";
import Settings from "../../js/containers/security/containers/settings/Settings";
import * as PreferencesModel from "../../js/model/preferences";
import { preferencesFormRole } from "../../js/containers/preferences/containers/FormContainer";
import { dashedCase } from "../common/utils";

describe("Virtual rendered SecuritySettingsForm", () => {
  defaultComponents({
    entity: "SecuritySettingsForm",
    View: props => <Settings {...props} />,
    record: () => ({}),
    defaultProps: () => ({}),
    render: ({ screen, fireEvent, mockedApi }) => {
      const securityFormData = dashedCase({
        [PreferencesModel.SecurityAutoDisableInactiveAccount.uniqueKey]:
          mockedApi.db.preference[PreferencesModel.SecurityAutoDisableInactiveAccount.uniqueKey],
        [PreferencesModel.SecurityPasswordComplexity.uniqueKey]:
          mockedApi.db.preference[PreferencesModel.SecurityPasswordComplexity.uniqueKey],
        [PreferencesModel.SecurityPasswordExpiryPeriod.uniqueKey]:
          mockedApi.db.preference[PreferencesModel.SecurityPasswordExpiryPeriod.uniqueKey],
        [PreferencesModel.SecurityTFAExpiryPeriod.uniqueKey]: mockedApi.db.preference[PreferencesModel.SecurityTFAExpiryPeriod.uniqueKey],
        [PreferencesModel.SecurityNumberIncorrectLoginAttempts.uniqueKey]:
          mockedApi.db.preference[PreferencesModel.SecurityNumberIncorrectLoginAttempts.uniqueKey],
        [PreferencesModel.SecurityTFAStatus.uniqueKey]: mockedApi.db.preference[PreferencesModel.SecurityTFAStatus.uniqueKey].toString(),
      });

      fireEvent.click(screen.getByTestId('appbar-submit-button'));

      setTimeout(() => {
        expect(screen.getByRole(preferencesFormRole)).toHaveFormValues(securityFormData);
      }, 500);
    }
  });
});
