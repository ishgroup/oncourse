import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  POST_USER_REQUEST_FULFILLED, resetUserPassword
} from "../../../js/containers/security/actions";
import { EpicResetUserPassword } from "../../../js/containers/security/containers/users/epics/EpicResetUserPassword";
import { showMessage } from "../../../js/common/actions";

describe("Reset users password epic tests", () => {
  it("EpicResetUserPassword should returns correct values", () => DefaultEpic({
    action: resetUserPassword(351),
    epic: EpicResetUserPassword,
    processData: mockedApi => {
      const users = mockedApi.db.getSecurityUsers();

      return [
        {
          type: POST_USER_REQUEST_FULFILLED,
          payload: { users }
        },
        showMessage({ message: "Invitation was successfully sent", success: true }),
        initialize("UsersForm", users.find(u => u.id === 351))
      ];
    }
  }));
});
