import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateMembershipProductItem } from "../../../js/containers/entities/membershipProducts/epics/EpicUpdateMembershipProductItem";
import {
  UPDATE_MEMBERSHIP_PRODUCT_ITEM_FULFILLED,
  updateMembershipProduct
} from "../../../js/containers/entities/membershipProducts/actions";

describe("Update membership product epic tests", () => {
  it("EpicUpdateMembershipProductItem should returns correct values", () => DefaultEpic({
    action: mockedApi => updateMembershipProduct("1", mockedApi.db.getMembershipProduct(1)),
    epic: EpicUpdateMembershipProductItem,
    processData: () => [
      {
        type: UPDATE_MEMBERSHIP_PRODUCT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Membership Product Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "MembershipProduct", listUpdate: true, savedID: "1" }
      }
    ]
  }));
});
