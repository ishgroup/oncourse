import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicDeleteUserRole } from "../../../js/containers/security/containers/user-roles/epics/EpicDeleteUserRole";
import { DELETE_USER_ROLES_FULFILLED, removeUserRole } from "../../../js/containers/security/actions";

describe("Delete security user role epic tests", () => {
  it("EpicDeleteUserRole should returns correct values", () => DefaultEpic({
    action: () => removeUserRole(200),
    epic: EpicDeleteUserRole,
    processData: mockedApi => {
      const userRoles = mockedApi.db.securityRoles.filter(role => Number(role.id) !== 200);
      return [
        {
          type: DELETE_USER_ROLES_FULFILLED,
          payload: { userRoles }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "User Role was successfully deleted" }
        }
      ];
    }
  }));
});
