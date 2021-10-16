import { _toRequestType } from "../../../actions/ActionUtils";

export const TOGGLE_TAB_LIST_EXPANDED = _toRequestType("toggle/tab/list/expanded");

export const toggleTabListExpanded = (rootEntity: string, expanded: number[]) => ({
  type: TOGGLE_TAB_LIST_EXPANDED,
  payload: { rootEntity, expanded }
});