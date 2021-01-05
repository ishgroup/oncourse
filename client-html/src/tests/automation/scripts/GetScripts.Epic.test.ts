import { CommonListItem } from "../../../js/model/common/sidebar";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_SCRIPTS_LIST_FULFILLED,
  getScriptsList
} from "../../../js/containers/automation/containers/scripts/actions";
import { EpicGetScriptsList } from "../../../js/containers/automation/containers/scripts/epics/EpicGetScriptsList";

describe("Get scripts list epic tests", () => {
  it("EpicGetScriptsList should returns correct values", () => DefaultEpic({
    action: getScriptsList(),
    epic: EpicGetScriptsList,
    processData: mockedAPI => {
      const scriptsResponse = mockedAPI.db.getPlainScripts();
      const scripts: CommonListItem[] = scriptsResponse.rows.map(r => ({
        id: Number(r.id),
        name: r.values[0],
        grayOut: r.values[1] === "false",
        keyCode: r.values[2],
        hasIcon: r.values[2] && r.values[2].startsWith("ish.")
      }));

      return [
        {
          type: GET_SCRIPTS_LIST_FULFILLED,
          payload: { scripts }
        }
      ];
    }
  }));
});
