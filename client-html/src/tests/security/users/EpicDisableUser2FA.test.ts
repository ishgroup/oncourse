import { setSubmitSucceeded } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  DISABLE_USER_2FA_FULFILLED,
  disableUser2FA
} from "../../../js/containers/security/actions";
import { EpicDisableUser2FA } from "../../../js/containers/security/containers/users/epics/EpicDisableUser2FA";

describe("Disabled two factor authentication of security user epic tests", () => {
  it("EpicDisableUser2FA should returns correct values", () => DefaultEpic({
    action: disableUser2FA(300),
    epic: EpicDisableUser2FA,
    processData: mockedApi => {
      const users = mockedApi.db.getSecurityUsers();
      return [
        {
          type: DISABLE_USER_2FA_FULFILLED,
          payload: { users }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "2FA was successfully disabled" }
        },
        setSubmitSucceeded("UsersForm")
      ];
    }
  }));
});
