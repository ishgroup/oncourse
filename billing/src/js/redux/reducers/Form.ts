/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from '../../models/IshAction';
import { FormsState } from '../../models/Forms';
import { contactFormInitialValue, organisationFormInitialValue } from '../initialValues';
import { SET_CONTACT_FORM_VALUES, SET_ORGANISATION_FORM_VALUES } from '../actions/College';

const Initial: FormsState = {
  contactForm: contactFormInitialValue,
  organisationForm: organisationFormInitialValue,
};

export const formReducer = (state: FormsState = Initial, action: IAction): FormsState => {
  switch (action.type) {
    case SET_CONTACT_FORM_VALUES:
      return {
        ...state,
        contactForm: action.payload
      };

    case SET_ORGANISATION_FORM_VALUES:
      return {
        ...state,
        organisationForm: action.payload
      };
    default:
      return {
        ...state
      };
  }
};
