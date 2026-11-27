import { DefaultEpic } from "../../common/Default.Epic";
import FetchErrorHandler from "../../../js/common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../js/common/services/EntityService";
import { EpicGetTutorRoles } from "../../../js/containers/preferences/containers/tutor-roles/epics/EpicGetTutorRoles";
import { GET_TUTOR_ROLES_FAILED, GET_TUTOR_ROLES_FULFILLED, getTutorRoles } from "../../../js/containers/preferences/actions";

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

  it("EpicGetTutorRoles should mark tutor roles as failed when they can not be loaded", () => {
    const response = { status: 500, data: null };
    const spy = jest.spyOn(EntityService, "getPlainRecords").mockRejectedValueOnce(response);

    return DefaultEpic({
      action: getTutorRoles(),
      epic: EpicGetTutorRoles,
      processData: () => [
        ...FetchErrorHandler(response as any, "Failed to get tutor role list"),
        { type: GET_TUTOR_ROLES_FAILED }
      ]
    }).finally(() => spy.mockRestore());
  });
});
