/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { SettingsDTO } from '@api/model';
import { Request, Create } from '../EpicUtils';
import { GET_SETTINGS, getSettingsFulfilled } from '../../actions/Settings';
import SettingsService from '../../../api/services/SettingsService';

const request: Request<SettingsDTO> = {
  type: GET_SETTINGS,
  getData: () => SettingsService.getSettings(),
  processData: (settings) => [getSettingsFulfilled(settings)]
};

export const EpicGetSettings: Epic<any, any> = Create(request);
