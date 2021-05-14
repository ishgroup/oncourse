import { FULFILLED, REJECTED } from "./ActionUtils";

export const FETCH_START = "common/fetch/start";
export const FETCH_SUCCESS = FULFILLED("common/fetch");
export const FETCH_FAIL = REJECTED("common/fetch");
export const FETCH_FINISH = "common/fetch/finish";
export const SHOW_MESSAGE = "SHOW_MESSAGE";

export const SET_CAPTCHA_TOKEN = "SET_CAPTCHA_TOKEN";
export const CHECK_SITENAME = "CHECK_SITENAME";
export const SET_SITENAME_VALID_VALUE = "SET_SITENAME_VALID_VALUE";
export const SET_SITENAME_VALUE = "SET_SITENAME_VALUE";
export const CREATE_COLLEGE = "CREATE_COLLEGE";
export const SET_TEMPLATE_VALUE = "SET_TEMPLATE_VALUE";
export const SET_CONTACT_FORM_VALUES = "SET_CONTACT_FORM_VALUES";
export const SET_ORGANISATION_FORM_VALUES = "SET_ORGANISATION_FORM_VALUES";
export const CLEAR_MESSAGE = "CLEAR_MESSAGE";
export const COLLEGE_WAS_CREATED = "COLLEGE_WAS_CREATED";
export const SET_SEND_TOKEN_AGAIN_VALUE = "SET_SEND_TOKEN_AGAIN_VALUE";
export const SET_SERVER_ERROR_VALUE = "SET_SERVER_ERROR_VALUE";
export const RESET_STORE = "RESET_STORE";
export const SET_LOADING_VALUE = "SET_LOADING_VALUE";

export const GET_SITES = "GET_SITES";
export const GET_SITES_FULFILLED = FULFILLED(GET_SITES);

export const getSites = (userId: string) => ({
  type: GET_SITES,
  payload: userId
});

export const setCaptchaToken = (token: string) => ({
  type: SET_CAPTCHA_TOKEN,
  payload: token
});

export const checkSiteName = ( payload: { name: string, token: string } ) => ({
  type: CHECK_SITENAME,
  payload
});

export const setSitenameValidValue = (payload: boolean) => ({
  type: SET_SITENAME_VALID_VALUE,
  payload
});

export const setSitenameValue = (payload: string) => ({
  type: SET_SITENAME_VALUE,
  payload
})

export const setCollegeWasCreatedValue = (payload: boolean) => ({
  type: COLLEGE_WAS_CREATED,
  payload
})

export const setTemplateValue = (template: string) => ({
  type: SET_TEMPLATE_VALUE,
  payload: template
})

export const setContactFormValues = (formData: any) => ({
  type: SET_CONTACT_FORM_VALUES,
  payload: formData
})

export const setOrganisationFormValues = (formData: any) => ({
  type: SET_ORGANISATION_FORM_VALUES,
  payload: formData
})

export const createCollege = (data: any) => ({
  type: CREATE_COLLEGE,
  payload: data
})

export const showMessage = (payload: { message: string, error: boolean }) => ({
  type: SHOW_MESSAGE,
  payload
});

export const clearMessage = () => ({
  type: CLEAR_MESSAGE
});

export const setServerErrorValue = (payload: boolean) => ({
  type: SET_SERVER_ERROR_VALUE,
  payload
})

export const resetStore = () => ({
  type: RESET_STORE
})

export const setLoadingValue = (payload: boolean) => ({
  type: SET_LOADING_VALUE,
  payload
})
