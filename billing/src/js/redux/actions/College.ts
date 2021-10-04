/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export const SET_CAPTCHA_TOKEN = 'SET_CAPTCHA_TOKEN';
export const CHECK_SITENAME = 'CHECK_SITENAME';
export const SET_SITENAME_VALID_VALUE = 'SET_SITENAME_VALID_VALUE';
export const SET_COLLEGE_KEY = 'SET_COLLEGE_KEY';
export const CREATE_COLLEGE = 'CREATE_COLLEGE';
export const SET_TEMPLATE_VALUE = 'SET_TEMPLATE_VALUE';
export const SET_CONTACT_FORM_VALUES = 'SET_CONTACT_FORM_VALUES';
export const SET_ORGANISATION_FORM_VALUES = 'SET_ORGANISATION_FORM_VALUES';
export const COLLEGE_WAS_CREATED = 'COLLEGE_WAS_CREATED';
export const SET_SEND_TOKEN_AGAIN_VALUE = 'SET_SEND_TOKEN_AGAIN_VALUE';
export const GET_COLLEGE_KEY = 'GET_COLLEGE_KEY';

export const getCollegeKey = () => ({
  type: GET_COLLEGE_KEY
});

export const setCaptchaToken = (token: string) => ({
  type: SET_CAPTCHA_TOKEN,
  payload: token
});

export const checkSiteName = (payload: { name: string, token: string }) => ({
  type: CHECK_SITENAME,
  payload
});

export const setSitenameValidValue = (payload: boolean) => ({
  type: SET_SITENAME_VALID_VALUE,
  payload
});

export const setCollegeKey = (payload: string) => ({
  type: SET_COLLEGE_KEY,
  payload
});

export const setCollegeWasCreatedValue = (payload: boolean) => ({
  type: COLLEGE_WAS_CREATED,
  payload
});

export const setTemplateValue = (template: string) => ({
  type: SET_TEMPLATE_VALUE,
  payload: template
});

export const setContactFormValues = (formData: any) => ({
  type: SET_CONTACT_FORM_VALUES,
  payload: formData
});

export const setOrganisationFormValues = (formData: any) => ({
  type: SET_ORGANISATION_FORM_VALUES,
  payload: formData
});

export const createCollege = (data: any) => ({
  type: CREATE_COLLEGE,
  payload: data
});
