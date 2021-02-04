import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetTutorRoles } from "../../../js/containers/preferences/containers/tutor-roles/epics/EpicGetTutorRoles";
import { GET_TUTOR_ROLES_FULFILLED, getTutorRoles } from "../../../js/containers/preferences/actions";

describe("Get tutor roles epic tests", () => {
  it("EpicGetTutorRoles should returns correct values", () => DefaultEpic({
    action: getTutorRoles(),
    epic: EpicGetTutorRoles,
    processData: mockedApi => {
      const response = mockedApi.db.getPlainTutorRoles();

      const tutorRoles = response.rows.map(r => ({
        id: r.id,
        name: r.values[0],
        description: r.values[1],
        active: r.values[2],
        grayOut: r.values[2] === "false"
      }));

      return [
        {
          type: GET_TUTOR_ROLES_FULFILLED,
          payload: { tutorRoles }
        }
      ];
    }
  }));
});
