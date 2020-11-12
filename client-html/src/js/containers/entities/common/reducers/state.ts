import { ConcessionType, CorporatePass } from "@api/model";

export interface QuickSearchConcessionTypesState {
  items?: ConcessionType[];
  pending?: boolean;
}

export interface QuickSearchCorporatePassState {
  items?: CorporatePass[];
  pending?: boolean;
}
