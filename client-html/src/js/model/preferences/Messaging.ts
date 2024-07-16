import { PreferenceSchema } from "./PreferencesSchema";

export const EmailAdminAddress: PreferenceSchema = {
  uniqueKey: "email.admin",
  mandatory: false,
  editable: true
};

export const EmailBounceAddress: PreferenceSchema = {
  uniqueKey: "email.bounce.address",
  mandatory: false,
  editable: true
};

export const EmailBounceEnabled: PreferenceSchema = {
  uniqueKey: "email.bounce",
  mandatory: false,
  editable: true
};

export const EmailFromAddress: PreferenceSchema = {
  uniqueKey: "email.from",
  mandatory: true,
  editable: true
};

export const EmailFromName: PreferenceSchema = {
  uniqueKey: "email.from.name",
  mandatory: false,
  editable: true
};

export const EmailPop3Account: PreferenceSchema = {
  uniqueKey: "email.pop3.account",
  mandatory: false,
  editable: true
};

export const EmailPop3Host: PreferenceSchema = {
  uniqueKey: "email.pop3host",
  mandatory: false,
  editable: true
};

export const EmailPop3Password: PreferenceSchema = {
  uniqueKey: "email.pop3.password",
  mandatory: false,
  editable: true
};

export const SMSFromAddress: PreferenceSchema = {
  uniqueKey: "sms.from",
  mandatory: false,
  editable: true
};

export const EmailDeliveryWaitingListLimit: PreferenceSchema = {
  uniqueKey: "contact.email.delivery.disable.limit.waiting_list",
  mandatory: false,
  editable: true
};

export const EmailDeliveryEnrolmentLimit: PreferenceSchema = {
  uniqueKey: "contact.email.delivery.disable.limit.enrolment",
  mandatory: false,
  editable: true
};

export const EmailDeliveryApplicationLimit: PreferenceSchema = {
  uniqueKey: "contact.email.delivery.disable.limit.application",
  mandatory: false,
  editable: true
};

export const EmailDeliveryCheckoutLimit: PreferenceSchema = {
  uniqueKey: "contact.email.delivery.disable.limit.checkout",
  mandatory: false,
  editable: true
};

export const EmailDeliveryPortalLimit: PreferenceSchema = {
  uniqueKey: "contact.email.delivery.disable.limit.portal",
  mandatory: false,
  editable: true
};