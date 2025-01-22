/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { executeActionsQueue } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { Create, Request } from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { PROCESS_OTCOME_CHANGE_FIELDS } from "../actions";

const request: Request<null, number[]> = {
  type: PROCESS_OTCOME_CHANGE_FIELDS,
  getData: (enrolmentIds, s ) => {

    const filtered = s.enrolments.changedOutcomeFields.filter(f => f.update);

    if (!filtered.length) return Promise.resolve();
    
    return EntityService
      .getPlainRecords("Outcome", "id", `enrolment.id in (${enrolmentIds.toString()})`)
      .then(res => {
        const ids = res.rows.map(r => Number(r.id));
        return EntityService.bulkChange('Outcome', {
          ids,
          diff: filtered.reduce((p, o) => {
            p[o.name] = o.value;
            return p;
          }, {})
        });
      });
  },
  processData: () => [
    executeActionsQueue(),
  ],
  processError: r => FetchErrorHandler(r, "Failed to update related outcomes")
};

export const EpicProcessOutcomeRelatedFields = Create(request);