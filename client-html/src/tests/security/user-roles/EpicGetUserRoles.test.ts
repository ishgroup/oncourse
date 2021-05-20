import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetUserRoles } from "../../../js/containers/security/containers/user-roles/epics/EpicGetUserRoles";
import { GET_USER_ROLES_FULFILLED, getUserRoles } from "../../../js/containers/security/actions";

describe("Get security user roles epic tests", () => {
  it("EpicGetUserRoles should returns correct values", () => DefaultEpic({
    action: getUserRoles(),
    epic: EpicGetUserRoles,
    processData: mockedApi => {
      const userRoles = mockedApi.db.getSecurityRoles();

      userRoles.sort((a, b) => (a.name[0] > b.name[0] ? 1 : -1));

      return [
        {
          type: GET_USER_ROLES_FULFILLED,
          payload: { userRoles }
        }
      ];
    }
  }));
});
