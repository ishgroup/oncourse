/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GoogleLoginResponse } from 'react-google-login';
import { PropertiesDTO } from '@api/model';
import { IAction } from '../../models/IshAction';
import { FULFILLED } from './ActionUtils';
import { GoogleState } from '../../models/Google';
import { SiteValues } from '../../models/Sites';

export const SET_GOOGLE_CREDENTIALS = 'SET_GOOGLE_DATA';

export const GET_CLIENT_ID = 'GET_CLIENT_ID';
export const GET_CLIENT_ID_FULFILLED = FULFILLED(GET_CLIENT_ID);

export const GET_GTM_AND_GA_DATA = 'GET_GTM_AND_GA_DATA';
export const GET_GTM_AND_GA_DATA_FULFILLED = FULFILLED(GET_GTM_AND_GA_DATA);

export const CONFIGURE_GOOGLE_FOR_SITE = 'CONFIGURE_GOOGLE_FOR_SITE';

export const getClientId = (): IAction => ({
  type: GET_CLIENT_ID
});

export const getGtmAndGaData = (): IAction => ({
  type: GET_GTM_AND_GA_DATA
});

export const getGtmAndGaDataFulfilled = (state: GoogleState): IAction => ({
  type: GET_GTM_AND_GA_DATA_FULFILLED,
  payload: state
});

export const getClientIdFulfilled = (props: PropertiesDTO): IAction => ({
  type: GET_CLIENT_ID_FULFILLED,
  payload: props
});

export const setGoogleCredentials = (payload: {
  profile?: GoogleLoginResponse['profileObj'],
  token?: GoogleLoginResponse['tokenObj']
}): IAction => ({
  type: SET_GOOGLE_CREDENTIALS,
  payload
});

export const configureGoogleForSite = (site: SiteValues): IAction => ({
  type: CONFIGURE_GOOGLE_FOR_SITE,
  payload: site
});
