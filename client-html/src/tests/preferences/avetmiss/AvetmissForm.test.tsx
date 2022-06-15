import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import Avetmiss from "../../../js/containers/preferences/containers/avetmiss/Avetmiss";
import { preferencesFormRole } from "../../../js/containers/preferences/containers/FormContainer";
import { dashedCase } from "../../common/utils";

describe("Virtual rendered AvetmissForm", () => {
  defaultComponents({
    entity: "AvetmissForm",
    View: props => <Avetmiss {...props} />,
    record: () => ({}),
    defaultProps: () => ({}),
    render: ({ screen, fireEvent }) => {
      const avetmissFormData = dashedCase({
        [PreferencesModel.AvetmissCollegeName.uniqueKey]: mockedAPI.db.preference[PreferencesModel.AvetmissCollegeName.uniqueKey].toString(),
        [PreferencesModel.Jurisdiction.uniqueKey]: mockedAPI.db.preference[PreferencesModel.Jurisdiction.uniqueKey].toString(),
        [PreferencesModel.Id.uniqueKey]: mockedAPI.db.preference[PreferencesModel.Id.uniqueKey].toString(),
        [PreferencesModel.Type.uniqueKey]: mockedAPI.db.preference[PreferencesModel.Type.uniqueKey].toString(),
        [PreferencesModel.Address1.uniqueKey]: mockedAPI.db.preference[PreferencesModel.Address1.uniqueKey].toString(),
        [PreferencesModel.Suburb.uniqueKey]: mockedAPI.db.preference[PreferencesModel.Suburb.uniqueKey].toString(),
        [PreferencesModel.Address2.uniqueKey]: mockedAPI.db.preference[PreferencesModel.Address2.uniqueKey].toString(),
        [PreferencesModel.Postcode.uniqueKey]: mockedAPI.db.preference[PreferencesModel.Postcode.uniqueKey].toString(),
        [PreferencesModel.State.uniqueKey]: mockedAPI.db.preference[PreferencesModel.State.uniqueKey].toString(),

        [PreferencesModel.ContactName.uniqueKey]: mockedAPI.db.preference[PreferencesModel.ContactName.uniqueKey].toString(),
        [PreferencesModel.Email.uniqueKey]: mockedAPI.db.preference[PreferencesModel.Email.uniqueKey].toString(),
        [PreferencesModel.Phone.uniqueKey]: mockedAPI.db.preference[PreferencesModel.Phone.uniqueKey].toString(),
        [PreferencesModel.Fax.uniqueKey]: mockedAPI.db.preference[PreferencesModel.Fax.uniqueKey].toString(),
        [PreferencesModel.CertSignatoryName.uniqueKey]: mockedAPI.db.preference[PreferencesModel.CertSignatoryName.uniqueKey].toString(),
        [PreferencesModel.QldIdentifier.uniqueKey]: mockedAPI.db.preference[PreferencesModel.QldIdentifier.uniqueKey].toString(),
        [PreferencesModel.FeeHelpProviderCode.uniqueKey]: mockedAPI.db.preference[PreferencesModel.FeeHelpProviderCode.uniqueKey].toString(),
      });

      fireEvent.click(screen.getByTestId('appbar-submit-button'));

      setTimeout(() => {
        expect(screen.getByRole(preferencesFormRole)).toHaveFormValues(avetmissFormData);
      }, 500);
    }
  });
});
