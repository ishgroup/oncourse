import {
  SET_CAPTCHA_TOKEN,
  CHECK_SITENAME,
  CREATE_COLLEGE,
  SET_CONTACT_FORM_VALUES,
  SET_ORGANISATION_FORM_VALUES,
  SET_TEMPLATE_VALUE,
  CLEAR_MESSAGE,
  SHOW_MESSAGE,
  SET_SITENAME_VALID_VALUE
} from "../actions";
import { contactFormInitialValue, organisationFormInitialValue } from "../initialValues";

const initState = {
  collegeKey: "",
  isValidName: true,
  webSiteTemplate: "",
  token: "",
  message: {
    message: "",
    error: ""
  },
  contactForm: contactFormInitialValue,
  organisationForm: organisationFormInitialValue,
}

export const createCollegeReducer = (state = initState, action) => {
  switch (action.type) {
    case SET_CAPTCHA_TOKEN:
      return {
        ...state,
        token: action.payload
      };

    case CHECK_SITENAME:
      return {
        ...state,
      };

    case SET_SITENAME_VALID_VALUE:
      return {
        ...state,
        isValidName: action.payload,
      };

    case SET_TEMPLATE_VALUE:
      return {
        ...state,
        webSiteTemplate: action.payload
      };

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

    case CREATE_COLLEGE:
      return {
        ...state,
      };

    case SHOW_MESSAGE:
      return {
        ...state,
        message: action.payload,
      };

    case CLEAR_MESSAGE:
      return {
        ...state,
        message: { message: "", error: false}
      };

    default:
      return state;
  }
}