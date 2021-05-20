/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { ReactElement } from "react";
import { InjectedFormProps } from "redux-form";
import { Dispatch } from "redux";
import { DataResponse, EmailTemplate, Filter, Script, SearchQuery } from "@api/model";
import { MenuTag } from "../tags";
import { AnyArgFunction, NoArgFunction } from "./CommonFunctions";
import { ShowConfirmCaller } from "./Confirm";
import { MessageData } from "./Message";
import { EntityName } from "../entities/common";

export interface CoreFilter extends Filter {
  active?: boolean;
  customLabel?: React.ReactNode;
}

export interface FilterGroup {
  title?: string;
  filters: CoreFilter[];
}

export interface FindRelatedItem {
  title: string;
  list?: string;
  expression?: string;
  customExpression?: string;
  destination?: string;
  items?: FindRelatedItem[];
}

export interface SavingFilterState {
  aqlSearch: string;
  isPrivate: boolean;
}

export interface CogwhelAdornmentProps {
  closeMenu: NoArgFunction;
  menuItemClass: string;
  searchQuery: string;
  selection: string[];
  showConfirm: ShowConfirmCaller;
  onCreate: NoArgFunction;
  entity: EntityName;
  showBulkEditDrawer: boolean;
  toggleBulkEditDrawer: NoArgFunction;
  records: DataResponse;
}

export interface ListState {
  menuTags: MenuTag[];
  menuTagsLoaded?: boolean;
  filterGroups: FilterGroup[];
  filterGroupsLoaded?: boolean;
  records: DataResponse;
  plainRecords: DataResponse;
  editRecord: any;
  fetching: boolean;
  editRecordFetching: boolean;
  selection: string[];
  search?: string;
  // current filters state
  searchQuery?: SearchQuery;
  searchError?: boolean;
  userAQLSearch?: string;
  nestedEditRecords?: any[];
  savingFilter?: SavingFilterState;
  scripts?: Script[];
  emailTemplates?: EmailTemplate[];
  emailTemplatesWithKeyCode?: EmailTemplate[];
  creatingNew?: boolean;
  fullScreenEditView?: boolean;
  recordsLeft?: number;
  recepients?: MessageData;
}

export interface EditViewContainerProps<E = any> extends Partial<InjectedFormProps> {
  classes?: any;
  EditViewContent: React.FunctionComponent<EditViewProps>;
  hasSelected: boolean;
  creatingNew: boolean;
  pending?: boolean;
  values?: E;
  updateDeleteCondition?: any;
  fullScreenEditView?: any;
  toogleFullScreenEditView: any;
  dispatch?: Dispatch<any>;
  rootEntity: EntityName;
  showConfirm: ShowConfirmCaller;
  openNestedEditView: any;
  manualLink?: any;
  isNested?: boolean;
  match?: any;
  nestedIndex?: number;
  nameCondition?: AnyArgFunction;
  hideFullScreenAppBar?: boolean;
  updateCaption?: (arg: string) => React.Component;
  threeColumn?: boolean;
  alwaysFullScreenCreateView?: boolean;
  syncErrors?: any;
  disabledSubmitCondition?: boolean;
}

export interface EditViewProps<V = any> extends Partial<InjectedFormProps<V>> {
  manualLink: string;
  rootEntity: EntityName;
  isNew: boolean;
  values: V;
  dispatch: any;
  updateDeleteCondition: AnyArgFunction;
  showConfirm: ShowConfirmCaller;
  openNestedEditView: AnyArgFunction;
  twoColumn?: boolean;
  isNested?: boolean;
  nestedIndex?: number;
  onCloseClick?: AnyArgFunction;
  syncErrors?: any;
  tabIndex?: number;
  expanded?: number[];
  setExpanded?: (arg: number[] | ((arg: number[]) => void)) => void;
  toogleFullScreenEditView?: any;
}

export type ListAqlMenuItemsRenderer = (content: React.ReactNode, rowData: any, searchValue: string) => void;

export interface GetRecordsArgs {
  entity: string;
  viewAll?: boolean;
  listUpdate?: boolean;
  savedID?: any;
  ignoreSelection?: boolean;
  startIndex?: number;
  stopIndex?: number;
  resolve?: AnyArgFunction;
}

export interface CustomColumnFormats {
  [column: string]: (value: any, row?: any, columns?: any) => string | ReactElement;
}
