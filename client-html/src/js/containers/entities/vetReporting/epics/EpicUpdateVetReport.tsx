/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { Create, Request } from "../../../../common/epics/EpicUtils";
import { ListActionEntity } from "../../../../model/entities/common";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import { UPDATE_VET_REPORT_ENTITIES } from "../actions";
import { VetReport } from "../../../../model/entities/VetReporting";
import { updateEntityItemById } from "../../common/entityItemsService";
import { executeActionsQueue, FETCH_SUCCESS } from "../../../../common/actions";
import { getRecords } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: Request<any, { item: VetReport, entity: ListActionEntity }> = {
  type: UPDATE_VET_REPORT_ENTITIES,
  getData: async ({ item }) => {
    await updateEntityItemById("Contact", item.student.id, item.student);
    if (item.enrolment.id) {
      await updateEntityItemById("Enrolment", item.enrolment.id, item.enrolment);
    }
    if (item.outcome?.id) {
      await updateEntityItemById("Outcome", item.outcome.id, item.outcome);
    }
    return item;
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { item }) => [
    executeActionsQueue(),
    {
      type: FETCH_SUCCESS,
      payload: { message: "Vet report records updated" }
    },
    getRecords({ entity: "Contact", listUpdate: true, savedID: item.id }),
    initialize(LIST_EDIT_VIEW_FORM_NAME, item)
  ],
  processError: (response, { item }) => [
    ...FetchErrorHandler(response, "Vet report items was not updated"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, item)
  ]
};

export const EpicUpdateVetReport: Epic = Create(request);