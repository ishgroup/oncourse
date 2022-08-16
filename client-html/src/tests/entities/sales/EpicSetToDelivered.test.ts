import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicSetToDelivered } from "../../../js/containers/entities/sales/epics/EpicSetToDelivered";
import { getEntityRecord } from "../../../js/containers/entities/common/actions";
import { setSaleDelivered } from "../../../js/containers/entities/sales/actions";

describe("Update sale to set delivered epic tests", () => {
  it("EpicSetToDelivered should returns correct values", () => DefaultEpic({
    action: setSaleDelivered(1),
    epic: EpicSetToDelivered,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Sale Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "ProductItem", listUpdate: true, savedID: 1 }
      },
      getEntityRecord(1, "ProductItem")
    ]
  }));
});