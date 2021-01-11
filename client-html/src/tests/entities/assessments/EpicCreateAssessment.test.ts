import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { CREATE_ASSESSMENT_ITEM_FULFILLED, createAssessment } from "../../../js/containers/entities/assessments/actions";
import { EpicCreateAssessment } from "../../../js/containers/entities/assessments/epics/EpicCreateAssessment";

describe("Create assessment epic tests", () => {
  it("EpicCreateAssessment should returns correct values", () => DefaultEpic({
    action: mockedApi => createAssessment(mockedApi.db.createNewAssessment()),
    epic: EpicCreateAssessment,
    processData: () => [
      {
        type: CREATE_ASSESSMENT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Assessment Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Assessment" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
