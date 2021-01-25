import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateMembershipProduct } from "../../../js/containers/entities/membershipProducts/epics/EpicCreateMembershipProduct";
import {
  CREATE_MEMBERSHIP_PRODUCT_ITEM_FULFILLED,
  createMembershipProduct
} from "../../../js/containers/entities/membershipProducts/actions";

describe("Create membership product epic tests", () => {
  it("EpicCreateMembershipProduct should returns correct values", () => DefaultEpic({
    action: mockedApi => createMembershipProduct(mockedApi.db.getMembershipProduct(1)),
    epic: EpicCreateMembershipProduct,
    processData: () => [
      {
        type: CREATE_MEMBERSHIP_PRODUCT_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Membership Product Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "MembershipProduct" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
