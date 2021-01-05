/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../actions/IshAction";
import { OPEN_CONFIRM, CLOSE_CONFIRM } from "../actions";
import { ConfirmState } from "../../model/common/Confirm";

const Initial: ConfirmState = {
  open: false,
  onCancel: null,
  onConfirm: null,
  title: "Are you sure?",
  confirmMessage: null,
  cancelButtonText: "Cancel",
  confirmButtonText: "Agree",
  onCancelCustom: null,
};

export const confirmReducer = (state: ConfirmState = { ...Initial }, action: IAction<any>): ConfirmState => {
  switch (action.type) {
    case OPEN_CONFIRM: {
      for (const k in action.payload) {
        if (action.payload[k] === undefined) {
          delete action.payload[k];
        }
      }

      return {
        ...Initial,
        ...action.payload,
        open: true
      };
    }

    case CLOSE_CONFIRM: {
      return {
        ...state,
        open: false
      };
    }

    default:
      return state;
  }
};
