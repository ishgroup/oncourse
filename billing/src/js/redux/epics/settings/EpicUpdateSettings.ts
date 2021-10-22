/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { SettingsDTO } from '@api/model';
import { Request, Create } from '../EpicUtils';
import { getSettings, UPDATE_SETTINGS } from '../../actions/Settings';
import SettingsService from '../../../api/services/SettingsService';

const request: Request<any, SettingsDTO> = {
  type: UPDATE_SETTINGS,
  getData: (settings) => SettingsService.updateSettings(settings),
  processData: () => [getSettings()]
};

export const EpicUpdateSettings: Epic<any, any> = Create(request);
