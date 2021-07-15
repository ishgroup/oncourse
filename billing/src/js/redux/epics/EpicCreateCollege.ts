import { Epic } from 'redux-observable';
import * as EpicUtils from './EpicUtils';
import { COLLEGE_WAS_CREATED, CREATE_COLLEGE, SET_LOADING_VALUE, SET_SERVER_ERROR_VALUE } from '../actions';
import BillingService from '../../api/services/BillingService';

const request: EpicUtils.Request = {
  type: CREATE_COLLEGE,
  getData: (data) => BillingService.createCollege(data),
  processData: () => [
    { type: COLLEGE_WAS_CREATED, payload: true },
    { type: SET_LOADING_VALUE, payload: false }
  ],
  processError: (response) => [
    { type: SET_LOADING_VALUE, payload: false },
    { type: SET_SERVER_ERROR_VALUE, payload: true },
  ]
};

export const EpicCreateCollege: Epic<any, any> = EpicUtils.Create(request);
