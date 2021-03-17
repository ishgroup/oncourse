import { DefaultEpic } from "../../common/Default.Epic";
import { updateAccount } from "../../../js/containers/entities/accounts/actions";
import { EpicUpdateAccountItem } from "../../../js/containers/entities/accounts/epics/EpicUpdateAccountItem";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";

describe("Update account epic tests", () => {
  it("EpicUpdateAccountItem should returns correct values", () => {
    const accountId: number = 2;
    return DefaultEpic({
      action: updateAccount("2", {
        accountCode: "accountCode 2",
        description: "description 3",
        id: accountId,
        isDefaultAccount: true,
        isEnabled: true,
        tax: null,
        type: "asset"
      }),
      epic: EpicUpdateAccountItem,
      processData: () => [
        {
          type: FETCH_SUCCESS,
          payload: { message: "Account Record updated" }
        },
        {
          type: GET_RECORDS_REQUEST,
          payload: { entity: "Account", listUpdate: true, savedID: String(accountId) }
        }
      ]
    });
  });
});
