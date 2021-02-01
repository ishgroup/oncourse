import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetSite } from "../../../js/containers/entities/sites/epics/EpicGetSite";
import { GET_SITE_ITEM_FULFILLED, getSite } from "../../../js/containers/entities/sites/actions";
import { getNoteItems } from "../../../js/common/components/form/notes/actions";

describe("Get site epic tests", () => {
  it("EpicGetSite should returns correct values", () => DefaultEpic({
    action: getSite("1"),
    epic: EpicGetSite,
    processData: mockedApi => {
      const site = mockedApi.db.getSite(1);
      return [
        {
          type: GET_SITE_ITEM_FULFILLED
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: site, name: site.name }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, site),
        getNoteItems("Site", "1" as any, LIST_EDIT_VIEW_FORM_NAME)
      ];
    }
  }));
});
