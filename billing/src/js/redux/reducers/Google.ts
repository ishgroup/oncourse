/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from '../../models/IshAction';
import { GoogleState } from '../../models/Google';

const Initial: GoogleState = {
  userEmail: null
};

export const googleReducer = (state: GoogleState = Initial, action: IAction): GoogleState => {
  switch (action.type) {
    default:
      return {
        ...state
      };
  }
};
