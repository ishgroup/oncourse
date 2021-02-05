import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_LANGUAGES_REQUEST_FULFILLED,
  getLanguages
} from "../../js/containers/preferences/actions";
import { EpicGetLanguages } from "../../js/containers/preferences/epics/EpicGetLanguages";

describe("Get languages epic tests", () => {
  it("EpicGetLanguages should returns correct values", () => DefaultEpic({
    action: getLanguages(),
    epic: EpicGetLanguages,
    processData: mockedApi => {
      const data = mockedApi.db.getLanguages();
      const languages = data.sort((a, b) => (a.name[0] > b.name[0] ? 1 : -1));

      return [
        {
          type: GET_LANGUAGES_REQUEST_FULFILLED,
          payload: { languages }
        }
      ];
    }
  }));
});
