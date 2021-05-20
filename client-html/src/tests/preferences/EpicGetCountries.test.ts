import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_COUNTRIES_REQUEST_FULFILLED,
  getCountries
} from "../../js/containers/preferences/actions";
import { EpicGetCountries } from "../../js/containers/preferences/epics/EpicGetCountries";

describe("Get countries epic tests", () => {
  it("EpicGetCountries should returns correct values", () => DefaultEpic({
    action: getCountries(),
    epic: EpicGetCountries,
    processData: mockedApi => {
      const data = mockedApi.db.countries;
      const countries = data.sort((a, b) => (a.name > b.name ? 1 : -1));

      return [
        {
          type: GET_COUNTRIES_REQUEST_FULFILLED,
          payload: { countries }
        }
      ];
    }
  }));
});
