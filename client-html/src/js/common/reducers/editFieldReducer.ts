/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PREVENT_EDIT_ON_FOCUS } from "../actions/EditFieldActions";
import { IAction } from "../actions/IshAction";

export interface EditFieldPlaceState {
  preventEditOnFocus?: boolean;
}

const initial: EditFieldPlaceState = {
  preventEditOnFocus: false
};

export const editFieldReducer = (state: any = initial, action: IAction<any>) => {
  switch (action.type) {
    case PREVENT_EDIT_ON_FOCUS: {
      return {
        ...state,
        preventEditOnFocus: action.payload.prevent
      };
    }

    default:
      return state;
  }
};
