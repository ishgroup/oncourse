/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SwipeableDrawer } from "../../../../../model/common/drawer/SwipeableDrawerModel";
import { IAction } from "../../../../actions/IshAction";
import { SET_SWIPEABLE_DRAWER_SELECTION, TOGGLE_SWIPEABLE_DRAWER } from "../actions";

const initial: SwipeableDrawer = {
  opened: false,
  variant: "temporary",
  selected: null,
  resetEditView: () => {
  }
};

export const swipeableDrawerReducer = (state: SwipeableDrawer = initial, action: IAction<any>): any => {
  switch (action.type) {
    case TOGGLE_SWIPEABLE_DRAWER: {
      return {
        ...state,
        opened: !state.opened,
        variant: action.payload.variant
      };
    }

    case SET_SWIPEABLE_DRAWER_SELECTION: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
