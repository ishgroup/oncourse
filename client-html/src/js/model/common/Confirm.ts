/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AnyArgFunction } from "./CommonFunctions";
import { ReactNode } from "react";

export interface ConfirmState {
  open: boolean;
  onCancel?: AnyArgFunction;
  onConfirm?: AnyArgFunction;
  title?: string;
  confirmMessage?: string;
  cancelButtonText?: string;
  confirmText?: string;
  confirmButtonText?: string;
  onCancelCustom?: AnyArgFunction;
  confirmCustomComponent?: ReactNode;
}

export type ShowConfirmCaller = (
  onConfirm: any,
  confirmMessage?: string,
  confirmButtonText?: string,
  onCancel?: any,
  title?: string,
  cancelButtonText?: string,
  onCancelCustom?: any,
  confirmCustomComponent?: ReactNode,
) => any;
