/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SettingsDTO } from '@api/model';
import { FULFILLED } from './ActionUtils';
import { IAction } from '../../models/IshAction';

export const GET_SETTINGS = 'GET_SETTINGS';
export const GET_SETTINGS_FULFILLED = FULFILLED(GET_SETTINGS);

export const UPDATE_SETTINGS = 'UPDATE_SETTINGS';

export const getSettings = (): IAction => ({
  type: GET_SETTINGS
});

export const updateSettings = (settings: SettingsDTO): IAction => ({
  type: UPDATE_SETTINGS,
  payload: settings
});

export const getSettingsFulfilled = (settings: SettingsDTO): IAction => ({
  type: GET_SETTINGS_FULFILLED,
  payload: settings
});
