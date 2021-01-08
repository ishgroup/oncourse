import { DefaultEpic } from "../common/Default.Epic";
import {
  SET_FAVORITE_SCRIPTS_FULFILLED,
  setFavoriteScripts
} from "../../js/containers/dashboard/actions";
import { EpicSetFavoriteScripts } from "../../js/containers/dashboard/epics/EpicSetFavoriteScripts";

describe("Set dashboard favorite scripts epic tests", () => {
  it("SetFavoriteScripts should returns correct values", () => DefaultEpic({
    action: setFavoriteScripts([]),
    epic: EpicSetFavoriteScripts,
    processData: () => [
      {
        type: SET_FAVORITE_SCRIPTS_FULFILLED
      }
    ]
  }));
});
