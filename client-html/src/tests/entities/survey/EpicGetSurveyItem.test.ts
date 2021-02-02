import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetSurveyItem } from "../../../js/containers/entities/survey/epics/EpicGetSurveyItem";
import { GET_STUDENT_SURVEY_ITEM_FULFILLED, getSurveyItem } from "../../../js/containers/entities/survey/actions";

describe("Get survey epic tests", () => {
  it("EpicGetSurveyItem should returns correct values", () => DefaultEpic({
    action: getSurveyItem("1"),
    epic: EpicGetSurveyItem,
    processData: mockedApi => {
      const surveyItem = mockedApi.db.getSurvey(1);
      return [
        {
          type: GET_STUDENT_SURVEY_ITEM_FULFILLED,
          payload: { surveyItem }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: surveyItem, name: surveyItem.studentName + " - " + surveyItem.className }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, surveyItem)
      ];
    }
  }));
});
