import { DefaultEpic } from "../../common/Default.Epic";
import {
  DELETE_CONCESSION_TYPE_FULFILLED,
  deleteConcessionType
} from "../../../js/containers/preferences/actions";
import { EpicDeleteConcessionType } from "../../../js/containers/preferences/containers/concession-types/epics/EpicDeleteConcessionType";
import { FETCH_SUCCESS } from "../../../js/common/actions";

describe("Delete concession type epic tests", () => {
  it("EpicDeleteConcessionType should returns correct values", () => DefaultEpic({
    action: deleteConcessionType("1"),
    epic: EpicDeleteConcessionType,
    processData: mockedApi => {
      const concessionTypes = mockedApi.db.concessionTypes;
      return [
        {
          type: DELETE_CONCESSION_TYPE_FULFILLED,
          payload: { concessionTypes }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Concession Type was successfully deleted" }
        }
      ];
    }
  }));
});
