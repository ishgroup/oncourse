/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import {
  SET_LOADING_VALUE, SET_SERVER_ERROR_VALUE,
  SHOW_MESSAGE
} from '../../actions';
import BillingService from '../../../api/services/BillingService';
import { CHECK_SITENAME, SET_SEND_TOKEN_AGAIN_VALUE, SET_SITENAME_VALID_VALUE } from '../../actions/College';

const errorHandler = (
  response: any,
  customMessage = 'Something unexpected has happened, please contact ish support or try again'
) => {
  if (!response) {
    return ([{
      type: SHOW_MESSAGE,
      payload: { message: customMessage || 'Something went wrong', error: true }
    }]);
  }

  const { data, status } = response;

  if (status) {
    return ([{
      type: SHOW_MESSAGE,
      payload: {
        message: (data && data.errorMessage) || customMessage,
        error: true
      }
    }]);
  } if (status === 500) {
    document.cookie.split(';').forEach((c) => {
      document.cookie = c.replace(/^ +/, '')
        .replace(/=.*/, `=;expires=${new Date().toUTCString()};path=/`);
    });

    return ([{
      type: SET_SERVER_ERROR_VALUE,
      payload: true
    }, {
      type: SHOW_MESSAGE,
      payload: {
        message: (data && data.errorMessage) || customMessage,
        error: true
      }
    }]);
  }
  return ([{
    type: SHOW_MESSAGE,
    payload: {
      message: response,
      error: true
    }
  }]);
};

const request: Request = {
  type: CHECK_SITENAME,
  getData: ({ name, token }) => BillingService.verifyCollegeName(name, token),
  processData: (response: boolean) => {
    const returnedValue:any[] = [
      { type: SET_SITENAME_VALID_VALUE, payload: response },
      { type: SET_SEND_TOKEN_AGAIN_VALUE, payload: false },
      { type: SET_LOADING_VALUE, payload: false },
    ];

    if (!response) {
      returnedValue.push({
        type: SHOW_MESSAGE,
        payload: {
          message: 'Site name is already taken',
          error: true
        }
      });
    }
    return returnedValue;
  },
  processError: (response) => [
    ...errorHandler(response),
    { type: SET_SITENAME_VALID_VALUE, payload: false },
    { type: SET_LOADING_VALUE, payload: false }
  ]
};

export const EpicCheckSiteName: Epic<any, any> = Create(request);
