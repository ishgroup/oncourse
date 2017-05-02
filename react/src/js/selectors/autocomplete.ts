import {IshState} from "../services/IshState";

export function selectSuggestions(state: IshState, key: string): string[] {
  return state.autocomplete[key] || [];
}
