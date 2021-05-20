import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_TIMEZONES_FULFILLED,
  getTimezones
} from "../../js/containers/preferences/actions";
import { EpicGetTimezones } from "../../js/containers/preferences/epics/EpicGetTimezones";
import { SelectItemDefault } from "../../js/model/entities/common";

describe("Get timezones epic tests", () => {
  it("EpicGetTimezones should returns correct values", () => DefaultEpic({
    action: getTimezones(),
    epic: EpicGetTimezones,
    processData: mockedApi => {
      const data = mockedApi.db.timezones;
      const timezones: SelectItemDefault[] = data.map(t => ({ value: t, label: t }));
      return [
        {
          type: GET_TIMEZONES_FULFILLED,
          payload: { timezones }
        }
      ];
    }
  }));
});
