export const SUBMIT_LOGIN_FORM: string = 'login/submit';

export const submitLoginForm = form => ({
  type: SUBMIT_LOGIN_FORM,
  payload: form,
})
