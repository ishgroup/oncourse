import {Suggestion} from "../selectors/autocomplete";

export interface AutocompleteResponsePayload {
  readonly key: string;
  readonly candidates: Suggestion[];
}

export interface AutocompleteRequestPayload {
  readonly key: string;
  readonly text: string;
}
