import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicUpdateTutorRole } from "../../../js/containers/preferences/containers/tutor-roles/epics/EpicUpdateTutorRole";
import {
  GET_TUTOR_ROLE_REQUEST, GET_TUTOR_ROLES_REQUEST,
  UPDATE_TUTOR_ROLE_FULFILLED,
  updateTutorRole
} from "../../../js/containers/preferences/actions";

describe("Update tutor role epic tests", () => {
  it("EpicUpdateTutorRole should returns correct values", () => DefaultEpic({
    action: mockedApi => updateTutorRole(mockedApi.db.getTutorRole(1)),
    epic: EpicUpdateTutorRole,
    processData: () => [
      {
        type: UPDATE_TUTOR_ROLE_FULFILLED
      },
      {
        type: GET_TUTOR_ROLE_REQUEST,
        payload: { id: 1 }
      },
      {
        type: GET_TUTOR_ROLES_REQUEST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Tutor role updated" }
      }
    ]
  }));
});
