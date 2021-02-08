import { DefaultEpic } from "../../common/Default.Epic";
import {
  UPDATE_CONCESSION_TYPES_FULFILLED, updateConcessionTypes
} from "../../../js/containers/preferences/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicUpdateConcessionTypes } from "../../../js/containers/preferences/containers/concession-types/epics/EpicUpdateConcessionTypes";

const concessionTypes = [{
  id: 886543,
  name: "Seniors card",
  requireExpary: false,
  requireNumber: true,
  allowOnWeb: true
}];

describe("Update concession type epic tests", () => {
  it("EpicUpdateConcessionTypes should returns correct values", () => DefaultEpic({
    action: updateConcessionTypes(concessionTypes),
    epic: EpicUpdateConcessionTypes,
    processData: () => [
      {
        type: UPDATE_CONCESSION_TYPES_FULFILLED,
        payload: { concessionTypes }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Concession Types were successfully updated" }
      }
    ]
  }));
});
