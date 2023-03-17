/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Create, Request } from "../../../../common/epics/EpicUtils";
import { Epic } from "redux-observable";
import { BULK_DELETE_WAITING_LISTS } from "../actions";
import { Diff } from "@api/model";
import WaitingListService from "../services/WaitingListService";
import { showMessage } from "../../../../common/actions";

const request: Request<any, Diff> = {
  type: BULK_DELETE_WAITING_LISTS,
  getData: diff => WaitingListService.bulkDelete(diff),
  processData: () => [
    showMessage({
      success: true,
      message: "Waiting lists deleted"
    })
  ],
};

export const EpicBulkDeleteWaitingLists: Epic = Create(request);