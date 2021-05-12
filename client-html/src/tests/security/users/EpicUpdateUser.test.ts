import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  POST_USER_REQUEST_FULFILLED,
  updateUser
} from "../../../js/containers/security/actions";
import { EpicUpdateUser } from "../../../js/containers/security/containers/users/epics/EpicUpdateUser";

describe("Update security user epic tests", () => {
  it("EpicUpdateUser should returns correct values", () => DefaultEpic({
    action: mockedApi => updateUser(mockedApi.db.securityUsers.find(user => user.id === 351)),
    epic: EpicUpdateUser,
    processData: mockedApi => {
      const users = mockedApi.db.getSecurityUsers();
      return [
        {
          type: POST_USER_REQUEST_FULFILLED,
          payload: { users }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "User settings was successfully updated" }
        }
      ];
    }
  }));
});
