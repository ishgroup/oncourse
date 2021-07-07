export const DefaultConfig = {
  CHECKOUT_PATH: '/enrol',
  PAYMENT_SUCCESS_URL: "/courses",
  PAYMENTS_API_VERSION: "v1",
  TERMS_AND_CONDITIONS: null,
  FEATURE_ENROLMENT_DISCLOSURE: null,
};

export const IS_JEST = process.env.NODE_ENV === 'test';

export const DEFAULT_CONFIG_KEY = 'checkout_config';
