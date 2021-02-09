import { DefaultEpic } from "../../common/Default.Epic";
import {
  DELETE_CONTACT_RELATION_TYPE_FULFILLED,
  deleteContactRelationType
} from "../../../js/containers/preferences/actions";
import { EpicDeleteContactRelationType } from "../../../js/containers/preferences/containers/contact-relation-types/epics/EpicDeleteContactRelationType";
import { FETCH_SUCCESS } from "../../../js/common/actions";

describe("Delete contact relation type epic tests", () => {
  it("EpicDeleteContactRelationType should returns correct values", () => DefaultEpic({
    action: deleteContactRelationType("886543"),
    epic: EpicDeleteContactRelationType,
    processData: mockedApi => {
      const contactRelationTypes = mockedApi.db.contactRelationTypes.filter(it => Number(it.id) !== Number("886543"));
      return [
        {
          type: DELETE_CONTACT_RELATION_TYPE_FULFILLED,
          payload: { contactRelationTypes }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Contact Relation Type was successfully deleted" }
        }
      ];
    }
  }));
});
