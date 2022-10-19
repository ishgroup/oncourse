import { GET_AUDIT_ITEM_FULFILLED, GET_AUDIT_ITEM_REQUEST } from "../../js/containers/audits/actions";
import { SET_LIST_EDIT_RECORD } from "../../js/common/components/list-view/actions";
import { EEE_D_MMM_YYYY } from "../../js/common/utils/dates/format";
import { store, mockedAPI } from "../TestEntry";
import { format } from "date-fns";
import { initialize } from "redux-form";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../js/common/components/list-view/constants";
import { FETCH_FINISH, FETCH_START } from "../../js/common/actions";
import { EpicGetAudit } from "../../js/containers/audits/epics/EpicGetAudit";
import { toArray, filter } from "rxjs/operators";
import { from } from "rxjs";

describe("Audit Epics tests", () => {
  test("EpicGetAudit should return correct actions", () => {
    // Expected response
    const item = mockedAPI.db.getAudit(1);

    // Redux action to trigger epic
    const action$ = from([{ type: GET_AUDIT_ITEM_REQUEST, payload: 1 }]);

    // Initializing epic instance
    const epic$ = EpicGetAudit(action$, store, {});

    // Testing epic to be resolved with expected array of actions
    return expect(
      epic$
        .pipe(
          // Filtering common actions
          filter(a => a.type !== FETCH_START && a.type !== FETCH_FINISH),
          toArray()
        )
        .toPromise()
    ).resolves.toEqual([
      {
        type: GET_AUDIT_ITEM_FULFILLED
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: {
          editRecord: item,
          name: `${item.entityIdentifier} ${item.action} ${format(new Date(item.created), EEE_D_MMM_YYYY)}`
        }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, item)
    ]);
  });
});
