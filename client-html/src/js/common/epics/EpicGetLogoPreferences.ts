/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from 'redux-observable';
import PreferencesService from '../../containers/preferences/services/PreferencesService';
import { GET_LOGO, GET_LOGO_FULFILLED } from '../actions';
import * as EpicUtils from './EpicUtils';

const request: EpicUtils.Request = {
  type: GET_LOGO,
  getData: () => PreferencesService.getLogo(),
  processData: logo => [
    {
      type: GET_LOGO_FULFILLED,
      payload: logo
    }
  ]
};

export const EpicGetLogoPreferences: Epic<any, any> = EpicUtils.Create(request);