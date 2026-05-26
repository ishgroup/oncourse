/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { GoogleMapsCoordinates } from '../../../../model/google';
import { FETCH_FAIL } from '../../../actions';
import FetchErrorHandler from '../../../api/fetch-errors-handlers/FetchErrorHandler';

import * as EpicUtils from '../../../epics/EpicUtils';
import { GET_GOOGLE_GEOCODE_DETAILS, GET_GOOGLE_GEOCODE_DETAILS_FULFILLED } from '../actions';
import GoogleApiService from '../services/GoogleApiService';

const request: EpicUtils.Request<GoogleMapsCoordinates, { address: string }> = {
  type: GET_GOOGLE_GEOCODE_DETAILS,
  getData: ({ address }) => GoogleApiService.getGeocodeDetails(address),
  processData: response => (response
      ? [
          {
            type: GET_GOOGLE_GEOCODE_DETAILS_FULFILLED,
            payload: response
          }
        ]
      : [
          {
            type: FETCH_FAIL,
            payload: { message: "Google Api error" }
          }
        ]),
  processError: response => FetchErrorHandler(response, "Google Api error")
};

export const EpicGetGoogleGeocode: Epic<any, any> = EpicUtils.Create(request);
