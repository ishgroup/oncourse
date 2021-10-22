/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import {
  setLoadingValue, setServerErrorValue
} from '../../actions';
import CollegeService from '../../../api/services/CollegeService';
import { CREATE_COLLEGE, setCollegeWasCreatedValue } from '../../actions/College';

const request: Request = {
  type: CREATE_COLLEGE,
  getData: (data) => CollegeService.createCollege(data),
  processData: () => [
    setCollegeWasCreatedValue(true),
    setLoadingValue(false)
  ],
  processError: () => [
    setLoadingValue(false),
    setServerErrorValue(true),
  ]
};

export const EpicCreateCollege: Epic<any, any> = Create(request);
