import {
  SET_CAPTCHA_TOKEN,
  CHECK_SITENAME,
  CREATE_COLLEGE,
  SET_CONTACT_FORM_VALUES,
  SET_ORGANISATION_FORM_VALUES,
  SET_TEMPLATE_VALUE,
  CLEAR_MESSAGE,
  SHOW_MESSAGE,
  SET_SITENAME_VALID_VALUE,
  COLLEGE_WAS_CREATED,
  SET_SEND_TOKEN_AGAIN_VALUE,
  SET_SITENAME_VALUE,
  SET_SERVER_ERROR_VALUE,
  RESET_STORE,
  SET_LOADING_VALUE, GET_SITES, GET_SITES_FULFILLED,
} from "../actions";
import { contactFormInitialValue, organisationFormInitialValue } from "../initialValues";
import {Site} from "../../models/Site";


export interface State {
  collegeKey: string;
  isValidName: boolean;
  webSiteTemplate: string;
  token: string;
  collegeWasCreated: boolean;
  sendTokenAgain: boolean;
  serverError: boolean;
  loading: boolean;
  sites: Site[];
  message: {
    message: string;
    error: boolean;
  },
  contactForm: {
    userFirstName: string;
    userLastName: string;
    userPhone: string;
    userEmail: string;
  },
  organisationForm: {
    organisationName: string;
    abn: string;
    tradingName: string;
    address: string;
    suburb: string;
    state: string;
    postcode: string;
    country: string;
    timeZone: string;
  }
}

const initState: State = {
  collegeKey: "",
  isValidName: false,
  webSiteTemplate: "",
  token: "",
  collegeWasCreated: false,
  sendTokenAgain: true,
  serverError: false,
  loading: false,
  sites: [],
  message: {
    message: "",
    error: false
  },
  contactForm: contactFormInitialValue,
  organisationForm: organisationFormInitialValue,
}

export const createCollegeReducer = (state: State = initState, action): State => {
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
        isValidName: action.payload
      };

    case SET_SITENAME_VALUE:
      return {
        ...state,
        collegeKey: action.payload
      }

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

    case SET_SEND_TOKEN_AGAIN_VALUE:
      return {
        ...state,
        sendTokenAgain: action.payload
      };

    case GET_SITES:
    case CREATE_COLLEGE:
      return {
        ...state,
        loading: true
      };

    case COLLEGE_WAS_CREATED:
      return {
        ...state,
        collegeWasCreated: action.payload
      }

    case SHOW_MESSAGE:
      return {
        ...state,
        message: action.payload
      };

    case CLEAR_MESSAGE:
      return {
        ...state,
        message: { message: "", error: false }
      };

    case SET_SERVER_ERROR_VALUE:
      return {
        ...state,
        serverError: action.payload
      }

    case RESET_STORE:
      return {
        ...initState
      }

    case SET_LOADING_VALUE:
      return {
        ...state,
        loading: action.payload
      }

    case GET_SITES_FULFILLED:
      return {
        ...state,
        sites: action.payload,
        loading: false
      }

    default:
      return state;
  }
}
