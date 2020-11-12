import { PreferenceSchema } from "./PreferencesSchema";

export const LdapBaseDN: PreferenceSchema = {
  uniqueKey: "ldap.base.dn",
  mandatory: false,
  editable: false
};

export const LdapBindUserDN: PreferenceSchema = {
  uniqueKey: "ldap.bind.user.dn",
  mandatory: false,
  editable: false
};

export const LdapBindUserPass: PreferenceSchema = {
  uniqueKey: "ldap.bind.user.pass",
  mandatory: false,
  editable: false
};

export const LdapDomain: PreferenceSchema = {
  uniqueKey: "ldap.domain",
  mandatory: false,
  editable: false
};

export const LdapGroupAttribute: PreferenceSchema = {
  uniqueKey: "ldap.group.attibute",
  mandatory: false,
  editable: false
};

export const LdapGroupMemberAttribute: PreferenceSchema = {
  uniqueKey: "ldap.group.member.attibute",
  mandatory: false,
  editable: false
};

export const LdapGroupPosixStyle: PreferenceSchema = {
  uniqueKey: "ldap.group.posixstyle",
  mandatory: false,
  editable: false
};

export const LdapGroupSearchFilter: PreferenceSchema = {
  uniqueKey: "ldap.group.search.filter",
  mandatory: false,
  editable: false
};

export const LdapHost: PreferenceSchema = {
  uniqueKey: "ldap.host",
  mandatory: false,
  editable: false
};

export const LdapSaslAuthentication: PreferenceSchema = {
  uniqueKey: "services.ldap.authorisation",
  mandatory: false,
  editable: false
};

export const LdapSecutiry: PreferenceSchema = {
  uniqueKey: "ldap.security",
  mandatory: false,
  editable: false
};

export const LdapServerPort: PreferenceSchema = {
  uniqueKey: "ldap.serverport",
  mandatory: false,
  editable: false
};

export const LdapSimpleAuthentication: PreferenceSchema = {
  uniqueKey: "services.ldap.authentication",
  mandatory: false,
  editable: false
};

export const LdapSSL: PreferenceSchema = {
  uniqueKey: "ldap.ssl",
  mandatory: false,
  editable: false
};

export const LdapUsernameAttribute: PreferenceSchema = {
  uniqueKey: "ldap.username.attibute",
  mandatory: false,
  editable: false
};

export const LdapUserSearchFilter: PreferenceSchema = {
  uniqueKey: "ldap.user.search.filter",
  mandatory: false,
  editable: false
};
