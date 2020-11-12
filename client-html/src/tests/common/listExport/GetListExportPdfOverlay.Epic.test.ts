import { ActionsObservable } from "redux-observable";
import { store, mockedAPI } from "../../TestEntry";
import { FETCH_FINISH, FETCH_START } from "../../../js/common/actions";
import {
  GET_OVERLAY_ITEMS,
  GET_OVERLAY_ITEMS_FULFILLED
} from "../../../js/common/components/list-view/components/share/actions";
import { EpicGetOverlays } from "../../../js/common/components/list-view/components/share/epics/EpicGetOverlays";
import { filter, toArray } from "rxjs/operators";

describe("Get list export pdf overlay epic tests", () => {
  it("GetListExportPdfOverlay should returns correct actions", () => {
    // Expected response
    const overlays = mockedAPI.db.getPlainReportOverlays().rows.map(r => ({
      id: Number(r.id),
      name: r.values[0]
    }));

    // Redux action to trigger epic
    const action$ = ActionsObservable.of({ type: GET_OVERLAY_ITEMS });

    // Initializing epic instance
    const epic$ = EpicGetOverlays(action$, store, {});

    // Testing epic to be resolved with expected array of actions
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
        type: GET_OVERLAY_ITEMS_FULFILLED,
        payload: {
          overlays
        }
      },
      ...[]
    ]);
  });
});
