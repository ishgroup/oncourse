/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../common/actions/IshAction";
import { ApiMethods } from "./apiHandlers";

export interface QueuedAction {
  method: ApiMethods;
  entity: string;
  id: number | string;
  actionBody: IAction<any>;
  // action ID to call on target action complete
  bindedActionId?: number | string;
}

export interface ActionsQueueState {
  queuedActions: QueuedAction[];
}
