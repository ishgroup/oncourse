import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateCorporatePass } from "../../../js/containers/entities/corporatePasses/epics/EpicUpdateCorporatePass";
import {
  GET_CORPORATE_PASS_ITEM,
  UPDATE_CORPORATE_PASS_ITEM_FULFILLED,
  updateCorporatePass
} from "../../../js/containers/entities/corporatePasses/actions";

describe("Update corporate pass epic tests", () => {
  it("EpicUpdateCorporatePass should returns correct values", () => {
    const id = "2";
    return DefaultEpic({
      action: mockedApi => updateCorporatePass(id, mockedApi.db.createNewCorporatePasses(Number(id))),
      epic: EpicUpdateCorporatePass,
      processData: () => [
        {
          type: UPDATE_CORPORATE_PASS_ITEM_FULFILLED
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "CorporatePass Record updated" }
        },
        {
          type: GET_RECORDS_REQUEST,
          payload: { entity: "CorporatePass", listUpdate: true, savedID: id }
        },
        {
          type: GET_CORPORATE_PASS_ITEM,
          payload: id
        }
      ]
    });
  });
});
