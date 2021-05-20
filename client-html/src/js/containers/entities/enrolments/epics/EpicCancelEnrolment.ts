import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { CANCEL_ENROLMENT, CANCEL_ENROLMENT_FULFILLED, setEnrolmentTransfered } from "../actions";
import { initialize } from "redux-form";
import EnrolmentService from "../services/EnrolmentService";

const request: EpicUtils.Request = {
  type: CANCEL_ENROLMENT,
  getData: ({ values }) => EnrolmentService.cancelEnrolment(values),
  processData: (v, s, { type }) => {
    return [
      {
        type: CANCEL_ENROLMENT_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: `Enrolment ${type === "cancel" ? "cancelled" : type} successfully` }
      },
      setEnrolmentTransfered(type === "transfer" ? true : false),
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Enrolment", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: (response, { type }) => [...FetchErrorHandler(response, `Failed to ${type} Enrolment`)]
};

export const EpicCancelEnrolment: Epic<any, any> = EpicUtils.Create(request);
