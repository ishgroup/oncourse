import {ContactNode} from "../../model/checkout/ContactNode";
import {Enrolment} from "../../model/checkout/Enrolment";
import {Application} from "../../model/checkout/Application";
import {Membership} from "../../model/checkout/Membership";
import {Article} from "../../model/checkout/Article";
import {Voucher} from "../../model/checkout/Voucher";
export class ContactNodeService {
    static getPurchaseItem = (data: ContactNode):  Enrolment | Application | Membership | Article | Voucher => {
        return data.enrolments[0] ? Object.assign(new Enrolment(), data.enrolments[0]) 
            :  data.applications[0] ?  Object.assign(new Application(), data.applications[0])
            :  data.vouchers[0] ? Object.assign(new Voucher(), data.vouchers[0]) 
            :  data.memberships[0] ? Object.assign(new Membership(), data.memberships[0])
            :  data.articles[0] ? Object.assign(new Article(), data.articles[0])
            : null                
    };
}