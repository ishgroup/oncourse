/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GoogleLoginResponse } from 'react-google-login';
import { PropertiesDTO } from '@api/model';
import { IAction } from '../../models/IshAction';
import { FULFILLED } from './ActionUtils';
import { GAProfilesRequestAction, GoogleState } from '../../models/Google';
import { SiteValues } from '../../models/Sites';

export const SET_GOOGLE_CREDENTIALS = 'SET_GOOGLE_DATA';

export const GET_CLIENT_ID = 'GET_CLIENT_ID';
export const GET_CLIENT_ID_FULFILLED = FULFILLED(GET_CLIENT_ID);

export const GET_GTM_AND_GA_ACCOUNTS = 'GET_GTM_AND_GA_ACCOUNTS';
export const GET_GTM_DATA_BY_ACCOUNT = 'GET_GTM_DATA_BY_ACCOUNT';
export const GET_GA_WEB_PROPERTIES_BY_ACCOUNT = 'GET_GA_WEB_PROPERTIES_BY_ACCOUNT';
export const GET_GA_PROFILES = 'GET_GA_PROFILES';
export const SET_GTM_AND_GA_DATA = 'SET_GTM_AND_GA_DATA';

export const CONFIGURE_GOOGLE_FOR_SITE = 'CONFIGURE_GOOGLE_FOR_SITE';

export const getClientId = (): IAction => ({
  type: GET_CLIENT_ID
});

export const getGtmAndGaAccounts = (): IAction => ({
  type: GET_GTM_AND_GA_ACCOUNTS
});

export const getGtmDataByAccount = (gtmAccountId: string): IAction => ({
  type: GET_GTM_DATA_BY_ACCOUNT,
  payload: gtmAccountId
});

export const getGaWebPropertiesByAccount = (gaAccountId: string): IAction => ({
  type: GET_GA_WEB_PROPERTIES_BY_ACCOUNT,
  payload: gaAccountId
});

export const getGaProfiles = (gaAccountId: string, gaPropertyId: string): IAction<GAProfilesRequestAction> => ({
  type: GET_GA_PROFILES,
  payload: { gaAccountId, gaPropertyId }
});

export const setGtmAndGaData = (state: GoogleState): IAction => ({
  type: SET_GTM_AND_GA_DATA,
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
