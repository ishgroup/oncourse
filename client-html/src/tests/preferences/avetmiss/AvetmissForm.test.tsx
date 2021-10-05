import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import Avetmiss from "../../../js/containers/preferences/containers/avetmiss/Avetmiss";

describe("Virtual rendered AvetmissForm", () => {
  defaultComponents({
    entity: "AvetmissForm",
    View: props => <Avetmiss {...props} />,
    record: () => ({}),
    defaultProps: () => ({}),
    render: wrapper => {
      expect(wrapper.find("#avetmiss-collegename input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.AvetmissCollegeName.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-jurisdiction input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.Jurisdiction.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-identifier input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.Id.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-type input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.Type.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-address-line1 input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.Address1.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-address-suburb input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.Suburb.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-address-line2 input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.Address2.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-address-postcode input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.Postcode.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-address-state input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.State.uniqueKey].toString()
      );

      /*---------*/
      expect(wrapper.find("#avetmiss-contactname input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.ContactName.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-email input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.Email.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-phone input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.Phone.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-fax input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.Fax.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-certificate-signatory-name input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.CertSignatoryName.uniqueKey].toString()
      );
      
      expect(wrapper.find("#avetmiss-qld-identifier input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.QldIdentifier.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-fee-help-provider-code input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.FeeHelpProviderCode.uniqueKey].toString()
      );
    }
  });
});
