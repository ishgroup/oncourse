export interface AutocompleteResponsePayload {
  readonly key: string;
  readonly candidates: string[];
}

export interface AutocompleteRequestPayload {
  readonly key: string;
  readonly text: string;
}
