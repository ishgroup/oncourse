/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import {
  setIsNewUser,
  setLoadingValue
} from '../../actions';
import BillingService from '../../../api/services/BillingService';
import FetchErrorHandler from '../../../api/fetch-errors-handlers/FetchErrorHandler';
import { GET_COLLEGE_KEY, setCollegeKey } from '../../actions/College';

const request: Request = {
  type: GET_COLLEGE_KEY,
  getData: () => BillingService.getCollegeKey(),
  processData: (payload) => [
    setIsNewUser(false),
    setCollegeKey(payload)
  ],
  processError: (response) => {
    const actions = [setLoadingValue(false)];
    if (response?.status && [401, 406].includes(response.status)) {
      return actions;
    }
    return [...actions, ...FetchErrorHandler(response)];
  }
};

export const EpicGetCollegeKey: Epic<any, any> = Create(request);
