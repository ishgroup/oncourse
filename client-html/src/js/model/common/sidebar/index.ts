import { StringKeyObject } from  "ish-ui";

export interface CommonListItem extends StringKeyObject<boolean | string | number> {
  id: number;
  name: string;
  custom?: boolean;
  hasIcon?: boolean;
  grayOut?: boolean;
}

export type CommonListFilterCondition = (item: CommonListItem | any) => boolean;

export interface CommonListFilter {
  name: string;
  condition: CommonListFilterCondition;
  icon?: React.ReactElement;
}

export interface SidebarSharedProps {
  activeFiltersConditions: CommonListFilterCondition[];
  history: any;
  search: string;
  category: string;
}
