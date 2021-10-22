/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { PropertiesDTO } from '@api/model';
import { Request, Create } from '../EpicUtils';
import {
  GET_CLIENT_ID,
  getClientIdFulfilled,
} from '../../actions/Google';
import GoogleService from '../../../api/services/GoogleService';

const request: Request<PropertiesDTO, null> = {
  type: GET_CLIENT_ID,
  getData: () => GoogleService.getProperties(),
  processData: (res) => [getClientIdFulfilled(res.clientId)]
};

export const EpicGetClientId: Epic<any, any> = Create(request);
