import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_SCRIPTS_LIST_FULFILLED,
  getScriptsList
} from "../../../js/containers/automation/containers/scripts/actions";
import { EpicGetScriptsList } from "../../../js/containers/automation/containers/scripts/epics/EpicGetScriptsList";
import { CatalogItemType } from "../../../js/model/common/Catalog";
import { mapListToCatalogItem } from "../../../js/common/utils/Catalog";

describe("Get scripts list epic tests", () => {
  it("EpicGetScriptsList should returns correct values", () => DefaultEpic({
    action: getScriptsList(),
    epic: EpicGetScriptsList,
    processData: mockedAPI => {
      const scriptsResponse = mockedAPI.db.getPlainScripts();
      const scripts: CatalogItemType[] = scriptsResponse.rows.map(mapListToCatalogItem);

      scripts.sort((a, b) => (a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1));

      return [
        {
          type: GET_SCRIPTS_LIST_FULFILLED,
          payload: { scripts }
        }
      ];
    }
  }));
});
