import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateSite } from "../../../js/containers/entities/sites/epics/EpicUpdateSite";
import { UPDATE_SITE_ITEM_FULFILLED, updateSite } from "../../../js/containers/entities/sites/actions";

describe("Update site epic tests", () => {
  it("EpicUpdateSite should returns correct values", () => DefaultEpic({
    action: mockedApi => updateSite("1", mockedApi.db.getSite(1)),
    epic: EpicUpdateSite,
    processData: () => [
      {
        type: UPDATE_SITE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Site was updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Site", listUpdate: true, savedID: "1" }
      }
    ]
  }));
});
