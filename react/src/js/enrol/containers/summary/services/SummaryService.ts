import {State} from "../reducers/State";
import {Enrolment} from "../../../../model/checkout/Enrolment";
import {Application} from "../../../../model/checkout/Application";
export class SummaryService {
  /**
   * Return if state has at least one selected purchased item
   */
  static hasSelected = (state: State): boolean => {
  
    let enrlSelected: boolean = false;
    let appSelected: boolean  = false;

    if (state.entities.enrolments) {
        const enrolment: Enrolment = Object.keys(state.entities.enrolments).map((key) => state.entities.enrolments[key])
            .find((e) => (e.selected && e.errors.length === 0));
      enrlSelected = !!enrolment;
    }
    
    if (state.entities.applications) {
      const application: Application = Object.keys(state.entities.applications).map((key) => state.entities.applications[key])
          .find((e) => (e.selected && e.errors.length === 0));
      appSelected = !!application;
    }
    
    return appSelected || enrlSelected
  
  };
}