import { DefaultEpic } from "../common/Default.Epic";
import {
  UPDATE_COLUMNS_WIDTH_REQUEST_FULFILLED,
  updateColumnsWidth
} from "../../js/containers/preferences/actions";
import { EpicUpdateColumnsWidth } from "../../js/containers/preferences/epics/EpicUpdateColumnsWidth";

describe("Update columns width epic tests", () => {
  it("EpicUpdateColumnsWidth should returns correct values", () => DefaultEpic({
    action: updateColumnsWidth({ preferenceLeftColumnWidth: 241 }),
    epic: EpicUpdateColumnsWidth,
    processData: () => [
      {
        type: UPDATE_COLUMNS_WIDTH_REQUEST_FULFILLED
      }
    ]
  }));
});
