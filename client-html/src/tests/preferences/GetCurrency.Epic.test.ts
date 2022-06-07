import { TestScheduler } from 'rxjs/testing';
import { filter, toArray } from "rxjs/operators";
import { store, mockedAPI } from "../TestEntry";
import { FETCH_FINISH, FETCH_START } from "../../js/common/actions";
import { GET_CURRENCY, GET_CURRENCY_FULFILLED } from "../../js/containers/preferences/actions";
import { EpicGetCurrency } from "../../js/containers/preferences/epics/EpicGetCurrency";

it("EpicGetCurrency should return correct actions", () => {
  const testScheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  });

  testScheduler.run(({ hot, expectObservable }) => {
    const action$ = hot('-a', {
      a: { type: GET_CURRENCY }
    });

    const output$ = EpicGetCurrency(action$, store, null);

    const currency = mockedAPI.db.currency;

    expectObservable(output$).toBe('0-1|', [
      {
        type: FETCH_START,
        payload: { hideIndicator: false }
      },
      {
        type: GET_CURRENCY_FULFILLED,
        payload: { currency }
      },
      {
        type: FETCH_FINISH
      }
    ]);
  });
});
