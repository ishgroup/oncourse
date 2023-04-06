import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import { mockedAPI } from "../../TestEntry";
import * as PreferencesModel from "../../../js/model/preferences";
import LDAP from "../../../js/containers/preferences/containers/ldap/LDAP";
import { preferencesFormRole } from "../../../js/containers/preferences/containers/FormContainer";
import { dashedCase } from "../../common/utils";

describe("Virtual rendered LDAPForm", () => {
  defaultComponents({
    entity: "LDAPForm",
    View: props => <LDAP {...props} />,
    record: () => ({}),
    defaultProps: () => ({
      form: "LDAPForm",
      history: jest.fn()
    }),
    render: ({ screen, fireEvent }) => {
      const ldapFormData = dashedCase({
        [PreferencesModel.LdapHost.uniqueKey]: mockedAPI.db.preference[PreferencesModel.LdapHost.uniqueKey],
        [PreferencesModel.LdapBindUserDN.uniqueKey]: mockedAPI.db.preference[PreferencesModel.LdapBindUserDN.uniqueKey],
        [PreferencesModel.LdapServerPort.uniqueKey]: mockedAPI.db.preference[PreferencesModel.LdapServerPort.uniqueKey],
        [PreferencesModel.LdapBindUserPass.uniqueKey]: mockedAPI.db.preference[PreferencesModel.LdapBindUserPass.uniqueKey],
        [PreferencesModel.LdapBaseDN.uniqueKey]: mockedAPI.db.preference[PreferencesModel.LdapBaseDN.uniqueKey],
        [PreferencesModel.LdapUsernameAttribute.uniqueKey]: mockedAPI.db.preference[PreferencesModel.LdapUsernameAttribute.uniqueKey],
        [PreferencesModel.LdapUserSearchFilter.uniqueKey]: mockedAPI.db.preference[PreferencesModel.LdapUserSearchFilter.uniqueKey],
        [PreferencesModel.LdapGroupMemberAttribute.uniqueKey]: mockedAPI.db.preference[PreferencesModel.LdapGroupMemberAttribute.uniqueKey],
        [PreferencesModel.LdapGroupAttribute.uniqueKey]: mockedAPI.db.preference[PreferencesModel.LdapGroupAttribute.uniqueKey],
        [PreferencesModel.LdapGroupSearchFilter.uniqueKey]: mockedAPI.db.preference[PreferencesModel.LdapGroupSearchFilter.uniqueKey],
      });
      
      fireEvent.click(screen.getByTestId('appbar-submit-button'));
      
      setTimeout(() => {
        expect(screen.getByRole(preferencesFormRole)).toHaveFormValues(ldapFormData);
      }, 500);
    }
  });
});
