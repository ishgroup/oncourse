import { Epic } from 'redux-observable';
import * as EpicUtils from './EpicUtils';
import {
  setIsNewUser,
  setLoadingValue
} from '../actions';
import BillingService from '../../api/services/BillingService';
import FetchErrorHandler from '../../api/fetch-errors-handlers/FetchErrorHandler';
import { GET_COLLEGE_KEY, setCollegeKey } from '../actions/College';

const request: EpicUtils.Request = {
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

export const EpicGetCollegeKey: Epic<any, any> = EpicUtils.Create(request);
