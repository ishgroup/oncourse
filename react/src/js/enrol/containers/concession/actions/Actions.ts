import {IAction} from "../../../../actions/IshAction";

export const UPDATE_CONCESSION_CONTACT: string = "checkout/update/concession/contact";

export const updateConcessionContact = (id): IAction<any> => {
  return {
    type: UPDATE_CONCESSION_CONTACT,
    payload: id,
  };
};
