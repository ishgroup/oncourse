import * as React from "react";
import { defaultComponents } from "../common/Default.Components";
import Settings from "../../js/containers/security/containers/settings/Settings";

describe("Virtual rendered SecuritySettingsForm", () => {
  defaultComponents({
    entity: "SecuritySettingsForm",
    View: props => <Settings {...props} />,
    record: () => ({}),
    defaultProps: () => ({
      form: "SecuritySettingsForm",
      history: jest.fn()
    }),
    render: wrapper => {

      expect(wrapper.find("#security-password-expiry-period").text()).toContain(
        30
      );

      expect(wrapper.find("#security-tfa-expiry-period").text()).toContain(
        16
      );

      expect(wrapper.find("#security-number-login-attempts").text()).toContain(
        5
      );
    }
  });
});
