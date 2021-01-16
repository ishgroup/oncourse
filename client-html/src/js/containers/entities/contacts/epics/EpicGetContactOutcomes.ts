/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_CONTACT_OUTCOMES, GET_CONTACT_OUTCOMES_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";

const getOutcome = (search: string) =>
  EntityService.getPlainRecords(
    "Outcome",
    "module.nationalCode,name,status,startDate,endDate,deliveryMode",
    search,
    0,
    0,
    "",
    true
  );

export const contactOutcomesMap = ({ values, id }) => ({
  id: Number(id),
  nationalCode: values[0],
  course: values[1],
  status: values[2],
  startDate: values[3],
  endDate: values[4],
  deliveryMode: values[5]
});

const request: EpicUtils.Request<any, any, any> = {
  type: GET_CONTACT_OUTCOMES,
  hideLoadIndicator: true,
  getData: contactId =>
    Promise.all([
      getOutcome(`enrolment.student.contact.id == ${contactId}`),
      getOutcome(`priorLearning.student.contact.id == ${contactId}`)
    ]),
  processData: (response: DataResponse[]) => {
    const combinedRows = [...response[0].rows, ...response[1].rows];
    const outcomes = combinedRows.map(contactOutcomesMap);

    return [
      {
        type: GET_CONTACT_OUTCOMES_FULFILLED,
        payload: { outcomes }
      }
    ];
  }
};

export const EpicGetContactOutcomes: Epic<any, any> = EpicUtils.Create(request);
