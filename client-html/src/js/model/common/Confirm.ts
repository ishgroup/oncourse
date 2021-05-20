/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ReactNode } from "react";
import { AnyArgFunction } from "./CommonFunctions";

export interface ConfirmProps {
  onCancel?: AnyArgFunction;
  onConfirm?: AnyArgFunction;
  title?: string;
  confirmMessage?: ReactNode;
  cancelButtonText?: string;
  confirmButtonText?: string;
  onCancelCustom?: AnyArgFunction;
  confirmCustomComponent?: ReactNode;
}

export interface ConfirmState extends ConfirmProps {
  open: boolean;
}

export type ShowConfirmCaller = (props: ConfirmProps) => any;
