import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateSite } from "../../../js/containers/entities/sites/epics/EpicCreateSite";
import { CREATE_SITE_ITEM_FULFILLED, createSite } from "../../../js/containers/entities/sites/actions";

describe("Create site epic tests", () => {
  it("EpicCreateSite should returns correct values", () => DefaultEpic({
    action: mockedApi => createSite(mockedApi.db.getSite(1)),
    epic: EpicCreateSite,
    processData: () => [
      {
        type: CREATE_SITE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Site created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Site" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
