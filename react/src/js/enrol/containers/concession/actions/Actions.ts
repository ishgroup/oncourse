import {IAction} from "../../../../actions/IshAction";
import {_toRequestType} from "../../../../common/actions/ActionUtils";


export const UPDATE_CONCESSION_CONTACT: string = "checkout/update/concession/contact";

export const GET_CONCESSION_TYPES_REQUEST: string = "checkout/get/concessionTypes/request";
export const ADD_CONCESSION_TYPES_TO_STATE: string = "checkout/update/concessionTypes";

export const updateConcessionContact = (id): IAction<any> => {
  return {
    type: UPDATE_CONCESSION_CONTACT,
    payload: id,
  };
};

export const getConcessionTypes = (): IAction<any> => {
  return {
    type: GET_CONCESSION_TYPES_REQUEST,
  };
};

