/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Contact, DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { getCustomColumnsMap } from "../../../../common/utils/common";
import { State } from "../../../../reducers/state";
import {CHECKOUT_GET_RELATED_CONTACT, CHECKOUT_GET_RELATED_CONTACT_FULFILLED} from "../../actions/checkoutContact";

const defaultContactMap = ({ id, values }) => ({
  id: Number(id),
  firstName: values[0],
  lastName: values[1],
  email: values[2],
  birthDate: values[3],
  relationString: ""
});

const request: EpicUtils.Request<
  DataResponse,
  State,
  { search?: string; columns?: string; ascending?: boolean; sort?: string }
  > = {
  type: CHECKOUT_GET_RELATED_CONTACT,
  hideLoadIndicator: true,
  getData: ({ search, columns, ascending, sort }) => {
    return EntityService.getPlainRecords(
      "Contact",
      columns || "firstName,lastName,email,birthDate",
      search,
      100,
      0,
      sort,
      ascending
    );
  },
  processData: (records, s, { columns }) => {
    const { rows } = records;
    const relatedContacts: Contact[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultContactMap);
    const relationTypes = [...s.contacts.contactsRelationTypes];
    return [
      {
        type: CHECKOUT_GET_RELATED_CONTACT_FULFILLED,
        payload: { relatedContacts, relationTypes }
      }
    ];
  }
};

export const EpicCheckoutGetRelatedContacts: Epic<any, any> = EpicUtils.Create(request);
