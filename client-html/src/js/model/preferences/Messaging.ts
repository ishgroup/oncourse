import { PreferenceSchema } from "./PreferencesSchema";

export const EmailAdminAddress: PreferenceSchema = {
  uniqueKey: "email.admin",
  mandatory: false,
  editable: true
};

export const EmailBounceAddress: PreferenceSchema = {
  uniqueKey: "email.bounce.address",
  mandatory: false,
  editable: true
};

export const EmailBounceEnabled: PreferenceSchema = {
  uniqueKey: "email.bounce",
  mandatory: false,
  editable: true
};

export const EmailFromAddress: PreferenceSchema = {
  uniqueKey: "email.from",
  mandatory: true,
  editable: true
};

export const EmailFromName: PreferenceSchema = {
  uniqueKey: "email.from.name",
  mandatory: false,
  editable: true
};

export const EmailPop3Account: PreferenceSchema = {
  uniqueKey: "email.pop3.account",
  mandatory: false,
  editable: true
};

export const EmailPop3Host: PreferenceSchema = {
  uniqueKey: "email.pop3host",
  mandatory: false,
  editable: true
};

export const EmailPop3Password: PreferenceSchema = {
  uniqueKey: "email.pop3.password",
  mandatory: false,
  editable: true
};

export const SMSFromAddress: PreferenceSchema = {
  uniqueKey: "sms.from",
  mandatory: false,
  editable: true
};