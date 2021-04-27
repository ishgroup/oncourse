import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import Maintenance from "../../../js/containers/preferences/containers/maintenance/Maintenance";

describe("Virtual rendered MaintenanceForm", () => {
  defaultComponents({
    entity: "MaintenanceForm",
    View: props => <Maintenance {...props} />,
    record: () => ({}),
    defaultProps: () => ({}),
    render: wrapper => {
      expect(wrapper.find("#logout-timeout input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.LogoutTimeout.uniqueKey].toString()
      );
    }
  });
});
