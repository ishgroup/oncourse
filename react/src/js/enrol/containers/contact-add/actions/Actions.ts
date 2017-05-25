import {_toRejectType, _toRequestType} from "../../../../common/actions/ActionUtils";
import {normalize} from "normalizr";
import {ContactsSchema} from "../../../../NormalizeSchema";
import {NAME as ContactAddForm} from "../ContactAddForm";

export const ContactAdd: string = "checkout/contact/add";
export const ContactAddRequest: string = _toRequestType(ContactAdd);
export const ContactAddReject: string = _toRejectType(ContactAdd);

export const submitAddContact = (id: string, values: any): any => {
  const payload: any = normalize(Object.assign({}, values, {id: id}), ContactsSchema);
  return {
    type: ContactAddRequest,
    payload: payload,
    meta: {
      from: ContactAddForm
    }
  };
};
