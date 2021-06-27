import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_USERS_REQUEST_FULFILLED,
  getUsers
} from "../../../js/containers/security/actions";
import { EpicGetUsers } from "../../../js/containers/security/containers/users/epics/EpicGetUsers";

describe("Get security users epic tests", () => {
  it("EpicGetUsers should returns correct values", () => DefaultEpic({
    action: getUsers(),
    epic: EpicGetUsers,
    processData: mockedApi => {
      const users = mockedApi.db.getSecurityUsers();

      return [
        {
          type: GET_USERS_REQUEST_FULFILLED,
          payload: { users }
        }
      ];
    }
  }));
});
