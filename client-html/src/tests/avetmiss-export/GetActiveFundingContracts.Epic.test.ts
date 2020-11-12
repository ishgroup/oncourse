import { ActionsObservable } from "redux-observable";
import { store, mockedAPI } from "../TestEntry";
import { FETCH_START, FETCH_FINISH } from "../../js/common/actions";
import {
  GET_ACTIVE_FUNDING_CONTRACTS_REQUEST,
  GET_ACTIVE_FUNDING_CONTRACTS_FULFILLED
} from "../../js/containers/avetmiss-export/actions";
import { EpicGetActiveFundingContracts } from "../../js/containers/avetmiss-export/epics/EpicGetActiveFundingContracts";
import { filter, toArray } from "rxjs/operators";

describe("Get active funding contracts epics tests", () => {
  it("GetActiveFundingContracts should return correct actions", () => {
    // Expected response
    const response = mockedAPI.db.getAvetmissExportPlainList();

    // Redux action to trigger epic
    const action$ = ActionsObservable.of({ type: GET_ACTIVE_FUNDING_CONTRACTS_REQUEST });

    // Initializing epic instance
    const epic$ = EpicGetActiveFundingContracts(action$, store, {});

    const contracts: any[] = response.rows.map(({ id, values }) => ({
      id: Number(id),
      name: values[0],
      flavour: values[1]
    }));

    contracts.sort((a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1));

    // Testing epic to be resolved with array of actions
    return expect(
      epic$
        .pipe(
          // Filtering common actions
          filter(a => [FETCH_START, FETCH_FINISH].includes(a.type) === false),
          toArray()
        )
        .toPromise()
    ).resolves.toEqual([
      {
        type: GET_ACTIVE_FUNDING_CONTRACTS_FULFILLED,
        payload: { contracts }
      }
    ]);
  });
});
