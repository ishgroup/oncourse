/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_CONTACT_PRIOR_LEARNINGS, GET_CONTACT_PRIOR_LEARNINGS_FULFILLED } from "../actions";

export const contactPriorLearningsMap = ({ values, id }) => ({
  id: Number(id),
  title: values[0],
  externalRef: values[1],
  qualNationalCode: values[2],
  qualLevel: values[3],
  qualName: values[4]
});

const request: EpicUtils.Request = {
  type: GET_CONTACT_PRIOR_LEARNINGS,
  hideLoadIndicator: true,
  getData: contactId =>
    EntityService.getPlainRecords(
      "PriorLearning",
      "title,externalRef,qualification.nationalCode,qualification.level,qualification.title",
      `student.contact.id == ${contactId}`,
      0,
      0,
      "",
      true
    ),
  processData: (response: DataResponse) => {
    const priorLearnings = response.rows.map(contactPriorLearningsMap);

    return [
      {
        type: GET_CONTACT_PRIOR_LEARNINGS_FULFILLED,
        payload: { priorLearnings }
      }
    ];
  }
};

export const EpicGetContactPriorLearnings: Epic<any, any> = EpicUtils.Create(request);
