/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GoogleLoginResponse } from 'react-google-login';

// Google Tag Manager
export type GTMAccount = gapi.client.tagmanager.Account;
export type GTMContainer = gapi.client.tagmanager.Container;
export type GTMVariable = gapi.client.tagmanager.Variable;
export type GTMTrigger = gapi.client.tagmanager.Trigger;
export type GTMTag = gapi.client.tagmanager.Tag;

// Google Analytics
export type GAAccount = gapi.client.analytics.Account;
export type GAWebProperty = gapi.client.analytics.Webproperty;
export type GAWebPropertyProfile = gapi.client.analytics.Profile;

export interface GoogleState {
  loading?: boolean;
  clientId?: string;
  profile?: GoogleLoginResponse['profileObj'],
  token?: GoogleLoginResponse['tokenObj'],
  gtmAccounts: GTMAccount[],
  gtmContainers: Record<GTMAccount['accountId'], GTMContainer[]>,
  gaAccounts: GAAccount[],
  gaWebProperties: Record<GAAccount['id'], GAWebProperty[]>,
}
