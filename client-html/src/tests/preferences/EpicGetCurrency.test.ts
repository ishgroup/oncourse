import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_CURRENCY_FULFILLED,
  getCurrency
} from "../../js/containers/preferences/actions";
import { EpicGetCurrency } from "../../js/containers/preferences/epics/EpicGetCurrency";

describe("Get currency epic tests", () => {
  it("EpicGetCurrency should returns correct values", () => DefaultEpic({
    action: getCurrency(),
    epic: EpicGetCurrency,
    processData: mockedApi => {
      const currency = mockedApi.db.currency;
      return [
        {
          type: GET_CURRENCY_FULFILLED,
          payload: { currency }
        }
      ];
    }
  }));
});
