/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Currency } from '@api/model';

export interface CollegeState {
  collegeKey: string;
  isValidName: boolean;
  isNewUser: boolean;
  webSiteTemplate: string;
  token: string;
  collegeWasCreated: boolean;
  sendTokenAgain: boolean;
  serverError: boolean;
  currency: Currency;
}
