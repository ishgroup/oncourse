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
      expect(wrapper.find("#email-from input").val()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailFromAddress.uniqueKey]
      );

      expect(wrapper.find("#email-from-name input").val()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailFromName.uniqueKey]
      );

      expect(wrapper.find("#email-admin input").val()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailAdminAddress.uniqueKey]
      );

      expect(wrapper.find("#email-pop3host input").val()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailPop3Host.uniqueKey]
      );

      expect(wrapper.find("#email-bounce-address input").val()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailBounceAddress.uniqueKey]
      );

      expect(wrapper.find("#email-pop3-account input").val()).toContain(
        mockedAPI.db.preference[PreferencesModel.EmailPop3Account.uniqueKey]
      );

      expect(wrapper.find("#email-pop3-password input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.EmailPop3Password.uniqueKey]
      );
    }
  });
});
