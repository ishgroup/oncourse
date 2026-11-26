/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PreferenceSchema } from "./PreferencesSchema";

export const AssistantEnabled: PreferenceSchema = {
  uniqueKey: "oncourse.assistant.enabled",
  mandatory: false,
  editable: true
};

export const AssistantAdminOnly: PreferenceSchema = {
  uniqueKey: "oncourse.assistant.adminOnly",
  mandatory: false,
  editable: true
};

export const AssistantStudentDataAllowed: PreferenceSchema = {
  uniqueKey: "oncourse.assistant.studentDataAllowed",
  mandatory: false,
  editable: true
};
