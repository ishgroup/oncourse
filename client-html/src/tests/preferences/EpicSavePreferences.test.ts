import { DefaultEpic } from "../common/Default.Epic";
import {
  SAVE_PREFERENCES_FULFILLED,
  savePreferences
} from "../../js/containers/preferences/actions";
import { Categories } from "../../js/model/preferences";
import * as ModelLicences from "../../js/model/preferences/Licences";
import { EpicSavePreferences } from "../../js/containers/preferences/epics/EpicSavePreferences";
import { FETCH_SUCCESS } from "../../js/common/actions";

describe("Save preferences epic tests", () => {
  it("EpicSavePreferences should returns correct values", () => DefaultEpic({
    action: savePreferences(Categories.licences, { "license.accesscontrol": true, "license.budget": true }),
    epic: EpicSavePreferences,
    processData: mockedApi => {
      const preferences = mockedApi.db.getPreferences(Object.keys(ModelLicences).map(item => ModelLicences[item].uniqueKey));
      return [
        {
          type: SAVE_PREFERENCES_FULFILLED,
          payload: { preferences, category: Categories.licences }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Form was successfully saved" }
        }
      ];
    }
  }));
});
