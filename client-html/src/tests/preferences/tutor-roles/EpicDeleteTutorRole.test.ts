import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicDeleteTutorRole } from "../../../js/containers/preferences/containers/tutor-roles/epics/EpicDeleteTutorRole";
import {
  DELETE_TUTOR_ROLE_FULFILLED,
  GET_TUTOR_ROLES_REQUEST,
  removeTutorRole
} from "../../../js/containers/preferences/actions";

describe("Delete tutor role epic tests", () => {
  it("EpicDeleteTutorRole should returns correct values", () => DefaultEpic({
    action: mockedApi => {
      const tutorRoles = mockedApi.db.getPlainTutorRoles();
      return removeTutorRole("1", tutorRoles.rows);
    },
    epic: EpicDeleteTutorRole,
    processData: () => [
      {
        type: DELETE_TUTOR_ROLE_FULFILLED
      },
      {
        type: GET_TUTOR_ROLES_REQUEST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Tutor role deleted" }
      }
    ]
  }));
});
