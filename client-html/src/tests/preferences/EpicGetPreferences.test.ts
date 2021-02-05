import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_PREFERENCES_FULFILLED,
  getPreferences
} from "../../js/containers/preferences/actions";
import { EpicGetPreferences } from "../../js/containers/preferences/epics/EpicGetPreferences";
import { Categories } from "../../js/model/preferences";
import * as ModelLicences from "../../js/model/preferences/Licences";

describe("Get preferences epic tests", () => {
  it("EpicGetPreferences should returns correct values", () => DefaultEpic({
    action: getPreferences(Categories.licences),
    epic: EpicGetPreferences,
    processData: mockedApi => {
      const preferences = mockedApi.db.getPreferences(Object.keys(ModelLicences).map(item => ModelLicences[item].uniqueKey));
      return [
        {
          type: GET_PREFERENCES_FULFILLED,
          payload: { preferences, category: Categories.licences }
        }
      ];
    }
  }));
});
