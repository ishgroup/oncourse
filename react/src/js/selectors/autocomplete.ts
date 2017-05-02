import {IshState} from "../services/IshState";

export interface Suggestion {
  key: string;
  value: string;
}

export function selectSuggestions(state: IshState, key: string): Suggestion[] {
  return state.autocomplete[key] || [];
}
