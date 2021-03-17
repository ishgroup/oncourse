import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateBanking } from "../../../js/containers/entities/bankings/epics/EpicUpdateBanking";
import {
  UPDATE_BANKING_ITEM_FULFILLED,
  updateBanking
} from "../../../js/containers/entities/bankings/actions";

describe("Update banking epic tests", () => {
  it("EpicUpdateBanking should returns correct values", () => DefaultEpic({
    action: updateBanking("2", {
      id: 2,
      reconciledStatus: "N/A",
      settlementDate: "2016-06-28",
      adminSite: "online",
      administrationCenterId: 200,
      createdBy: "onCourse Administrator",
      total: 40,
      payments: [
        {
          amount: 20,
          contactId: 3326,
          contactName: "James, Parent",
          created: "2018-08-21",
          id: "p4108",
          paymentDate: "2018-08-21",
          paymentId: 4108,
          paymentMethodName: "Credit card",
          paymentTypeName: "payment in",
          reconcilable: true,
          reconciled: true,
          source: "web",
          status: "Success"
        },
        {
          amount: 500,
          contactId: 3318,
          contactName: "Miller, James",
          created: "2018-08-17",
          id: "p4068",
          paymentDate: "2018-08-17",
          paymentId: 4068,
          paymentMethodName: "Credit card",
          paymentTypeName: "payment in",
          reconcilable: true,
          reconciled: false,
          source: "web",
          status: "Success"
        }
      ]
    }),
    epic: EpicUpdateBanking,
    processData: () => [
      {
        type: UPDATE_BANKING_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Banking Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Banking", listUpdate: true, savedID: "2" }
      }
    ]
  }));
});
