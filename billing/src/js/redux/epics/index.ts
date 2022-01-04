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
import { EpicGetGTMandGAAccounts } from './google/EpicGetGTMandGAAccounts';
import { EpicGetGTMDataByAccount } from './google/EpicGetGTMDataByAccount';
import { EpicGetGAWebPropertiesByAccount } from './google/EpicGetGAWebPropertiesByAccount';
import { EpicGetGAProfilesByAccount } from './google/EpicGetGAProfilesByAccount';
import { EpicConfigGoogleServicesForSite } from './google/EpicConfigGoogleServicesForSite';
import { EpicGetSettings } from './settings/EpicGetSettings';
import { EpicUpdateSettings } from './settings/EpicUpdateSettings';
import { EpicGetClientId } from './google/EpicGetClientId';

export const EpicRoot = combineEpics(
  EpicGetGAProfilesByAccount,
  EpicGetGAWebPropertiesByAccount,
  EpicGetGTMDataByAccount,
  EpicUpdateCollegeSites,
  EpicCheckSiteName,
  EpicCreateCollege,
  EpicGetSites,
  EpicGetCollegeKey,
  EpicGetGTMandGAAccounts,
  EpicConfigGoogleServicesForSite,
  EpicGetSettings,
  EpicUpdateSettings,
  EpicGetClientId
);
