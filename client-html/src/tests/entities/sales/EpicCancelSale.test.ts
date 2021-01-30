import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicCancelSale } from "../../../js/containers/entities/sales/epics/EpicCancelSale";
import { CANCEL_SALE_FULFILLED, cancelSale, GET_SALE } from "../../../js/containers/entities/sales/actions";

describe("Cancel sale epic tests", () => {
  it("EpicCancelSale should returns correct values", () => DefaultEpic({
    action: cancelSale({
      createCrediNote: true,
      id: 1,
      retainAdministrativeFee: false
    }),
    epic: EpicCancelSale,
    processData: () => [
      {
        type: CANCEL_SALE_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Sale Record cancelled" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "ProductItem", listUpdate: true, savedID: 1 }
      },
      {
        type: GET_SALE,
        payload: { id: 1 }
      }
    ]
  }));
});
