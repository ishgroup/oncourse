import { _toRequestType, FULFILLED } from "../../../../../common/actions/ActionUtils";

export const GET_AUTOMATION_PDF_BACKGROUNDS_LIST = _toRequestType("get/pdf-backgrounds/list");
export const GET_AUTOMATION_PDF_BACKGROUNDS_LIST_FULFILLED = FULFILLED(GET_AUTOMATION_PDF_BACKGROUNDS_LIST);

export const CREATE_AUTOMATION_PDF_BACKGROUND = _toRequestType("post/pdf-backgrounds/list");

export const UPDATE_AUTOMATION_PDF_BACKGROUND = _toRequestType("put/pdf-backgrounds/list");

export const REMOVE_AUTOMATION_PDF_BACKGROUND = _toRequestType("delete/pdf-backgrounds/list");

export const GET_AUTOMATION_PDF_BACKGROUND = _toRequestType("get/pdf-backgrounds/item");

export const updateAutomationPdfBackground = (fileName: string, id: number, overlay: File) => ({
  type: UPDATE_AUTOMATION_PDF_BACKGROUND,
  payload: { fileName, id, overlay }
});

export const createAutomationPdfBackground = (fileName: string, overlay: File) => ({
  type: CREATE_AUTOMATION_PDF_BACKGROUND,
  payload: { fileName, overlay }
});
export const removeAutomationPdfBackground = (id: number) => ({
  type: REMOVE_AUTOMATION_PDF_BACKGROUND,
  payload: id
});

export const getAutomationPdfBackground = (id: number) => ({
  type: GET_AUTOMATION_PDF_BACKGROUND,
  payload: id
});

export const getAutomationPdfBackgroundsList = (selectFirst?: boolean, filenameToSelect?: string) => ({
  type: GET_AUTOMATION_PDF_BACKGROUNDS_LIST,
  payload: { selectFirst, filenameToSelect }
});
