import { DefaultEpic } from "../common/Default.Epic";
import { EpicGetColumnsWidth } from "../../js/containers/preferences/epics/EpicGetColumnsWidth";
import { GET_COLUMNS_WIDTH_REQUEST_FULFILLED, getColumnsWidth } from "../../js/containers/preferences/actions";

describe("Get column width epic tests", () => {
  it("EpicGetColumnsWidth should returns correct values", () => DefaultEpic({
    action: getColumnsWidth(),
    epic: EpicGetColumnsWidth,
    processData: mockedApi => {
      const columnWidth = mockedApi.db.columnsSettings;
      return [
        {
          type: GET_COLUMNS_WIDTH_REQUEST_FULFILLED,
          payload: { columnWidth }
        }
      ];
    }
  }));
});
