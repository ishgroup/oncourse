export const SET_MESSAGE = 'set/message';

export const setMessage = (message: string) => ({
  type: SET_MESSAGE,
  payload: { message }
});
