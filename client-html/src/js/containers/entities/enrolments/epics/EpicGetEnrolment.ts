/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Enrolment } from "@api/model";
import { initialize } from "redux-form";
import { clearActionsQueue } from "../../../../common/actions";
import { getNoteItems } from "../../../../common/components/form/notes/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ENROLMENT_ITEM, GET_ENROLMENT_ITEM_FULFILLED } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EnrolmentService from "../services/EnrolmentService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import EntityService from "../../../../common/services/EntityService";
import { getContactName } from "../../contacts/utils";
import { EnrolmentExtended } from "../../../../model/entities/Enrolment";

const request: EpicUtils.Request = {
  type: GET_ENROLMENT_ITEM,
  getData: (id: number) => EnrolmentService.getEnrolment(id).then(async (en: EnrolmentExtended) => {
    for (const a of en.assessments) {
      await EntityService.getPlainRecords(
        "Contact",
        "firstName,lastName",
        `tutor.assessmentClassTutors.assessmentClass.courseClass.id is ${en.courseClassId} and tutor.assessmentClassTutors.assessmentClass.assessment.id is ${a.id}`
      )
      .then(res => {
        a.tutors = res.rows.map(r => ({
          contactId: Number(r.id),
          tutorName: getContactName({ firstName: r.values[0], lastName: r.values[1] })
        }));
      });
    }

    return en;
  }),
  processData: (enrolment: Enrolment, s, id) => {
    if (enrolment.relatedFundingSourceId === null) {
      enrolment.relatedFundingSourceId = -1;
    }
    return [
      {
        type: GET_ENROLMENT_ITEM_FULFILLED,
        payload: { enrolment }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: enrolment, name: enrolment.studentName }
      },
      getNoteItems("Enrolment", id, LIST_EDIT_VIEW_FORM_NAME),
      initialize(LIST_EDIT_VIEW_FORM_NAME, enrolment),
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetEnrolment: Epic<any, any> = EpicUtils.Create(request);
