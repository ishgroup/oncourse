import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_FAVORITE_SCRIPTS_FULFILLED,
  getFavoriteScripts
} from "../../js/containers/dashboard/actions";
import { EpicGetFavoriteScripts } from "../../js/containers/dashboard/epics/EpicGetFavoriteScripts";

describe("Get dashboard favorite scripts epic tests", () => {
  it("GetFavoriteScripts should returns correct values", () => DefaultEpic({
    action: getFavoriteScripts(),
    epic: EpicGetFavoriteScripts,
    processData: () => [
      {
        type: GET_FAVORITE_SCRIPTS_FULFILLED,
        payload: { favoriteScripts: [] }
      }
    ]
  }));
});
