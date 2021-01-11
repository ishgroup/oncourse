import { PreferenceSchema } from "../PreferencesSchema";

export const SecurityAutoDisableInactiveAccount: PreferenceSchema = {
  uniqueKey: "security.auto.disable.inactive.account",
  mandatory: false,
  editable: true
};

export const SecurityNumberIncorrectLoginAttempts: PreferenceSchema = {
  uniqueKey: "security.number.login.attempts",
  mandatory: false,
  editable: true
};

export const SecurityPasswordComplexity: PreferenceSchema = {
  uniqueKey: "security.password.complexity",
  mandatory: true,
  editable: true
};

export const SecurityPasswordExpiryPeriod: PreferenceSchema = {
  uniqueKey: "security.password.expiry.period",
  mandatory: false,
  editable: true
};

export const SecurityTFAStatus: PreferenceSchema = {
  uniqueKey: "security.tfa.status",
  mandatory: false,
  editable: true
};

export const SecurityTFAExpiryPeriod: PreferenceSchema = {
  uniqueKey: "security.tfa.expiry.period",
  mandatory: false,
  editable: true
};
