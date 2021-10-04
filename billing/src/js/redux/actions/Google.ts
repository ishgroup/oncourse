/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GoogleLoginResponse } from 'react-google-login';
import { IAction } from '../../models/IshAction';

export const SET_GOOGLE_CREDENTIALS = 'SET_GOOGLE_DATA';

export const setGoogleCredentials = (
  profile: GoogleLoginResponse['profileObj'],
  token: GoogleLoginResponse['tokenObj']
): IAction => ({
  type: SET_GOOGLE_CREDENTIALS,
  payload: {
    profile,
    token
  }
});
