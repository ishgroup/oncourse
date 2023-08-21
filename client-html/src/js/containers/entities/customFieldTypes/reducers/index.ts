/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../../common/actions/IshAction";
import { GET_CUSTOM_FIELD_TYPES, GET_CUSTOM_FIELD_TYPES_FULFILLED } from "../actions";
import { CustomFieldTypesState } from "./state";

const initial: CustomFieldTypesState = {
  types: {},
  updating: false
};

export const customFieldTypesReducer = (state: CustomFieldTypesState = initial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_CUSTOM_FIELD_TYPES: {
      return {
        ...state,
        updating: true
      };
    }

    case GET_CUSTOM_FIELD_TYPES_FULFILLED: {
      return {
        ...state,
        types: {
          ...state.types,
          [action.payload.entity]: action.payload.types
        },
        updating: false
      };
    }

    default:
      return state;
  }
};
