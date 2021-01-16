import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_CONTACTS_TAX_TYPES_FULFILLED,
  getContactsTaxTypes
} from "../../../js/containers/entities/contacts/actions";
import { EpicGetContactsTaxTypes } from "../../../js/containers/entities/contacts/epics/EpicGetContactsTaxTypes";

describe("Get contact tax types epic tests", () => {
  it("EpicGetContactsTaxTypes should returns correct values", () => DefaultEpic({
    action: getContactsTaxTypes(),
    epic: EpicGetContactsTaxTypes,
    processData: mockedApi => {
      const taxTypes = mockedApi.db.taxTypes;

      return [
        {
          type: GET_CONTACTS_TAX_TYPES_FULFILLED,
          payload: { taxTypes: taxTypes.filter(t => t.code !== "*") }
        }
      ];
    }
  }));
});
