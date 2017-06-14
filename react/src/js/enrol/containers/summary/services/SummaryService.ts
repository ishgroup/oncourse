import {State} from "../reducers/State";
import {Enrolment} from "../../../../model/checkout/Enrolment";
import {Application} from "../../../../model/checkout/Application";
import {Membership} from "../../../../model/checkout/Membership";
import {Article} from "../../../../model/checkout/Article";
import {Voucher} from "../../../../model/checkout/Voucher";
export class SummaryService {
  /**
   * Return if state has at least one selected purchased item
   */
  static hasSelected = (state: State): boolean => {
    const items: Membership[] |  Article[] | Enrolment[] | Application[] | Voucher[] =
        [].concat(state.entities.enrolments ? Object.values(state.entities.enrolments) : [])
            .concat(state.entities.applications ? Object.values(state.entities.applications) : [])
            .concat(state.entities.memberships ? Object.values(state.entities.memberships) : [])
            .concat(state.entities.articles ? Object.values(state.entities.articles) : [])
            .concat(state.entities.vouchers ? Object.values(state.entities.vouchers) : [])
            .find((e) => (e.selected && e.errors.length === 0));
    return !!items;
  };
}