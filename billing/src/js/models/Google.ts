/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GoogleLoginResponse } from 'react-google-login';

export interface GoogleState {
  profile: GoogleLoginResponse['profileObj'],
  token: GoogleLoginResponse['tokenObj']
}

export interface GTMAccount {
  path?: string,
  accountId?: string,
  name?: string,
  shareData?: boolean,
  fingerprint?: string,
  tagManagerUrl?: string
}
