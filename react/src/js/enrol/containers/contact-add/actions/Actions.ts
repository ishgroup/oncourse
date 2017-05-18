import {_toRejectType, _toRequestType} from "../../../../common/actions/ActionUtils";
import {normalize} from "normalizr";
import {contactsSchema} from "../../../../schema";
import {NAME as ContactAddForm} from "../ContactAddForm";
import {MessagesSet} from "../../../actions/Actions";

export const ContactAdd: string = "checkout/contact/add";
export const ContactAddRequest: string = _toRequestType(ContactAdd);
export const ContactAddReject: string = _toRejectType(ContactAdd);

export const ContactAddAction = (id: string, values: any): any => {
  const payload: any = normalize(Object.assign({}, values, {id: id}), contactsSchema);
  return {
    type: ContactAddRequest,
    payload: payload,
    meta: {
      from: ContactAddForm
    }
  };
};
