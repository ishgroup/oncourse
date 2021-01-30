import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateQualification } from "../../../js/containers/entities/qualifications/epics/EpicUpdateQualification";
import {
  GET_QUALIFICATION_ITEM,
  UPDATE_QUALIFICATION_ITEM_FULFILLED,
  updateQualification
} from "../../../js/containers/entities/qualifications/actions";

describe("Update qualification epic tests", () => {
  it("EpicUpdateQualification should returns correct values", () => DefaultEpic({
    action: mockedApi => updateQualification("1", mockedApi.db.getQualification(1)),
    epic: EpicUpdateQualification,
    processData: () => [
      {
        type: UPDATE_QUALIFICATION_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Qualification was updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Qualification", listUpdate: true, savedID: "1" }
      },
      {
        type: GET_QUALIFICATION_ITEM,
        payload: "1"
      }
    ]
  }));
});
