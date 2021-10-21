/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  SET_SERVER_ERROR_VALUE,
  RESET_STORE,
  SET_IS_NEW_USER,
} from '../actions';
import { CollegeState } from '../../models/College';
import { IAction } from '../../models/IshAction';
import {
  CHECK_SITENAME,
  COLLEGE_WAS_CREATED,
  SET_CAPTCHA_TOKEN,
  SET_COLLEGE_KEY,
  SET_SEND_TOKEN_AGAIN_VALUE,
  SET_SITENAME_VALID_VALUE,
  SET_TEMPLATE_VALUE
} from '../actions/College';

const initState: CollegeState = {
  collegeKey: '',
  isNewUser: true,
  isValidName: false,
  webSiteTemplate: '',
  token: '',
  collegeWasCreated: false,
  sendTokenAgain: true,
  serverError: false
};

export const collegeReducer = (state: CollegeState = initState, action: IAction): CollegeState => {
  switch (action.type) {
    case SET_CAPTCHA_TOKEN:
      return {
        ...state,
        token: action.payload
      };

    case CHECK_SITENAME:
      return {
        ...state,
      };

    case SET_SITENAME_VALID_VALUE:
      return {
        ...state,
        isValidName: action.payload
      };

    case SET_IS_NEW_USER:
      return {
        ...state,
        isNewUser: action.payload
      };

    case SET_COLLEGE_KEY:
      return {
        ...state,
        collegeKey: action.payload
      };

    case SET_TEMPLATE_VALUE:
      return {
        ...state,
        webSiteTemplate: action.payload
      };

    case SET_SEND_TOKEN_AGAIN_VALUE:
      return {
        ...state,
        sendTokenAgain: action.payload
      };

    case COLLEGE_WAS_CREATED:
      return {
        ...state,
        collegeWasCreated: action.payload
      };

    case SET_SERVER_ERROR_VALUE:
      return {
        ...state,
        serverError: action.payload
      };

    case RESET_STORE:
      return {
        ...initState
      };

    default:
      return state;
  }
};
