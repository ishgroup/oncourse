import {EnrolmentToState, State} from "../../../../../../js/enrol/containers/summary/reducers/State";

test('test convertFromEnrolment ', () => {

  const state: State = EnrolmentToState(
    {
      contactId: "10000",
      classId: "20000",
      selected: false
    });

  expect(state.result[0]).toBe("10000");
  expect(state.entities.enrolments['10000-20000'].classId).toBe("20000");
  expect(state.entities.contactNodes['10000'].enrolments[0]).toBe('10000-20000');
});