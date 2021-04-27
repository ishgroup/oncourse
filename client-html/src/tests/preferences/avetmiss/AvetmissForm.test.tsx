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
      expect(wrapper.find("input[type='checkbox']").at(0).props().checked).toEqual(false);

      expect(wrapper.find("#avetmiss-collegename input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.AvetmissCollegeName.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-jurisdiction input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.Jurisdiction.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-identifier input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.Id.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-type input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.Type.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-address-line1 input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.Address1.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-address-suburb input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.Suburb.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-address-line2 input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.Address2.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-address-postcode input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.Postcode.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-address-state input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.State.uniqueKey].toString()
      );

      /*---------*/
      expect(wrapper.find("#avetmiss-contactname input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.ContactName.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-email input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.Email.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-phone input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.Phone.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-fax input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.Fax.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-certificate-signatory-name input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.CertSignatoryName.uniqueKey].toString()
      );

      expect(wrapper.find("input[type='checkbox']").at(1).props().checked).toEqual(false);

      expect(wrapper.find("#avetmiss-qld-identifier input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.QldIdentifier.uniqueKey].toString()
      );

      expect(wrapper.find("#avetmiss-fee-help-provider-code input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.FeeHelpProviderCode.uniqueKey].toString()
      );
    }
  });
});
