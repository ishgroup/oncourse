import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicCreateTutorRole } from "../../../js/containers/preferences/containers/tutor-roles/epics/EpicCreateTutorRole";
import {
  CREATE_TUTOR_ROLE_FULFILLED,
  createTutorRole,
  GET_TUTOR_ROLES_REQUEST
} from "../../../js/containers/preferences/actions";

describe("Create tutor role epic tests", () => {
  it("EpicCreateTutorRole should returns correct values", () => DefaultEpic({
    action: mockedApi => createTutorRole(mockedApi.db.getTutorRole(1)),
    epic: EpicCreateTutorRole,
    processData: mockedApi => {
      const tutorRole = mockedApi.db.getTutorRole(1);

      return [
        {
          type: CREATE_TUTOR_ROLE_FULFILLED
        },
        {
          type: GET_TUTOR_ROLES_REQUEST,
          payload: { keyCodeToSelect: tutorRole.name }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Tutor role created" }
        }
      ];
    }
  }));
});
