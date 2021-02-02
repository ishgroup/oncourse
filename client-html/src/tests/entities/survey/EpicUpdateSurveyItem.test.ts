import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateSurveyItem } from "../../../js/containers/entities/survey/epics/EpicUpdateSurveyItem";
import {
  GET_STUDENT_SURVEY_ITEM,
  UPDATE_SURVEY_ITEM_FULFILLED,
  updateSurveyItem
} from "../../../js/containers/entities/survey/actions";

describe("Update survey epic tests", () => {
  it("EpicUpdateSurveyItem should returns correct values", () => DefaultEpic({
    action: mockedApi => updateSurveyItem("1", mockedApi.db.getSurvey(1)),
    epic: EpicUpdateSurveyItem,
    processData: () => [
      {
        type: UPDATE_SURVEY_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Student Feedback Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Survey", listUpdate: true, savedID: "1" }
      },
      {
        type: GET_STUDENT_SURVEY_ITEM,
        payload: "1"
      }
    ]
  }));
});
