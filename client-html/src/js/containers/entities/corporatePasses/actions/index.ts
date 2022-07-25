import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { CorporatePass } from "@api/model";

export const UPDATE_CORPORATE_PASS_ITEM = _toRequestType("put/corporatepass");
export const UPDATE_CORPORATE_PASS_ITEM_FULFILLED = FULFILLED(UPDATE_CORPORATE_PASS_ITEM);

export const updateCorporatePass = (id: string, corporatePass: CorporatePass) => ({
  type: UPDATE_CORPORATE_PASS_ITEM,
  payload: { id, corporatePass }
});