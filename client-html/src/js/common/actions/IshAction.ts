/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Action } from "redux";

export interface IAction<P = any> extends Action {
  type: string;
  payload?: P;
  meta?: any;
}
