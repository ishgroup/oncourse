/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  ConcessionType,
  Contact,
  ContactRelationType,
  Tax,
  UsiVerificationResult
} from "@api/model";
import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_CONTACT_EDUCATION, CLEAR_CONTACTS,
  CLEAR_USI_VERIFICATION_RESULT,
  CLOSE_MERGE_CONTACTS_SUCCESS,
  GET_CONTACT_CERTIFICATES_FULFILLED,
  GET_CONTACT_ENROLMENTS_FULFILLED,
  GET_CONTACT_OUTCOMES_FULFILLED,
  GET_CONTACT_PRIOR_LEARNINGS_FULFILLED,
  GET_CONTACTS,
  GET_CONTACTS_CONCESSION_TYPES_FULFILLED,
  GET_CONTACTS_FULFILLED,
  GET_CONTACTS_RELATION_TYPES_FULFILLED, GET_CONTACTS_STORED_CC_FULFILLED,
  GET_CONTACTS_TAX_TYPES_FULFILLED,
  POST_MERGE_CONTACTS_FULFILLED,
  SET_CONTACTS_SEARCH,
  VERIFY_USI,
  VERIFY_USI_FULFILLED,
  VERIFY_USI_REJECTED
} from "../actions";
import { PlainEntityState } from "../../../../model/common/Plain";

export interface ContactsState extends PlainEntityState<Contact> {
  education: any;
  usiVerificationResult: UsiVerificationResult;
  verifyingUSI: boolean;
  mergeContactsSuccessOpen: boolean;
  contactsRelationTypes: ContactRelationType[];
  contactsConcessionTypes: ConcessionType[];
  taxTypes: Tax[];
  storedCard?: {
    creditCardNumber: string;
    creditCardType: string;
    created: string;
  }
}

const initial: ContactsState = {
  taxTypes: [],
  items: [],
  search: "",
  loading: false,
  rowsCount: 10000,
  mergeContactsSuccessOpen: false,
  contactsRelationTypes: null,
  contactsConcessionTypes: null,
  verifyingUSI: false,
  usiVerificationResult: null,
  education: {}
};

export const contactsReducer = (state: ContactsState = initial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_CONTACTS: {
      return {
        ...state,
        loading: true
      };
    }

    case CLEAR_CONTACTS: {
      return {
        ...state,
        items: [],
        loading: false
      };
    }

    case GET_CONTACTS_FULFILLED: {
      const { items, offset, pageSize } = action.payload;

      const updated = offset ? state.items.concat(items) : items;

      return {
        ...state,
        loading: false,
        items: updated,
        rowsCount: pageSize < 100 ? pageSize : 5000
      };
    }

    case VERIFY_USI: {
      return {
        ...state,
        verifyingUSI: true
      };
    }

    case VERIFY_USI_FULFILLED: {
      const { usiVerificationResult } = action.payload;

      return {
        ...state,
        verifyingUSI: false,
        usiVerificationResult
      };
    }

    case VERIFY_USI_REJECTED: {
      return {
        ...state,
        verifyingUSI: false
      };
    }

    case GET_CONTACTS_RELATION_TYPES_FULFILLED: {
      const { contactsRelationTypes } = action.payload;

      return {
        ...state,
        contactsRelationTypes
      };
    }

    case CLEAR_USI_VERIFICATION_RESULT: {
      return {
        ...state,
        usiVerificationResult: null
      };
    }

    case CLEAR_CONTACT_EDUCATION: {
      return {
        ...state,
        education: null
      };
    }

    case GET_CONTACT_ENROLMENTS_FULFILLED: {
      const { enrolments } = action.payload;
      const education = { ...state.education, enrolments };

      return {
        ...state,
        education
      };
    }

    case GET_CONTACT_PRIOR_LEARNINGS_FULFILLED: {
      const { priorLearnings } = action.payload;
      const education = { ...state.education, priorLearnings };

      return {
        ...state,
        education
      };
    }

    case GET_CONTACT_OUTCOMES_FULFILLED: {
      const { outcomes } = action.payload;
      const education = { ...state.education, outcomes };

      return {
        ...state,
        education
      };
    }

    case GET_CONTACT_CERTIFICATES_FULFILLED: {
      const { certificates } = action.payload;
      const education = { ...state.education, certificates };

      return {
        ...state,
        education
      };
    }

    case GET_CONTACTS_CONCESSION_TYPES_FULFILLED: {
      const { contactsConcessionTypes } = action.payload;

      return {
        ...state,
        contactsConcessionTypes
      };
    }

    case GET_CONTACTS_STORED_CC_FULFILLED: {
      const { storedCard } = action.payload;

      return {
        ...state,
        storedCard
      };
    }

    case GET_CONTACTS_TAX_TYPES_FULFILLED: {
      const { taxTypes } = action.payload;

      return {
        ...state,
        taxTypes
      };
    }

    case SET_CONTACTS_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case POST_MERGE_CONTACTS_FULFILLED: {
      return {
        ...state,
        mergeContactsSuccessOpen: true
      };
    }

    case CLOSE_MERGE_CONTACTS_SUCCESS: {
      return {
        ...state,
        mergeContactsSuccessOpen: false
      };
    }

    default:
      return state;
  }
};
