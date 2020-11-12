import { ActionsObservable } from "redux-observable";
import { filter, toArray } from "rxjs/operators";
import { store, mockedAPI } from "../TestEntry";
import { FETCH_FINISH, FETCH_START } from "../../js/common/actions";
import { GET_CURRENCY, GET_CURRENCY_FULFILLED } from "../../js/containers/preferences/actions";
import { EpicGetCurrency } from "../../js/containers/preferences/epics/EpicGetCurrency";

describe("Get currency epic tests", () => {
  it("EpicGetCurrency should return correct actions", () => {
    // Expected response
    const currency = mockedAPI.db.currency;

    // Redux action to trigger epic
    const action$ = ActionsObservable.of({ type: GET_CURRENCY });

    // Initializing epic instance
    const epic$ = EpicGetCurrency(action$, store, {});

    // Testing epic to be resolved with expected array of actions
    return expect(
      epic$
        .pipe(
          // Filtering common actions
          filter(a => ![FETCH_START, FETCH_FINISH].includes(a.type)),
          toArray()
        )
        .toPromise()
    ).resolves.toEqual([
      {
        type: GET_CURRENCY_FULFILLED,
        payload: { currency }
      }
    ]);
  });
});
