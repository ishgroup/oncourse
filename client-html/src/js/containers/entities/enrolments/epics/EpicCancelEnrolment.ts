import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { CancelEnrolment } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { CANCEL_ENROLMENT, setEnrolmentsDialog, setEnrolmentsProcessing } from "../actions";
import EnrolmentService from "../services/EnrolmentService";
import { openInternalLink } from "../../../../common/utils/links";

const request: EpicUtils.Request<any, { values: CancelEnrolment, type: string }> = {
  type: CANCEL_ENROLMENT,
  getData: ({ values }) => EnrolmentService.cancelEnrolment(values),
  processData: (v, s, { type, values: { enrolmentIds } }) => {
    if (type === "transfer") {
      openInternalLink(`/checkout?enrolmentId=${enrolmentIds}`);
    }
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: `Enrolment ${type === "cancel" ? "cancelled" : type} successfully` }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Enrolment", listUpdate: true }
      },
      setEnrolmentsProcessing(false),
      setListSelection([]),
      setEnrolmentsDialog(null),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: (response, { type }) => [...FetchErrorHandler(response, `Failed to ${type} Enrolment`)]
};

export const EpicCancelEnrolment: Epic<any, any> = EpicUtils.Create(request);