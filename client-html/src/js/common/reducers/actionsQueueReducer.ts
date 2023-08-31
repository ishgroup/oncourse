/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ActionsQueueState } from "../../model/common/ActionsQueue";
import { ApiMethods } from "../../model/common/apiHandlers";
import { ADD_ACTION_TO_QUEUE, CLEAR_ACTIONS_QUEUE, REMOVE_ACTIONS_FROM_QUEUE } from "../actions";
import { IAction } from "../actions/IshAction";

const Initial: ActionsQueueState = {
  queuedActions: []
};

export const actionsQueueReducer = (
  state: ActionsQueueState = {...Initial},
  action: IAction<any>
): ActionsQueueState => {
  switch (action.type) {
    case ADD_ACTION_TO_QUEUE: {
      let queuedActions = [...state.queuedActions];

      const sameItemActonIndex = state.queuedActions.findIndex(
        a => a.entity === action.payload.entity && a.id === action.payload.id
      );

      switch (action.payload.method as ApiMethods) {
        case "POST":
        case "PUT": {
          if (sameItemActonIndex !== -1) {
            queuedActions.splice(sameItemActonIndex, 1, action.payload);
          } else {
            queuedActions.push(action.payload);
          }
          break;
        }

        case "DELETE": {
          queuedActions.push(action.payload);
          if (sameItemActonIndex !== -1) {
            queuedActions = queuedActions.filter(a => a.entity !== action.payload.entity || a.id !== action.payload.id);
          }
        }
      }

      return {
        ...state,
        queuedActions
      };
    }

    case REMOVE_ACTIONS_FROM_QUEUE: {
      let queuedActions = state.queuedActions;

      if (action.payload.meta) {
        action.payload.meta.forEach(m => {
          queuedActions = m.id
            ? state.queuedActions.filter(a => a.entity !== m.entity || a.id !== m.id)
            : state.queuedActions.filter(a => a.entity !== m.entity);
        });
      }

      return {
        ...state,
        queuedActions
      };
    }

    case CLEAR_ACTIONS_QUEUE: {
      return {
        ...Initial
      };
    }

    default:
      return state;
  }
};
