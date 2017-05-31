import {State} from "../reducers/State";
import {Enrolment} from "../../../../model/checkout/Enrolment";
export class SummaryService {
  /**
   * Return if state has at least one selected purchased item
   */
  static hasSelected = (state: State): boolean => {
    const enrolment: Enrolment = Object.keys(state.entities.enrolments).map((key) => state.entities.enrolments[key])
      .find((e) => (e.selected && e.errors.length === 0));
    return !!enrolment;
  };
}