/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PreferenceSchema } from "./PreferencesSchema";

export const ClassDeliveryMode: PreferenceSchema = {
  uniqueKey: "courseclass_default_deliveryMode",
  mandatory: false,
  editable: true
};

export const ClassFundingSourcePreference: PreferenceSchema = {
  uniqueKey: "courseclass_default_fundingSource",
  mandatory: false,
  editable: true
};

export const ClassMaxPlaces: PreferenceSchema = {
  uniqueKey: "courseclass_default_maximumPlaces",
  mandatory: false,
  editable: true
};

export const ClassMinPlaces: PreferenceSchema = {
  uniqueKey: "courseclass_default_minimumPlaces",
  mandatory: false,
  editable: true
};
