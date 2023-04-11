import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import Messaging from "../../../js/containers/preferences/containers/messaging/Messaging";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import { preferencesFormRole } from "../../../js/containers/preferences/containers/FormContainer";
import { dashedCase } from "../../common/utils";

describe("Virtual rendered MessagingForm", () => {
  defaultComponents({
    entity: "MessagingForm",
    View: props => <Messaging {...props} />,
    record: () => ({}),
    defaultProps: () => ({
      form: "MessagingForm",
      history: jest.fn()
    }),
    render: ({ screen, fireEvent }) => {
      const messagingFormData = dashedCase({
        [PreferencesModel.EmailFromAddress.uniqueKey]: mockedAPI.db.preference[PreferencesModel.EmailFromAddress.uniqueKey],
        [PreferencesModel.EmailFromName.uniqueKey]: mockedAPI.db.preference[PreferencesModel.EmailFromName.uniqueKey],
        [PreferencesModel.EmailAdminAddress.uniqueKey]: mockedAPI.db.preference[PreferencesModel.EmailAdminAddress.uniqueKey],
        [PreferencesModel.EmailPop3Host.uniqueKey]: mockedAPI.db.preference[PreferencesModel.EmailPop3Host.uniqueKey],
        [PreferencesModel.EmailBounceAddress.uniqueKey]: mockedAPI.db.preference[PreferencesModel.EmailBounceAddress.uniqueKey],
        [PreferencesModel.EmailPop3Account.uniqueKey]: mockedAPI.db.preference[PreferencesModel.EmailPop3Account.uniqueKey],
        [PreferencesModel.EmailPop3Password.uniqueKey]: mockedAPI.db.preference[PreferencesModel.EmailPop3Password.uniqueKey],
      });

      fireEvent.click(screen.getByTestId('appbar-submit-button'));

      setTimeout(() => {
        expect(screen.getByRole(preferencesFormRole)).toHaveFormValues(messagingFormData);
      }, 500);
    }
  });
});
