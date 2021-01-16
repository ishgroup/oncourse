import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_CONTACTS_RELATION_TYPES_FULFILLED,
  getContactsRelationTypes
} from "../../../js/containers/entities/contacts/actions";
import { EpicGetContactsRelationTypes } from "../../../js/containers/entities/contacts/epics/EpicGetContactsRelationTypes";

describe("Get contact relation types epic tests", () => {
  it("EpicGetContactsRelationTypes should returns correct values", () => DefaultEpic({
    action: getContactsRelationTypes(),
    epic: EpicGetContactsRelationTypes,
    processData: mockedApi => {
      const items = mockedApi.db.contactRelationTypes;

      const contactsRelationTypes = items.reduce((acc, r) => {
        const { relationName, reverseRelationName, id } = r;

        acc.push({ label: relationName, value: id, isReverseRelation: false });
        acc.push({ label: reverseRelationName, value: id, isReverseRelation: true });

        return acc;
      }, []);

      return [
        {
          type: GET_CONTACTS_RELATION_TYPES_FULFILLED,
          payload: { contactsRelationTypes }
        }
      ];
    }
  }));
});
