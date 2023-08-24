/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ConfirmState } from "ish-ui";
import { CLOSE_CONFIRM, OPEN_CONFIRM } from "../actions";
import { IAction } from "../actions/IshAction";

const Initial: ConfirmState = {
  open: false,
  onCancel: null,
  onConfirm: null,
  title: "Are you sure?",
  confirmMessage: "You have unsaved changes. Do you want to discard them and proceed?",
  cancelButtonText: "Cancel",
  confirmButtonText: "Agree",
  onCancelCustom: null,
};

export const confirmReducer = (state: ConfirmState = {...Initial}, action: IAction<any>): ConfirmState => {
  switch (action.type) {
    case OPEN_CONFIRM: {
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
