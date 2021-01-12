import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteAssessment } from "../../../js/containers/entities/assessments/epics/EpicDeleteAssessment";
import { DELETE_ASSESSMENT_ITEM_FULFILLED, removeAssessment } from "../../../js/containers/entities/assessments/actions";

describe("Delete assessment epic tests", () => {
  it("EpicDeleteAssessment should returns correct values", () => DefaultEpic({
    action: removeAssessment("1"),
    epic: EpicDeleteAssessment,
    processData: () => [
      {
        type: DELETE_ASSESSMENT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Assessment record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Assessment", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
