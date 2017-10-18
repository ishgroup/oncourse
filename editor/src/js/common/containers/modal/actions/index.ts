export const SHOW_MODAL: string = "common/show/modal";
export const HIDE_MODAL: string = "common/hide/modal";

export const showModal = props => ({
  type: SHOW_MODAL,
  payload: props,
});

export const hideModal = () => ({
  type: HIDE_MODAL,
});
