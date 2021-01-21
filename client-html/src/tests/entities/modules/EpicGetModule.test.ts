import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetModule } from "../../../js/containers/entities/modules/epics/EpicGetModule";
import { GET_MODULE_ITEM_FULFILLED, getModule } from "../../../js/containers/entities/modules/actions";

describe("Get module epic tests", () => {
  it("EpicGetModule should returns correct values", () => DefaultEpic({
    action: getModule("1"),
    epic: EpicGetModule,
    processData: mockedApi => {
      const module = mockedApi.db.getModule(1);
      return [
        {
          type: GET_MODULE_ITEM_FULFILLED,
          payload: { module }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: module, name: module.title }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, module)
      ];
    }
  }));
});
