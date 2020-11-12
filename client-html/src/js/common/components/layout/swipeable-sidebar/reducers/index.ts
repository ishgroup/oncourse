/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../../actions/IshAction";
import { SwipeableDrawer } from "../../../../../model/common/drawer/SwipeableDrawerModel";
import { SET_SWIPEABLE_DRAWER_DIRTY_FORM, TOGGLE_SWIPEABLE_DRAWER } from "../actions";
import { VARIANTS } from "../utils";

const initial: SwipeableDrawer = {
  opened: false,
  variant: VARIANTS.temporary,
  isDirty: false,
  resetEditView: () => {}
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

    case SET_SWIPEABLE_DRAWER_DIRTY_FORM: {
      return {
        ...state,
        isDirty: action.payload.isDirty,
        resetEditView: action.payload.resetEditView
      }
    }

    default:
      return state;
  }
};
