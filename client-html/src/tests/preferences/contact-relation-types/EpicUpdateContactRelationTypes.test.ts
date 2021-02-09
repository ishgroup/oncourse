import { DefaultEpic } from "../../common/Default.Epic";
import {
  UPDATE_CONTACT_RELATION_TYPES_FULFILLED,
  updateContactRelationTypes
} from "../../../js/containers/preferences/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicUpdateContactRelationTypes } from "../../../js/containers/preferences/containers/contact-relation-types/epics/EpicUpdateContactRelationTypes";

describe("Update contact relation type epic tests", () => {
  it("EpicUpdateContactRelationTypes should returns correct values", () => DefaultEpic({
    action: mockedApi => updateContactRelationTypes(mockedApi.db.contactRelationTypes),
    epic: EpicUpdateContactRelationTypes,
    processData: mockedApi => {
      const contactRelationTypes = mockedApi.db.contactRelationTypes;
      return [
        {
          type: UPDATE_CONTACT_RELATION_TYPES_FULFILLED,
          payload: { contactRelationTypes }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Contact Relation Types were successfully updated" }
        }
      ];
    }
  }));
});
