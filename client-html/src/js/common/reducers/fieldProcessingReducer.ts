/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { IAction } from "../actions/IshAction";
import { END_FIELD_PROCESSING_ACTION, START_FIELD_PROCESSING_ACTION } from "../actions/FieldProcessing";
import { FieldProcessingAction } from "../../model/common/FieldProcessing";

export const fieldProcessingReducer = (state: FieldProcessingAction[] = [], action: IAction<any>): any => {
  switch (action.type) {
    case START_FIELD_PROCESSING_ACTION: {
      return state.concat(action.payload);
    }

    case END_FIELD_PROCESSING_ACTION: {
      return state.filter(a => a.id !== action.payload);
    }

    default:
      return state;
  }
};
