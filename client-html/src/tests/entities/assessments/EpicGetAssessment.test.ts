import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetAssessment } from "../../../js/containers/entities/assessments/epics/EpicGetAssessment";
import { GET_ASSESSMENT_ITEM_FULFILLED, getAssessment } from "../../../js/containers/entities/assessments/actions";
import { getNoteItems } from "../../../js/common/components/form/notes/actions";

describe("Get assessment epic tests", () => {
  it("EpicGetAssessment should returns correct values", () => DefaultEpic({
    action: getAssessment("1"),
    epic: EpicGetAssessment,
    processData: mockedApi => {
      const assessment = mockedApi.db.getAssessment(1);
      return [
        {
          type: GET_ASSESSMENT_ITEM_FULFILLED,
          payload: { assessment }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: assessment, name: assessment.name }
        },
        getNoteItems("Assessment", "1" as any, LIST_EDIT_VIEW_FORM_NAME),
        initialize(LIST_EDIT_VIEW_FORM_NAME, assessment)
      ];
    }
  }));
});
