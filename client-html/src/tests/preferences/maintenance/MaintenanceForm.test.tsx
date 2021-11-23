import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import Maintenance from "../../../js/containers/preferences/containers/maintenance/Maintenance";

// TODO Enable test on fix

describe.skip("Virtual rendered MaintenanceForm", () => {
  defaultComponents({
    entity: "MaintenanceForm",
    View: props => <Maintenance {...props} />,
    record: () => ({}),
    defaultProps: () => ({}),
    render: wrapper => {
      expect(wrapper.find("#logout-timeout input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.LogoutTimeout.uniqueKey].toString()
      );
    }
  });
});
