import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import Maintenance from "../../../js/containers/preferences/containers/maintenance/Maintenance";
import { preferencesFormRole } from "../../../js/containers/preferences/containers/FormContainer";
import { dashedCase } from "../../common/utils";

describe("Virtual rendered MaintenanceForm", () => {
  defaultComponents({
    entity: "MaintenanceForm",
    View: props => <Maintenance {...props} />,
    record: () => ({}),
    defaultProps: () => ({}),
    render: ({ screen, fireEvent }) => {
      const maintenanceFormData = dashedCase({
        [PreferencesModel.LogoutTimeout.uniqueKey]: Number(mockedAPI.db.preference[PreferencesModel.LogoutTimeout.uniqueKey])
      });

      expect(screen.getByRole(preferencesFormRole)).toHaveFormValues(maintenanceFormData);

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
