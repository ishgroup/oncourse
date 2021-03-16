/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { ContactRelationType } from "@api/model";
import { GET_CONTACTS_RELATION_TYPES, GET_CONTACTS_RELATION_TYPES_FULFILLED } from "../actions";
import ContactsService from "../services/ContactsService";

const request: EpicUtils.Request = {
  type: GET_CONTACTS_RELATION_TYPES,
  getData: () => ContactsService.getContactsRelationTypes(),
  processData: (items: ContactRelationType[]) => {
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
};

export const EpicGetContactsRelationTypes: Epic<any, any> = EpicUtils.Create(request);
