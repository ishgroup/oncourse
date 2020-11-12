import { PreferenceSchema } from "./PreferencesSchema";

export const CollegeABN: PreferenceSchema = {
  uniqueKey: "college.abn",
  mandatory: false,
  editable: true
};

export const CollegeName: PreferenceSchema = {
  uniqueKey: "college.name",
  mandatory: true,
  editable: true
};

export const CollegeTimezone: PreferenceSchema = {
  uniqueKey: "oncourse.server.timezone.default",
  mandatory: false,
  editable: true
};

export const CollegeWebsite: PreferenceSchema = {
  uniqueKey: "web.url",
  mandatory: false,
  editable: true
};

export const CollegeSucureKey: PreferenceSchema = {
  uniqueKey: "services.securitykey",
  mandatory: false,
  editable: true
};
