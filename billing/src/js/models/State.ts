/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SiteDTO } from '@api/model';
import { CollegeState } from 'src/js/models/College';
import { GoogleState } from './Google';
import { MessageState } from './Message';
import { FormsState } from './Forms';

export interface State {
  college: CollegeState;
  loading: boolean;
  sites: SiteDTO[];
  google: GoogleState,
  message: MessageState,
  form: FormsState,
}
