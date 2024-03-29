import { Column, DataRow, Script } from "@api/model";
import { DefaultEpic } from "../common/Default.Epic";
import { EpicGetDashboardScripts } from "../../js/containers/dashboard/epics/EpicGetDashboardScripts";
import { GET_ON_DEMAND_SCRIPTS_FULFILLED, getOnDemandScripts } from "../../js/common/actions";

describe("Get dashboard scripts epic tests", () => {
  it("GetDashboardScripts should returns correct values", () => DefaultEpic({
    action: getOnDemandScripts(),
    epic: EpicGetDashboardScripts,
    processData: mockedApi => {
      const records = mockedApi.db.getScripts();
      const scripts: Script[] = records.rows.map((row: DataRow) => ({
        id: Number(row.id),
        name: row.values[0],
        description: row.values[1]
      }));

      return [
        {
          type: GET_ON_DEMAND_SCRIPTS_FULFILLED,
          payload: { scripts }
        }
      ];
    }
  }));
});
