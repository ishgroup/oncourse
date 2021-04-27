import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import Messaging from "../../../js/containers/preferences/containers/messaging/Messaging";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";

describe("Virtual rendered MessagingForm", () => {
  defaultComponents({
    entity: "MessagingForm",
    View: props => <Messaging {...props} />,
    record: () => ({}),
    defaultProps: () => ({
      form: "MessagingForm",
      history: jest.fn()
    }),
    render: wrapper => {
      expect(wrapper.find("#email-from").text()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailFromAddress.uniqueKey]
      );

      expect(wrapper.find("#email-from-name").text()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailFromName.uniqueKey]
      );

      expect(wrapper.find("#email-admin").text()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailAdminAddress.uniqueKey]
      );

      expect(wrapper.find("input[type='checkbox']").props().checked).toEqual(false);

      expect(wrapper.find("#email-pop3host").text()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailPop3Host.uniqueKey]
      );

      expect(wrapper.find("#email-bounce-address").text()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailBounceAddress.uniqueKey]
      );

      expect(wrapper.find("#email-pop3-account").text()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailPop3Account.uniqueKey]
      );

      expect(wrapper.find("#email-pop3-password input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.EmailPop3Password.uniqueKey]
      );
    }
  });
});
