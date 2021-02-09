import { DefaultEpic } from "../../common/Default.Epic";
import { EpicGetContactRelationTypes } from "../../../js/containers/preferences/containers/contact-relation-types/epics/EpicGetContactRelationTypes";
import {
  GET_CONTACT_RELATION_TYPES_FULFILLED,
  getContactRelationTypes
} from "../../../js/containers/preferences/actions";

describe("Get contact relation types epic tests", () => {
  it("EpicGetContactRelationTypes should returns correct values", () => DefaultEpic({
    action: getContactRelationTypes(),
    epic: EpicGetContactRelationTypes,
    processData: mockedApi => {
      const contactRelationTypes = mockedApi.db.contactRelationTypes;
      return [
        {
          type: GET_CONTACT_RELATION_TYPES_FULFILLED,
          payload: { contactRelationTypes }
        }
      ];
    }
  }));
});
