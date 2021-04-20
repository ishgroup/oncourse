/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Currency } from "@api/model";
import { IAction } from "../../../common/actions/IshAction";
import { Categories } from "../../../model/preferences";

import {
  GET_PREFERENCES_FULFILLED,
  SAVE_PREFERENCES_FULFILLED,
  GET_TIMEZONES_FULFILLED,
  GET_HOLIDAYS_FULFILLED,
  SAVE_HOLIDAYS_FULFILLED,
  DELETE_HOLIDAYS_ITEM_FULFILLED,
  GET_DATA_COLLECTION_FORMS_FULFILLED,
  GET_DATA_COLLECTION_FORM_FIELD_TYPES_FULFILLED,
  GET_DATA_COLLECTION_RULES_FULFILLED,
  CREATE_DATA_COLLECTION_RULE_FULFILLED,
  DELETE_DATA_COLLECTION_RULE_FULFILLED,
  UPDATE_DATA_COLLECTION_RULE_FULFILLED,
  UPDATE_DATA_COLLECTION_FORM_FULFILLED,
  CREATE_DATA_COLLECTION_FORM_FULFILLED,
  DELETE_DATA_COLLECTION_FORM_FULFILLED,
  GET_PAYMENT_TYPES_FULFILLED,
  DELETE_PAYMENT_TYPE_FULFILLED,
  UPDATE_PAYMENT_TYPES_FULFILLED,
  GET_TAX_TYPES_FULFILLED,
  DELETE_TAX_TYPE_FULFILLED,
  UPDATE_TAX_TYPES_FULFILLED,
  GET_CONCESSION_TYPES_FULFILLED,
  DELETE_CONCESSION_TYPE_FULFILLED,
  UPDATE_CONCESSION_TYPES_FULFILLED,
  GET_CONTACT_RELATION_TYPES_FULFILLED,
  DELETE_CONTACT_RELATION_TYPE_FULFILLED,
  UPDATE_CONTACT_RELATION_TYPES_FULFILLED,
  GET_CUSTOM_FIELDS_FULFILLED,
  UPDATE_CUSTOM_FIELDS_FULFILLED,
  GET_ENUM_FULFILLED,
  GET_COMPLEX_PASS_FILLED,
  GET_IS_LOGGED_FULFILLED,
  GET_COUNTRIES_REQUEST_FULFILLED,
  GET_COLUMNS_WIDTH_REQUEST_FULFILLED,
  GET_CURRENCY_FULFILLED,
  GET_PREFERENCES_BY_KEYS_FULFILLED,
  GET_ACCOUNT_TRANSACTION_LOCKED_DATE_FULFILLED,
  GET_TUTOR_ROLES_FULFILLED,
  GET_LANGUAGES_REQUEST_FULFILLED,
  GET_USI_SORTWARE_ID_FULFILLED,
  GET_ENTITY_RELATION_TYPES_FULFILLED,
  DELETE_ENTITY_RELATION_TYPE_FULFILLED,
  UPDATE_ENTITY_RELATION_TYPES_FULFILLED, GET_GRADING_TYPES_FULFILLED
} from "../actions";
import { GET_MESSAGE_QUEUED_FULFILLED, POST_AUTHENTICATION_FULFILLED } from "../../../common/actions";
import getTimestamps from "../../../common/utils/timestamps/getTimestamps";
import {
  DELETE_FUNDING_CONTACT_FULFILLED,
  GET_FUNDING_CONTACTS_FULFILLED,
  SAVE_FUNDING_CONTACTS_FULFILLED
} from "../containers/funding-contracts/actions";
import { PreferencesState } from "./state";

export const preferencesReducer = (state: PreferencesState = {}, action: IAction<any>): PreferencesState => {
  switch (action.type) {
    case GET_COMPLEX_PASS_FILLED: {
      const complexPass = action.payload;
      return {
        ...state,
        complexPass
      };
    }

    case POST_AUTHENTICATION_FULFILLED:
    case GET_IS_LOGGED_FULFILLED: {
      const isLogged = action.payload;
      return {
        ...state,
        isLogged
      };
    }
    case GET_PREFERENCES_FULFILLED:
    case SAVE_PREFERENCES_FULFILLED:
    case GET_PREFERENCES_BY_KEYS_FULFILLED: {
      const { preferences, category } = action.payload;
      const newPreferences = {} as any;
      const categoryName = Categories[category];
      const timestamps = getTimestamps(preferences);

      preferences.forEach(item => (newPreferences[item.uniqueKey] = item.valueString));

      newPreferences.created = timestamps[0];
      newPreferences.modified = timestamps[1];

      return {
        ...state,
        [categoryName]: { ...(state[categoryName] || {}), ...newPreferences }
      };
    }

    case GET_HOLIDAYS_FULFILLED:
    case SAVE_HOLIDAYS_FULFILLED:
    case DELETE_HOLIDAYS_ITEM_FULFILLED: {
      const { holidays } = action.payload;

      return {
        ...state,
        holidays
      };
    }

    case GET_DATA_COLLECTION_FORMS_FULFILLED:
    case UPDATE_DATA_COLLECTION_FORM_FULFILLED:
    case CREATE_DATA_COLLECTION_FORM_FULFILLED:
    case DELETE_DATA_COLLECTION_FORM_FULFILLED: {
      const { dataCollectionForms } = action.payload;

      return {
        ...state,
        dataCollectionForms
      };
    }

    case GET_DATA_COLLECTION_FORM_FIELD_TYPES_FULFILLED: {
      const { dataCollectionFormFieldTypes } = action.payload;

      return {
        ...state,
        dataCollectionFormFieldTypes
      };
    }

    case GET_DATA_COLLECTION_RULES_FULFILLED:
    case CREATE_DATA_COLLECTION_RULE_FULFILLED:
    case UPDATE_DATA_COLLECTION_RULE_FULFILLED:
    case DELETE_DATA_COLLECTION_RULE_FULFILLED: {
      const { dataCollectionRules } = action.payload;

      return {
        ...state,
        dataCollectionRules
      };
    }

    case GET_PAYMENT_TYPES_FULFILLED:
    case DELETE_PAYMENT_TYPE_FULFILLED:
    case UPDATE_PAYMENT_TYPES_FULFILLED: {
      const { paymentTypes } = action.payload;

      return {
        ...state,
        paymentTypes
      };
    }

    case GET_TAX_TYPES_FULFILLED:
    case DELETE_TAX_TYPE_FULFILLED:
    case UPDATE_TAX_TYPES_FULFILLED: {
      const { taxTypes } = action.payload;

      return {
        ...state,
        taxTypes
      };
    }

    case GET_CONCESSION_TYPES_FULFILLED:
    case DELETE_CONCESSION_TYPE_FULFILLED:
    case UPDATE_CONCESSION_TYPES_FULFILLED: {
      const { concessionTypes } = action.payload;

      return {
        ...state,
        concessionTypes
      };
    }

    case GET_CONTACT_RELATION_TYPES_FULFILLED:
    case DELETE_CONTACT_RELATION_TYPE_FULFILLED:
    case UPDATE_CONTACT_RELATION_TYPES_FULFILLED: {
      const { contactRelationTypes } = action.payload;

      return {
        ...state,
        contactRelationTypes
      };
    }

    case GET_ENTITY_RELATION_TYPES_FULFILLED:
    case DELETE_ENTITY_RELATION_TYPE_FULFILLED:
    case UPDATE_ENTITY_RELATION_TYPES_FULFILLED: {
      const { entityRelationTypes } = action.payload;

      return {
        ...state,
        entityRelationTypes
      };
    }

    case GET_CUSTOM_FIELDS_FULFILLED:
    case UPDATE_CUSTOM_FIELDS_FULFILLED: {
      const { customFields } = action.payload;

      return {
        ...state,
        customFields
      };
    }

    case GET_MESSAGE_QUEUED_FULFILLED: {
      const { count, type } = action.payload;

      return {
        ...state,
        [type]: count
      };
    }

    case GET_COLUMNS_WIDTH_REQUEST_FULFILLED: {
      const { columnWidth } = action.payload;

      return {
        ...state,
        columnWidth
      };
    }

    case GET_FUNDING_CONTACTS_FULFILLED:
    case SAVE_FUNDING_CONTACTS_FULFILLED:
    case DELETE_FUNDING_CONTACT_FULFILLED: {
      const { fundingContracts } = action.payload;

      return {
        ...state,
        fundingContracts
      };
    }

    case GET_TUTOR_ROLES_FULFILLED: {
      const { tutorRoles } = action.payload;

      return {
        ...state,
        tutorRoles
      };
    }

    case GET_GRADING_TYPES_FULFILLED: {
      return {
        ...state,
        gradingTypes: action.payload
      };
    }

    default:
      return state;
  }
};

const initialCurrency: Currency = {
  name: null,
  shortCurrencySymbol: null,
  currencySymbol: null
};

export const currencyReducer = (state: Currency = { ...initialCurrency }, action: IAction<any>): Currency => {
  switch (action.type) {
    case GET_CURRENCY_FULFILLED: {
      const { currency } = action.payload;
      return currency;
    }

    default:
      return state;
  }
};

export const usiSoftwareIdReducer = (state: string = null, action: IAction<string>): string => {
  switch (action.type) {
    case GET_USI_SORTWARE_ID_FULFILLED: {
      return action.payload;
    }

    default:
      return state;
  }
};

export const lockedDateReducer = (state: Date = null, action: IAction<any>): Date => {
  switch (action.type) {
    case GET_ACCOUNT_TRANSACTION_LOCKED_DATE_FULFILLED: {
      const { lockedDate } = action.payload;
      return lockedDate;
    }

    default:
      return state;
  }
};

export const enumsReducer = (state: string[] = [], action: IAction<any>): string[] => {
  switch (action.type) {
    case GET_ENUM_FULFILLED: {
      const { enums, type } = action.payload;

      return {
        ...state,
        [type]: enums
      };
    }
    default:
      return state;
  }
};

export const timezonesReducer = (state: string[] = [], action: IAction<any>): string[] => {
  switch (action.type) {
    case GET_TIMEZONES_FULFILLED: {
      return action.payload.timezones;
    }
    default:
      return state;
  }
};

export const countriesReducer = (state: string[] = [], action: IAction<any>): string[] => {
  switch (action.type) {
    case GET_COUNTRIES_REQUEST_FULFILLED: {
      return action.payload.countries;
    }
    default:
      return state;
  }
};

export const languagesReducer = (state: string[] = [], action: IAction<any>): string[] => {
  switch (action.type) {
    case GET_LANGUAGES_REQUEST_FULFILLED: {
      return action.payload.languages;
    }
    default:
      return state;
  }
};
