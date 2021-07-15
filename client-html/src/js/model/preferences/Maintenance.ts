import { PreferenceSchema } from "./PreferencesSchema";

export const LogoutEnabled: PreferenceSchema = {
  uniqueKey: "logout.enabled",
  mandatory: false,
  editable: true
};

export const LogoutTimeout: PreferenceSchema = {
  uniqueKey: "logout.timeout",
  mandatory: false,
  editable: true
};
