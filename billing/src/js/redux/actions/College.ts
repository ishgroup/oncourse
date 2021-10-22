/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Currency } from '@api/model';
import { IAction } from '../../models/IshAction';

export const SET_CAPTCHA_TOKEN = 'SET_CAPTCHA_TOKEN';
export const SET_CURRENCY = 'SET_CURRENCY';
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

export const setCurrency = (cur: Currency):IAction => ({
  type: SET_CURRENCY,
  payload: cur
});

export const getCollegeKey = ():IAction => ({
  type: GET_COLLEGE_KEY
});

export const setCaptchaToken = (token: string):IAction => ({
  type: SET_CAPTCHA_TOKEN,
  payload: token
});

export const checkSiteName = (payload: { name: string, token: string }):IAction => ({
  type: CHECK_SITENAME,
  payload
});

export const setSitenameValidValue = (payload: boolean):IAction => ({
  type: SET_SITENAME_VALID_VALUE,
  payload
});

export const setCollegeKey = (payload: string):IAction => ({
  type: SET_COLLEGE_KEY,
  payload
});

export const setCollegeWasCreatedValue = (payload: boolean):IAction => ({
  type: COLLEGE_WAS_CREATED,
  payload
});

export const setTemplateValue = (template: string):IAction => ({
  type: SET_TEMPLATE_VALUE,
  payload: template
});

export const setContactFormValues = (formData: any):IAction => ({
  type: SET_CONTACT_FORM_VALUES,
  payload: formData
});

export const setOrganisationFormValues = (formData: any):IAction => ({
  type: SET_ORGANISATION_FORM_VALUES,
  payload: formData
});

export const createCollege = (data: any):IAction => ({
  type: CREATE_COLLEGE,
  payload: data
});
