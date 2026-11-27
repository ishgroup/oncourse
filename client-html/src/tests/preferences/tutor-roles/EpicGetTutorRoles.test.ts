import { from, lastValueFrom } from "rxjs";
import { toArray } from "rxjs/operators";
import { DefaultEpic } from "../../common/Default.Epic";
import { store } from "../../TestEntry";
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
        { type: GET_TUTOR_ROLES_FAILED, payload: { columns: undefined } }
      ]
    }).finally(() => spy.mockRestore());
  });

  it("EpicGetTutorRoles should mark tutor roles as failed when the response can not be processed", async () => {
    const columns = "name,description,active";

    // Selecting a role which is not in the list throws while the response is processed
    const actions = await lastValueFrom(
      EpicGetTutorRoles(from([getTutorRoles(false, "Missing role", columns)]), store, {}).pipe(toArray())
    );

    expect(actions[actions.length - 2]).toEqual({ type: GET_TUTOR_ROLES_FAILED, payload: { columns } });
  });
});
