/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DataResponse, EmailTemplate, Filter, Script, SearchQuery } from "@api/model";
import { AnyArgFunction, NoArgFunction, ShowConfirmCaller } from "ish-ui";
import React, { ReactElement } from "react";
import { Dispatch } from "redux";
import { FormErrors, InjectedFormProps } from "redux-form";
import { CustomTableModelName, EntityName } from "../entities/common";
import { FormMenuTag } from "../tags";
import { MessageData } from "./Message";

export interface CoreFilter extends Filter {
  active?: boolean;
  customLabel?: () => React.ReactNode;
}

export interface FilterGroup {
  title?: string;
  filters: CoreFilter[];
}

export interface FindRelatedItem {
  title: string;
  list?: string;
  expression?: string;
  customExpression?: AnyArgFunction<string, string>;
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
  menuTags: FormMenuTag[];
  checkedChecklists?: FormMenuTag[];
  uncheckedChecklists?: FormMenuTag[];
  menuTagsLoaded?: boolean;
  filterGroups: FilterGroup[];
  filterGroupsLoaded?: boolean;
  records: DataResponse;
  plainRecords: DataResponse;
  editRecord: any;
  fetching: boolean;
  editRecordFetching: boolean;
  showColoredDots: boolean;
  selection: string[];
  search?: string;
  // current filters state
  searchQuery?: SearchQuery;
  searchError?: boolean;
  userAQLSearch?: string;
  customTableModel?: CustomTableModelName;
  savingFilter?: SavingFilterState;
  scripts?: Script[];
  emailTemplates?: EmailTemplate[];
  emailTemplatesWithKeyCode?: EmailTemplate[];
  creatingNew?: boolean;
  fullScreenEditView?: boolean;
  recepients?: MessageData;
}

export interface EditViewContainerProps<E = any> extends Partial<InjectedFormProps> {
  classes?: any;
  EditViewContent: React.FunctionComponent<EditViewProps>;
  hasSelected: boolean;
  creatingNew: boolean;
  customTableModel: CustomTableModelName;
  pending?: boolean;
  values?: E;
  updateDeleteCondition?: any;
  fullScreenEditView?: any;
  toogleFullScreenEditView: any;
  dispatch?: Dispatch<any>;
  rootEntity: EntityName;
  showConfirm: ShowConfirmCaller;
  manualLink?: any;
  isNested?: boolean;
  match?: any;
  nameCondition?: AnyArgFunction;
  updateCaption?: (arg: string) => React.Component;
  threeColumn?: boolean;
  alwaysFullScreenCreateView?: boolean;
  syncErrors?: any;
  disabledSubmitCondition?: boolean;
  hideTitle?: boolean;
}

export interface EditViewProps<V = any> extends Partial<InjectedFormProps<V>> {
  manualLink: string;
  rootEntity: EntityName;
  isNew: boolean;
  values: V;
  dispatch: any;
  updateDeleteCondition: AnyArgFunction;
  showConfirm: ShowConfirmCaller;
  onScroll?: AnyArgFunction;
  twoColumn?: boolean;
  isNested?: boolean;
  onCloseClick?: AnyArgFunction;
  syncErrors?: FormErrors<V>;
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
  stopIndex?: number;
  resolve?: AnyArgFunction;
  tableModel?: string;
}

export interface CustomColumnFormats {
  [column: string]: (value: any, row?: any, columns?: any) => string | ReactElement;
}

export type FilterScriptsBy = Record<string, {
  ids: string[];
  scripts: Script[];
}>