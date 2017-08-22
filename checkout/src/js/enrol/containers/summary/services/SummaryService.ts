import {State} from "../reducers/State";
import {PurchaseItem} from "../../../../model";
export class SummaryService {
  /**
   * Return if state has at least one selected purchased item
   */
  static hasSelected = (state: State): boolean => {

    const enrolmentsIds = [].concat(...state.result.map(id => state.entities.contactNodes[id].enrolments));
    const voucherIds = [].concat(...state.result.map(id => state.entities.contactNodes[id].vouchers));
    const applicationsIds = [].concat(...state.result.map(id => state.entities.contactNodes[id].applications));
    const membershipsIds = [].concat(...state.result.map(id => state.entities.contactNodes[id].memberships));
    const articlesIds = [].concat(...state.result.map(id => state.entities.contactNodes[id].articles));

    const items: PurchaseItem[] =
      [].concat(state.entities.enrolments ? enrolmentsIds.map(id => state.entities.enrolments[id]) : [])
        .concat(state.entities.vouchers ? voucherIds.map(id => state.entities.vouchers[id]) : [])
        .concat(state.entities.applications ? applicationsIds.map(id => state.entities.applications[id]) : [])
        .concat(state.entities.memberships ? membershipsIds.map(id => state.entities.memberships[id]) : [])
        .concat(state.entities.articles ? articlesIds.map(id => state.entities.articles[id]) : [])
        .find(e => (e.selected && e.errors.length === 0));

    return !!items;
  }
}
