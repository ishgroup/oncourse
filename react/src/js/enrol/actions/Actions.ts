import {_toRejectType, _toRequestType} from "../../common/actions/ActionUtils";

//initialize enrol application
const Init:string = "enrol/init";
export const InitRequest:string = _toRequestType(Init);
export const InitReject:string = _toRejectType(Init);

//change current phase action
export const PhaseChange:string = "enrol/phase/change";

export const ContactAdd:string = "enrol/contact/add";
export const ContactAddRequest:string = _toRequestType(ContactAdd);
export const ContactAddReject:string = _toRejectType(ContactAdd);


export const PayerSet:string = "enrol/payer/set";
export const PayerSetRequest:string = _toRequestType(PayerSet);
export const PayerSetReject:string = _toRequestType(PayerSet);

