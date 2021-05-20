import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateEnrolmentItem } from "../../../js/containers/entities/enrolments/epics/EpicUpdateEnrolmentItem";
import {
  UPDATE_ENROLMENT_ITEM_FULFILLED,
  updateEnrolment
} from "../../../js/containers/entities/enrolments/actions";

describe("Update enrolment epic tests", () => {
  it("EpicUpdateEnrolmentItem should returns correct values", () => {
    const id = 2;
    return DefaultEpic({
      action: mockedApi => updateEnrolment(id, mockedApi.db.getEnrolment(id)),
      epic: EpicUpdateEnrolmentItem,
      processData: () => [
        {
          type: UPDATE_ENROLMENT_ITEM_FULFILLED
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Enrolment Record updated" }
        },
        {
          type: GET_RECORDS_REQUEST,
          payload: { entity: "Enrolment", listUpdate: true, savedID: id }
        }
      ]
    });
  });
});
