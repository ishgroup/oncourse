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

      expect(wrapper.find("#ldap-host input").val()).toContain(
        mockedAPI.db.preference[PreferencesModel.LdapHost.uniqueKey]
      );

      expect(wrapper.find("#ldap-bind-user-dn input").val()).toContain(
        mockedAPI.db.preference[PreferencesModel.LdapBindUserDN.uniqueKey]
      );

      expect(wrapper.find("#ldap-serverport input").val()).toContain(
        mockedAPI.db.preference[PreferencesModel.LdapServerPort.uniqueKey]
      );
      
      expect(wrapper.find("#ldap-bind-user-pass input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapBindUserPass.uniqueKey]
      );

      expect(wrapper.find("#ldap-base-dn input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapBaseDN.uniqueKey]
      );

      /*-----*/
      expect(wrapper.find("#ldap-username-attibute input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapUsernameAttribute.uniqueKey]
      );

      expect(wrapper.find("#ldap-user-search-filter input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapUserSearchFilter.uniqueKey]
      );
      
      expect(wrapper.find("#ldap-group-member-attibute input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapGroupMemberAttribute.uniqueKey]
      );

      expect(wrapper.find("#ldap-group-attibute input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapGroupAttribute.uniqueKey]
      );

      expect(wrapper.find("#ldap-group-search-filter input").val()).toEqual(
        mockedAPI.db.preference[PreferencesModel.LdapGroupSearchFilter.uniqueKey]
      );
    }
  });
});
