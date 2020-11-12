import { PreferenceSchema } from "./PreferencesSchema";

export const BackupDir: PreferenceSchema = {
  uniqueKey: "backup.destination",
  mandatory: false,
  editable: true
};

export const BackupDirWarning: PreferenceSchema = {
  uniqueKey: "backup.destination.warning",
  mandatory: false,
  editable: true
};

export const BackupEnabled: PreferenceSchema = {
  uniqueKey: "backup.enabled",
  mandatory: false,
  editable: true
};

export const BackupMaxHistory: PreferenceSchema = {
  uniqueKey: "backup.maxhistory",
  mandatory: false,
  editable: true
};

export const BackupNextNumber: PreferenceSchema = {
  uniqueKey: "backup.nextnumber",
  mandatory: false,
  editable: true
};

export const BackupTimeOfDay: PreferenceSchema = {
  uniqueKey: "backup.minuteofday",
  mandatory: false,
  editable: true
};

export const DatabaseUsed: PreferenceSchema = {
  uniqueKey: "database.used",
  mandatory: false,
  editable: true
};

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
