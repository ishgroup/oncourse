import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCancelEnrolment } from "../../../js/containers/entities/enrolments/epics/EpicCancelEnrolment";
import {
  CANCEL_ENROLMENT_FULFILLED,
  cancelEnrolment,
  setEnrolmentTransfered
} from "../../../js/containers/entities/enrolments/actions";

describe("Cancel/ transfer enrolment epic tests", () => {
  it("EpicCancelEnrolment should returns correct values", () => DefaultEpic({
    action: cancelEnrolment({
      enrolmentIds: 1,
      deleteNotSetOutcomes: false,
      transfer: false,
      invoiceLineParam: [
        {
          accountId: null,
          cancellationFee: null,
          invoiceLineId: 1,
          sendInvoice: false,
          taxId: 1
        }
      ]
    }, "cancel"),
    epic: EpicCancelEnrolment,
    processData: () => {
      const type = "cancel";
      return [
        {
          type: CANCEL_ENROLMENT_FULFILLED
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: `Enrolment ${type === "cancel" ? "cancelled" : type} successfully` }
        },
        setEnrolmentTransfered(false),
        {
          type: GET_RECORDS_REQUEST,
          payload: { entity: "Enrolment", listUpdate: true }
        },
        setListSelection([]),
        initialize(LIST_EDIT_VIEW_FORM_NAME, null)
      ];
    }
  }));
});
