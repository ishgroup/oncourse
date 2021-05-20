import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import LDAP from "../../../js/containers/preferences/containers/ldap/LDAP";

describe("Virtual rendered LDAPForm", () => {
  defaultComponents({
    entity: "LDAPForm",
    View: props => <LDAP {...props} />,
    record: () => ({}),
    defaultProps: () => ({
      form: "LDAPForm",
      history: jest.fn()
    }),
    render: wrapper => {
      expect(wrapper.find(`input[type="checkbox"]`).at(0).props().checked).toEqual(false);

      expect(wrapper.find("#ldap-host").text()).toContain(
        mockedAPI.db.preference[PreferencesModel.LdapHost.uniqueKey]
      );

      expect(wrapper.find("#ldap-bind-user-dn").text()).toContain(
        mockedAPI.db.preference[PreferencesModel.LdapBindUserDN.uniqueKey]
      );

      expect(wrapper.find("#ldap-serverport").text()).toContain(
        mockedAPI.db.preference[PreferencesModel.LdapServerPort.uniqueKey]
      );

      expect(wrapper.find(`input[type="checkbox"]`).at(1).props().checked).toEqual(false);

      expect(wrapper.find("#ldap-bind-user-pass input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapBindUserPass.uniqueKey]
      );

      expect(wrapper.find("#ldap-base-dn input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapBaseDN.uniqueKey]
      );

      /*-----*/
      expect(wrapper.find("#ldap-username-attibute input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapUsernameAttribute.uniqueKey]
      );

      expect(wrapper.find("#ldap-user-search-filter input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapUserSearchFilter.uniqueKey]
      );

      /*-----*/
      expect(wrapper.find(`input[type="checkbox"]`).at(2).props().checked).toEqual(false);

      expect(wrapper.find("#ldap-group-member-attibute input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapGroupMemberAttribute.uniqueKey]
      );

      expect(wrapper.find("#ldap-group-attibute input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapGroupAttribute.uniqueKey]
      );

      expect(wrapper.find("#ldap-group-search-filter input").getDOMNode().value).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapGroupSearchFilter.uniqueKey]
      );

      expect(wrapper.find(`input[type="checkbox"]`).at(3).props().checked).toEqual(false);
    }
  });
});
