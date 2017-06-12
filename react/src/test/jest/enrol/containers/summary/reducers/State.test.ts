import {ItemToState, State} from "../../../../../../js/enrol/containers/summary/reducers/State";
import {Enrolment} from "../../../../../../js/model/checkout/Enrolment";

test('test convertFromEnrolment ', () => {


  const value: Enrolment = Object.assign(new Enrolment(), {
    contactId: "10000",
    classId: "20000",
    selected: false
  });

  const state: State = ItemToState(value);

  expect(state.result[0]).toBe("10000");
  expect(state.entities.enrolments['10000-20000'].classId).toBe("20000");
  expect(state.entities.contactNodes['10000'].enrolments[0]).toBe('10000-20000');
});