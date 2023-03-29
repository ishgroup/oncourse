/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { _toRequestType } from "../../../../common/actions/ActionUtils";
import { Diff } from "@api/model";

export const BULK_DELETE_WAITING_LISTS = _toRequestType("delete/bulk/waitingList");

export const bulkDeleteWaitingLists = (diff: Diff) => ({
  type: BULK_DELETE_WAITING_LISTS,
  payload: diff
});