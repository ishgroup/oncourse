import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_CONTACTS_CONCESSION_TYPES_FULFILLED,
  getContactsConcessionTypes
} from "../../../js/containers/entities/contacts/actions";
import { EpicGetContactsConcessionTypes } from "../../../js/containers/entities/contacts/epics/EpicGetContactsConcessionTypes";

describe("Get contact concession types epic tests", () => {
  it("EpicGetContactsConcessionTypes should returns correct values", () => DefaultEpic({
    action: getContactsConcessionTypes(),
    epic: EpicGetContactsConcessionTypes,
    processData: mockedApi => {
      const contactsConcessionTypes = mockedApi.db.concessionTypes;

      return [
        {
          type: GET_CONTACTS_CONCESSION_TYPES_FULFILLED,
          payload: { contactsConcessionTypes }
        }
      ];
    }
  }));
});
