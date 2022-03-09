/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../../actions/IshAction";
import { SwipeableDrawer } from "../../../../../model/common/drawer/SwipeableDrawerModel";
import {
  CLEAR_SELECTED_CATEGORY_ITEM,
  SET_SELECTED_CATEGORY_ITEM,
  SET_SWIPEABLE_DRAWER_DIRTY_FORM,
  TOGGLE_SWIPEABLE_DRAWER
} from "../actions";

const initial: SwipeableDrawer = {
  opened: false,
  variant: "temporary",
  isDirty: false,
  resetEditView: () => {},
  categoryItem: {
    entity: null,
    id: null,
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

    case SET_SWIPEABLE_DRAWER_DIRTY_FORM: {
      return {
        ...state,
        isDirty: action.payload.isDirty,
        resetEditView: action.payload.resetEditView
      };
    }

    case SET_SELECTED_CATEGORY_ITEM: {
      return {
        ...state,
        categoryItem: {
          entity: action.payload.entity,
          id: action.payload.id,
        }
      };
    }

    case CLEAR_SELECTED_CATEGORY_ITEM: {
      return {
        ...state,
        categoryItem: {
          entity: null,
          id: null,
        }
      };
    }

    default:
      return state;
  }
};
