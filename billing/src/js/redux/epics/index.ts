/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from 'redux-observable';
import { EpicCheckSiteName } from './sites/EpicCheckSiteName';
import { EpicCreateCollege } from './college/EpicCreateCollege';
import { EpicGetSites } from './sites/EpicGetSites';
import { EpicGetCollegeKey } from './college/EpicGetCollegeKey';
import { EpicUpdateCollegeSites } from './sites/EpicUpdateCollegeSites';
import { EpicGetGTMandGAData } from './google/EpicGetGTMandGAData';
import { EpicConfigGoogleServicesForSite } from './google/EpicConfigGoogleServicesForSite';
import { EpicGetSettings } from './settings/EpicGetSettings';
import { EpicUpdateSettings } from './settings/EpicUpdateSettings';
import { EpicGetClientId } from './google/EpicGetClientId';

export const EpicRoot = combineEpics(
  EpicUpdateCollegeSites,
  EpicCheckSiteName,
  EpicCreateCollege,
  EpicGetSites,
  EpicGetCollegeKey,
  EpicGetGTMandGAData,
  EpicConfigGoogleServicesForSite,
  EpicGetSettings,
  EpicUpdateSettings,
  EpicGetClientId
);
