import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetCorporatePass } from "../../../js/containers/entities/corporatePasses/epics/EpicGetCorporatePass";
import {
  GET_CORPORATE_PASS_ITEM_FULFILLED,
  getCorporatePass
} from "../../../js/containers/entities/corporatePasses/actions";
import { corporatePassNameCondition } from "../../../js/containers/entities/corporatePasses/CorporatePasses";

describe("Get corporate pass epic tests", () => {
  it("EpicGetCorporatePass should returns correct values", () => DefaultEpic({
    action: getCorporatePass("1"),
    epic: EpicGetCorporatePass,
    processData: mockedApi => {
      const corporatePass = mockedApi.db.getCorporatePass(1);
      return [
        {
          type: GET_CORPORATE_PASS_ITEM_FULFILLED,
          payload: { corporatePass }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: corporatePass, name: corporatePassNameCondition(corporatePass) }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, corporatePass)
      ];
    }
  }));
});
