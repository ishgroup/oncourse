import { initialize } from "redux-form";
import { format } from "date-fns";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetBanking } from "../../../js/containers/entities/bankings/epics/EpicGetBanking";
import { GET_BANKING_ITEM_FULFILLED, getBanking } from "../../../js/containers/entities/bankings/actions";
import { EEE_D_MMM_YYYY } from "../../../js/common/utils/dates/format";

describe("Get banking epic tests", () => {
  it("EpicGetBanking should returns correct values", () => DefaultEpic({
    action: getBanking("1"),
    epic: EpicGetBanking,
    processData: mockedApi => {
      const banking = mockedApi.db.getBanking(1);
      return [
        {
          type: GET_BANKING_ITEM_FULFILLED,
          payload: { banking }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: {
            editRecord: banking,
            name: `${format(new Date(banking.settlementDate), EEE_D_MMM_YYYY)}${
              banking.adminSite ? " for " + banking.adminSite : ""
            }`
          }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, banking)
      ];
    }
  }));
});
