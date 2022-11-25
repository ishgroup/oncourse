import { mockedAPI } from "../../TestEntry";
import {
  getRecords,
  setListSelection,
  GET_RECORDS_FULFILLED
} from "../../../js/common/components/list-view/actions";
import { EpicGetEntities } from "../../../js/common/components/list-view/epics/EpicGetEntities";
import { DefaultEpic } from "../../common/Default.Epic";

const payload = {
  entity: "Account",
  listUpdate: false,
  savedID: null,
  ignoreSelection: null,
  viewAll: null,
  stopIndex: null,
  resolve: null
};

describe("Get account entities epic tests", () => {
  it("GetAccountEntities should returns correct actions", () => DefaultEpic({
    action: getRecords(payload),
    epic: EpicGetEntities,
    processData: (mockedApi, state) => {
      const {
        listUpdate, savedID, ignoreSelection, resolve
      } = payload;

      if (resolve) {
        resolve();
      }

      const records = mockedAPI.db.getAccountList();

      return [
        {
          type: GET_RECORDS_FULFILLED,
          payload: {
            records,
            payload,
            searchQuery: {
              filter: "",
              offset: 0,
              pageSize: 50,
              search: "",
              tagGroups: [],
              uncheckedChecklists: [],
            }
          }
        },
        ...(!ignoreSelection && !listUpdate && state.list.selection[0] !== "NEW"
          ? savedID && records.rows.find(r => String(r.id) === String(savedID))
            ? [setListSelection([String(savedID)])]
            : []
          : [])
      ];
    }
  }));
});