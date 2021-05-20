import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicUpdateUserRole } from "../../../js/containers/security/containers/user-roles/epics/EpicUpdateUserRole";
import { POST_USER_ROLES_FULFILLED, updateUserRole } from "../../../js/containers/security/actions";

describe("Update security user role epic tests", () => {
  it("EpicUpdateUserRole should returns correct values", () => DefaultEpic({
    action: mockedApi => updateUserRole(mockedApi.db.securityRoles.find(role => role.id === 200)),
    epic: EpicUpdateUserRole,
    processData: mockedApi => {
      const userRoles = mockedApi.db.getSecurityRoles();
      return [
        {
          type: POST_USER_ROLES_FULFILLED,
          payload: { userRoles }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "User Role was successfully saved" }
        }
      ];
    }
  }));
});
