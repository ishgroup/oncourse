/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { END_FIELD_PROCESSING_ACTION, START_FIELD_PROCESSING_ACTION } from "ish-ui";
import { IAction } from "../actions/IshAction";

export const fieldProcessingReducer = (state: Record<string, boolean> = {}, action: IAction<any>): any => {
  switch (action.type) {
    case START_FIELD_PROCESSING_ACTION: {
      return {
        ...state,
        [action.payload]: true
      };
    }

    case END_FIELD_PROCESSING_ACTION: {
      const updated = {...state};
      if (updated[action.payload]) {
        delete updated[action.payload];
      }
      return updated;
    }

    default:
      return state;
  }
};
