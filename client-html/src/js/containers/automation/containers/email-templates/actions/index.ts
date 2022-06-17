import { EmailTemplate } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../../common/actions/ActionUtils";
import { CatalogItemType } from "../../../../../model/common/Catalog";

export const GET_EMAIL_TEMPLATES_LIST = _toRequestType("get/email-templates/list");
export const GET_EMAIL_TEMPLATES_LIST_FULFILLED = FULFILLED(GET_EMAIL_TEMPLATES_LIST);

export const CREATE_EMAIL_TEMPLATE = _toRequestType("post/email-templates/list");

export const UPDATE_EMAIL_TEMPLATE = _toRequestType("put/email-templates/list");
export const UPDATE_EMAIL_TEMPLATE_FULFILLED = FULFILLED(UPDATE_EMAIL_TEMPLATE);

export const UPDATE_INTERNAL_EMAIL_TEMPLATE = _toRequestType("patch/email-templates/list");
export const UPDATE_INTERNAL_EMAIL_TEMPLATE_FULFILLED = FULFILLED(UPDATE_INTERNAL_EMAIL_TEMPLATE);

export const REMOVE_EMAIL_TEMPLATE = _toRequestType("delete/email-templates/list");
export const REMOVE_EMAIL_TEMPLATE_FULFILLED = FULFILLED(REMOVE_EMAIL_TEMPLATE);

export const GET_EMAIL_TEMPLATE = _toRequestType("get/email-templates/item");
export const GET_EMAIL_TEMPLATE_FULFILLED = FULFILLED(GET_EMAIL_TEMPLATE);

export const getEmailTemplatesList = (selectFirst?: boolean, keyCodeToSelect?: string) => ({
  type: GET_EMAIL_TEMPLATES_LIST,
  payload: { selectFirst, keyCodeToSelect }
});

export const updateEmailTemplate = (emailTemplate: EmailTemplate) => ({
  type: UPDATE_EMAIL_TEMPLATE,
  payload: { emailTemplate }
});

export const updateInternalEmailTemplate = (emailTemplate: EmailTemplate) => ({
  type: UPDATE_INTERNAL_EMAIL_TEMPLATE,
  payload: { emailTemplate }
});

export const createEmailTemplate = (emailTemplate: EmailTemplate) => ({
  type: CREATE_EMAIL_TEMPLATE,
  payload: { emailTemplate }
});
export const removeEmailTemplate = (id: number) => ({
  type: REMOVE_EMAIL_TEMPLATE,
  payload: id
});

export const getEmailTemplate = (id: number) => ({
  type: GET_EMAIL_TEMPLATE,
  payload: id
});

export const getEmailTemplatesListFulfilled = (emailTemplates: CatalogItemType[]) => ({
  type: GET_EMAIL_TEMPLATES_LIST_FULFILLED,
  payload: { emailTemplates }
});
