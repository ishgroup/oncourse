import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_BANKING_ITEM,
  POST_RECONCILE_BANKING_FULFILLED,
  reconcileBanking
} from "../../../js/containers/entities/bankings/actions";
import { EpicReconcileBanking } from "../../../js/containers/entities/bankings/epics/EpicReconcileBanking";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";

describe("Get reconcile banking epic tests", () => {
  it("EpicReconcileBanking should returns correct values", () => DefaultEpic({
    action: reconcileBanking([341]),
    epic: EpicReconcileBanking,
    processData: () => {
      const ids = [341];
      return [
        {
          type: POST_RECONCILE_BANKING_FULFILLED
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: `Reconcile banking deposit${ids.length > 1 ? "s" : ""} request successfully handled` }
        },
        {
          type: GET_RECORDS_REQUEST,
          payload: { entity: "Banking", listUpdate: true }
        },
        {
          type: GET_BANKING_ITEM,
          payload: ids[0]
        }
      ];
    }
  }));
});
