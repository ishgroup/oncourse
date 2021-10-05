import { PreferenceSchema } from "./PreferencesSchema";

export const LicenseAccessControl: PreferenceSchema = {
  uniqueKey: "license.access_control",
  mandatory: false,
  editable: false
};

export const LicenseScripting: PreferenceSchema = {
  uniqueKey: "license.scripting",
  mandatory: false,
  editable: false
};

export const LicenseSMS: PreferenceSchema = {
  uniqueKey: "license.sms",
  mandatory: false,
  editable: false
};