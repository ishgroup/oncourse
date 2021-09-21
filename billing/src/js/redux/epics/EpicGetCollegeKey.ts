import { Epic } from 'redux-observable';
import * as EpicUtils from './EpicUtils';
import {
  GET_COLLEGE_KEY, setCollegeKey, setIsNewUser, setLoadingValue
} from '../actions';
import BillingService from '../../api/services/BillingService';
import FetchErrorHandler from '../../api/fetch-errors-handlers/FetchErrorHandler';

const request: EpicUtils.Request = {
  type: GET_COLLEGE_KEY,
  getData: () => BillingService.getCollegeKey(),
  processData: (payload) => [
    setIsNewUser(false),
    setCollegeKey(payload)
  ],
  processError: (response) => {
    if (response?.status && [401, 406].includes(response.status)) {
      return [
        setLoadingValue(false),
      ];
    }
    return FetchErrorHandler(response);
  }
};

export const EpicGetCollegeKey: Epic<any, any> = EpicUtils.Create(request);
