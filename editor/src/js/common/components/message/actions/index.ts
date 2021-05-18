export const CLEAR_MESSAGE = "CLEAR_MESSAGE";
export const SHOW_MESSAGE = "SHOW_MESSAGE";

export const clearMessage = () => ({
  type: CLEAR_MESSAGE
});

export const showMessage = (payload: { message: string, error: boolean }) => ({
  type: SHOW_MESSAGE,
  payload
});