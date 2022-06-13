
import { from, filter, toArray } from "rxjs";
import { store, mockedAPI } from "../../TestEntry";
import { FETCH_FINISH, FETCH_START } from "../../../js/common/actions";
import {
  GET_OVERLAY_ITEMS,
  GET_PDF_REPORTS,
  GET_PDF_REPORTS_FULFILLED
} from "../../../js/common/components/list-view/components/share/actions";
import { EpicGetShareList } from "../../../js/common/components/list-view/components/share/epics/EpicGetShareList";

export const GetListExportPdfTemplate = (entityName: string) => {
  // Expected response
  const data = mockedAPI.db.getPdfTemplate(entityName);

  // Redux action to trigger epic
  const action$ = from([{ type: GET_PDF_REPORTS, payload: entityName }]);

  // Initializing epic instance
  const epic$ = EpicGetShareList(action$, store, {});

  // Testing epic to be resolved with expected array of actions
  return expect(
    epic$
      // Filtering common actions
      .pipe(
        // Filtering common actions
        filter(a => [FETCH_START, FETCH_FINISH].includes(a.type) === false),
        toArray()
      )
      .toPromise()
  ).resolves.toEqual([
    {
      type: GET_PDF_REPORTS_FULFILLED,
      payload: { pdfReports: data }
    },
    {
      type: GET_OVERLAY_ITEMS
    }
  ]);
};
