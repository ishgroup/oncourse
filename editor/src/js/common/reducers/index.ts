import * as Actions from "../actions";
import {CmsConfig} from "../../configLoader";

/**
 * Handle changing config properties.
 */

export const configReducer = (state: CmsConfig = new CmsConfig({}), action): CmsConfig => {
  switch (action.type) {

    case Actions.UPDATE_CMS_CONFIG:
      return action.payload;
    default:
      return state;
  }
};


/**
 * Global request handler
 * All requests should be converted by _toRequestType function,
 * fulfilled requests should be converted by FULFILLED function,
 * server errors should be contain `_ERROR` postfix
 */
export const fetchReducer = (state: boolean = false, action): boolean => {
  switch (true) {

    case /\/request$/.test(action.type):
      return true;

    case /_FULFILLED$/.test(action.type):
      return false;

    case /_ERROR$/.test(action.type):
      return false;

    default:
      return state;
  }
};
