import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetConcessionTypes } from "../../../js/containers/preferences/containers/concession-types/epics/EpicGetConcessionTypes";
import { GET_CONCESSION_TYPES_FULFILLED, getConcessionTypes } from "../../../js/containers/preferences/actions";

describe("Get concession types epic tests", () => {
  it("EpicGetConcessionTypes should returns correct values", () => DefaultEpic({
    action: getConcessionTypes(),
    epic: EpicGetConcessionTypes,
    processData: mockedApi => {
      const concessionTypes = mockedApi.db.concessionTypes;
      return [
        {
          type: GET_CONCESSION_TYPES_FULFILLED,
          payload: { concessionTypes }
        }
      ];
    }
  }));
});
