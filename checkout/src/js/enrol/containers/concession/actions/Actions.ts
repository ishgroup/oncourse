import {IAction} from "../../../../actions/IshAction";

export const UPDATE_CONTACT_CONCESSION: string = "checkout/update/concession/contact";

export const GET_CONCESSION_TYPES_REQUEST: string = "checkout/get/concessionTypes/request";

export const GET_CONTACT_CONCESSIONS_AND_MEMBERSHIPS_REQUEST: string = "checkout/summary/get/contactConcessions/request";

export const updateConcessionContact = (id): IAction<any> => {
  return {
    type: UPDATE_CONTACT_CONCESSION,
    payload: id,
  };
};

export const getConcessionTypes = (): IAction<any> => {
  return {
    type: GET_CONCESSION_TYPES_REQUEST,
  };
};

export const getContactConcessions = () => {
  return {
    type: GET_CONTACT_CONCESSIONS_AND_MEMBERSHIPS_REQUEST,
  };
};

