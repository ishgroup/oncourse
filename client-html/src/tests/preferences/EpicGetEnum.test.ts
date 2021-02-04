import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_ENUM_FULFILLED,
  getEnum
} from "../../js/containers/preferences/actions";
import { EpicGetEnum } from "../../js/containers/preferences/epics/EpicGetEnum";
import { sortDefaultSelectItems } from "../../js/common/utils/common";

const enumName = "ExportJurisdiction";

describe("Get enum epic tests", () => {
  it("EpicGetEnum should returns correct values", () => DefaultEpic({
    action: getEnum(enumName),
    epic: EpicGetEnum,
    processData: mockedApi => {
      const enums = mockedApi.db.getPreferencesEnumByName(enumName);
      enums.sort(sortDefaultSelectItems);
      return [
        {
          type: GET_ENUM_FULFILLED,
          payload: { enums, type: enumName }
        }
      ];
    }
  }));
});
