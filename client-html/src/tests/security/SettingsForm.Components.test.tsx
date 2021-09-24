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
      expect(wrapper.find(`input[type="checkbox"]`).at(0).props().checked).toEqual(
        false
      );

      expect(wrapper.find(`input[type="checkbox"]`).at(1).props().checked).toEqual(
        false
      );

      expect(wrapper.find("#security-password-expiry-period input").val()).toContain(
        30
      );

      expect(wrapper.find(`input[type="checkbox"]`).at(2).props().checked).toEqual(
        false
      );

      expect(wrapper.find("#security-tfa-expiry-period input").val()).toContain(
        16
      );

      expect(wrapper.find(`input[type="checkbox"]`).at(3).props().checked).toEqual(
        false
      );

      expect(wrapper.find("#security-number-login-attempts input").val()).toContain(
        5
      );
    }
  });
});
