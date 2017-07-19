import {ItemToState, State} from "../../../../../../js/enrol/containers/summary/reducers/State";
import {Enrolment, Application} from "../../../../../../js/model";

test('test convertFromEnrolment ', () => {


  const value: Enrolment = Object.assign(new Enrolment(), {
    contactId: "10000",
    classId: "20000",
    selected: false,
  });

  const state: State = ItemToState(value);

  expect(state.result[0]).toBe("10000");
  expect(state.entities.enrolments['10000-20000'].classId).toBe("20000");
  expect(state.entities.contactNodes['10000'].enrolments[0]).toBe('10000-20000');
});


test('test inputToObject ', () => {

  const input = {
    contactId: "10000",
    classId: "20000",
    price: {
      fee: "200.00",
      feeOverriden: "100.00",
      appliedDiscount: {
        id: "120",
        expiryDate: "12/09/2018",
        discountedFee: "90.00",
        discountValue: "10.00",
        title: "title",
      },
      possibleDiscounts: [{
        id: "121",
        expiryDate: "12/09/2019",
        discountedFee: "90.00",
        discountValue: "10.00",
        title: "title1"
      },
        {
          id: "122",
          expiryDate: "12/09/2020",
          discountedFee: "90.00",
          discountValue: "10.00",
          title: "title2"
        }],
      hasTax: true,
    },
    warnings: [],
    errors: [],
    selected: true,
  };

  const enrolment: Enrolment = Object.assign(new Enrolment(), input);
  expect(enrolment instanceof Enrolment).toBe(true);
  expect(enrolment.classId).toBe("20000");

  const state: State = ItemToState(enrolment);

  expect(state.result[0]).toBe("10000");
  expect(state.entities.enrolments['10000-20000'].classId).toBe("20000");
  expect(state.entities.contactNodes['10000'].enrolments[0]).toBe('10000-20000');

  const inputApp = {
    contactId: "10000",
    classId: "20000",
    warnings: [],
    errors: [],
    selected: true,
  };

  const application: Application = Object.assign(new Application(), inputApp);
  const stateApp: State = ItemToState(application);

  expect(stateApp.result[0]).toBe("10000");
  expect(stateApp.entities.applications['10000-20000'].classId).toBe("20000");
  expect(stateApp.entities.contactNodes['10000'].applications[0]).toBe('10000-20000');

});
