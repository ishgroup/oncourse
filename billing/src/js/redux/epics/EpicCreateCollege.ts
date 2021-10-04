import { Epic } from 'redux-observable';
import * as EpicUtils from './EpicUtils';
import {
  setLoadingValue, setServerErrorValue
} from '../actions';
import BillingService from '../../api/services/BillingService';
import { CREATE_COLLEGE, setCollegeWasCreatedValue } from '../actions/College';

const request: EpicUtils.Request = {
  type: CREATE_COLLEGE,
  getData: (data) => BillingService.createCollege(data),
  processData: () => [
    setCollegeWasCreatedValue(true),
    setLoadingValue(false)
  ],
  processError: () => [
    setLoadingValue(false),
    setServerErrorValue(true),
  ]
};

export const EpicCreateCollege: Epic<any, any> = EpicUtils.Create(request);
