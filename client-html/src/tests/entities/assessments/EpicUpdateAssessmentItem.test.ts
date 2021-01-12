import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateAssessmentItem } from "../../../js/containers/entities/assessments/epics/EpicUpdateAssessmentItem";
import {
  GET_ASSESSMENT_ITEM,
  UPDATE_ASSESSMENT_ITEM_FULFILLED,
  updateAssessment
} from "../../../js/containers/entities/assessments/actions";

describe("Update assessment epic tests", () => {
  it("EpicUpdateAssessmentItem should returns correct values", () => DefaultEpic({
    action: mockedApi => updateAssessment("2", {
      id: 2,
      code: "code 2",
      name: "code 2",
      description: "test description 1",
      active: true,
      documents: [],
      tags: [mockedApi.db.getTag(1)]
    }),
    epic: EpicUpdateAssessmentItem,
    processData: () => [
      {
        type: UPDATE_ASSESSMENT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Assessment Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Assessment", listUpdate: true, savedID: "2" }
      },
      {
        type: GET_ASSESSMENT_ITEM,
        payload: "2"
      }
    ]
  }));
});
