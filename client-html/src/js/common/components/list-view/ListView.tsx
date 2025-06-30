/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Currency, ExportTemplate, LayoutType, Report, TableModel } from '@api/model';
import ErrorOutline from '@mui/icons-material/ErrorOutline';
import { Button } from '@mui/material';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import $t from '@t';
import { History, Location } from 'history';
import {
  AnyArgFunction,
  BooleanArgFunction,
  ConfirmProps,
  NoArgFunction,
  ResizableWrapper,
  ShowConfirmCaller,
  StringArgFunction,
  usePrevious
} from 'ish-ui';
import React, { useEffect, useMemo, useRef } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom';
import { Dispatch } from 'redux';
import { getFormSyncErrors, initialize, isDirty, isInvalid, submit } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import {
  ENTITY_AQL_STORAGE_NAME,
  LIST_MAIN_CONTENT_DEFAULT_WIDTH,
  LIST_SIDE_BAR_DEFAULT_WIDTH,
  LISTVIEW_MAIN_CONTENT_WIDTH
} from '../../../constants/Config';
import {
  createEntityRecord,
  deleteEntityRecord,
  getEntityRecord,
  updateEntityRecord
} from '../../../containers/entities/common/actions';
import { getCustomFieldTypes } from '../../../containers/entities/customFieldTypes/actions';
import { Fetch } from '../../../model/common/Fetch';
import {
  EditViewContainerProps,
  FilterGroup,
  FindRelatedItem,
  ListAqlMenuItemsRenderer
} from '../../../model/common/ListView';
import { EntityName, FindEntityState } from '../../../model/entities/common';
import { FormMenuTag } from '../../../model/tags';
import { State } from '../../../reducers/state';
import { closeConfirm, getScripts, getUserPreferences, setUserPreference, showConfirm } from '../../actions';
import { IAction } from '../../actions/IshAction';
import { UserPreferencesState } from '../../reducers/userPreferencesReducer';
import { getEntityDisplayName } from '../../utils/getEntityDisplayName';
import { onSubmitFail } from '../../utils/highlightFormErrors';
import { saveCategoryAQLLink } from '../../utils/links';
import { LSGetItem } from '../../utils/storage';
import { pushGTMEvent } from '../google-tag-manager/actions';
import { GAEventTypes } from '../google-tag-manager/services/GoogleAnalyticsService';
import LoadingIndicator from '../progress/LoadingIndicator';
import {
  deleteCustomFilter,
  findRelatedByFilter,
  getRecords,
  setFilterGroups,
  setListEditRecord,
  setListEditRecordFetching,
  setListEntity,
  setListFullScreenEditView,
  setListLayout,
  setListMenuTags,
  setListSelection,
  setListUserAQLSearch,
  setSearch,
  updateTableModel,
} from './actions';
import BottomAppBar from './components/bottom-app-bar/BottomAppBar';
import BulkEditContainer from './components/bulk-edit/BulkEditContainer';
import EditView from './components/edit-view/EditView';
import FullScreenEditView from './components/full-screen-edit-view/FullScreenEditView';
import ReactTableList, { TableListProps } from './components/list/ReactTableList';
import ShareContainer from './components/share/ShareContainer';
import SideBar from './components/side-bar/SideBar';
import { LIST_EDIT_VIEW_FORM_NAME } from './constants';
import {
  getActiveTags,
  getFiltersNameString,
  getTagsUpdatedByIds,
  setActiveFiltersBySearch
} from './utils/listFiltersUtils';
import { shouldAsyncValidate } from './utils/listFormUtils';

const sideBarTheme = theme => createTheme({
  ...theme,
  overrides: {
    MuiFormControlLabel: {
      label: {
        fontSize: "12px"
      }
    }
  }
});

interface OwnProps {
  onCreate?: (item: any) => void;
  onDelete?: (id: number) => void;
  getEditRecord?: (id: number) => void;
  onSave?: (item: any) => void;
  classes?: any;
  isDirty?: boolean;
  isInvalid?: boolean;
  editRecord?: any;
  fullScreenEditView?: boolean;
  fetching?: boolean;
  savingFilter?: any;
  onSearch?: StringArgFunction;
  getCustomFieldTypes?: (entity: EntityName) => void;
  setEntity?: (entity: EntityName) => void;
  getListViewPreferences?: () => void;
  preferences?: UserPreferencesState;
  setListviewMainContentWidth?: (value: string) => void;
  submitForm?: any;
  closeConfirm?: () => void;
  onLoadMore?: (startIndex: number, stopIndex: number, resolve: AnyArgFunction) => void;
  updateTableModel?: (model: TableModel, listUpdate?: boolean) => void;
  dispatch?: Dispatch<IAction>;
  fetch?: Fetch;
  setFilterGroups?: (filterGroups: FilterGroup[]) => void;
  setListMenuTags?: ({ tags, checkedChecklists, uncheckedChecklists }: { tags: FormMenuTag[], checkedChecklists: FormMenuTag[], uncheckedChecklists: FormMenuTag[] }) => void;
  deleteFilter?: (id: number, entity: string, checked: boolean) => void;
  exportTemplates?: ExportTemplate[];
  pdfReports?: Report[];
  updateLayout?: (layout: LayoutType) => void;
  updateSelection?: (selection: string[]) => void;
  setListUserAQLSearch?: (userAQLSearch: string) => void;
  getScripts?: NoArgFunction;
  openConfirm?: ShowConfirmCaller;
  resetEditView?: NoArgFunction;
  setListFullScreenEditView?: BooleanArgFunction;
  sendGAEvent?: (event: GAEventTypes, screen: string, time?: number) => void;
  currency?: Currency;
  findRelatedByFilter?: AnyArgFunction;
  setListEditRecordFetching?: any;
}

interface Props {
  history: History;
  location: Location;
  match: any;
  listProps: TableListProps;
  rootEntity: EntityName;
  onBeforeSave?: any;
  EditViewContent: any;
  customTabTitle?: string;
  defaultDeleteDisabled?: boolean;
  createButtonDisabled?: boolean;
  scriptsFilterColumn?: string;
  filterEntity?: EntityName;
  filterGroups?: FilterGroup[];
  filterGroupsInitial?: FilterGroup[];
  onInit?: NoArgFunction;
  findRelated?: FindRelatedItem[];
  editViewProps?: {
    nameCondition?: EditViewContainerProps["nameCondition"];
    manualLink?: EditViewContainerProps["manualLink"];
    validate?: any;
    asyncValidate?: any;
    asyncBlurFields?: string[];
    asyncChangeFields?: string[];
    disabledSubmitCondition?: boolean;
    enableReinitialize?: boolean;
    keepDirtyOnReinitialize?: boolean;
    hideTitle?: EditViewContainerProps["hideTitle"];
  };
  CogwheelAdornment?: any;
  alwaysFullScreenCreateView?: any;
  CustomFindRelatedMenu?: any;
  ShareContainerAlertComponent?: any;
  searchMenuItemsRenderer?: ListAqlMenuItemsRenderer;
  customOnCreate?: any;
  customUpdateAction?: any;
  preformatBeforeSubmit?: AnyArgFunction<any>;
  deleteDisabledCondition?: (props) => boolean;
  noListTags?: boolean;
  deleteWithoutConfirmation?: boolean;
  getCustomBulkEditFields?: any;
}

interface ComponentState {
  showExportDrawer: boolean;
  showBulkEditDrawer: boolean;
  filtersSynchronized: boolean;
  mounted: boolean;
  querySearch: boolean;
  deleteEnabled: boolean;
  threeColumn: boolean;
  sidebarWidth: number;
  mainContentWidth: number;
  newSelection: string[] | null;
}

type ListCompProps = Props & OwnProps & State["list"] & State["share"];

function ListView(props: ListCompProps) {
  const {
    onInit,
    customOnCreate,
    getScripts,
    getCustomFieldTypes,
    history,
    sendGAEvent,
    rootEntity,
    setEntity,
    deleteFilter,
    match: { url, params },
    search,
    location,
    filterGroupsInitial = [],
    selection,
    getListViewPreferences,
    isDirty,
    updateSelection,
    editRecord,
    resetEditView,
    updateLayout,
    closeConfirm,
    openConfirm,
    isInvalid,
    fullScreenEditView,
    submitForm,
    alwaysFullScreenCreateView,
    setListFullScreenEditView,
    updateTableModel,
    records,
    fetch,
    defaultDeleteDisabled,
    filterGroups,
    menuTags,
    editRecordFetching,
    setListEditRecordFetching,
    setListUserAQLSearch,
    deleteDisabledCondition,
    menuTagsLoaded,
    filterGroupsLoaded,
    noListTags,
    preferences,
    checkedChecklists,
    uncheckedChecklists,
    customTabTitle,
    setListviewMainContentWidth,
    onSearch,
    setFilterGroups,
    setListMenuTags,
    getEditRecord,
    onBeforeSave,
    preformatBeforeSubmit,
    onCreate,
    deleteWithoutConfirmation,
    classes,
    EditViewContent,
    fetching,
    findRelated,
    editViewProps = {},
    CogwheelAdornment,
    savingFilter,
    CustomFindRelatedMenu,
    ShareContainerAlertComponent,
    pdfReports,
    exportTemplates,
    searchMenuItemsRenderer,
    createButtonDisabled,
    searchQuery,
    getCustomBulkEditFields,
    filterEntity,
    emailTemplatesWithKeyCode,
    scripts,
    listProps,
    onLoadMore,
    currency,
    dispatch,
    findRelatedByFilter,
    scriptsFilterColumn,
    customTableModel,
    userAQLSearch
  } = props;

  const containerNode = useRef(null);

  const searchComponentNode = useRef(null);

  const getMainContentWidth = (mainContentWidth, sidebarWidth) =>
    (mainContentWidth ? Number(mainContentWidth) : window.screen.width - sidebarWidth - 368);

  const creatingNew = useMemo(() => params.id === 'new', [params.id]);

  const [state, changeState] = React.useState<ComponentState>(
    {
      showExportDrawer: false,
      showBulkEditDrawer: false,
      filtersSynchronized: false,
      querySearch: false,
      mounted: false,
      deleteEnabled: !props.defaultDeleteDisabled,
      threeColumn: false,
      sidebarWidth: LIST_SIDE_BAR_DEFAULT_WIDTH,
      mainContentWidth: getMainContentWidth(LIST_MAIN_CONTENT_DEFAULT_WIDTH, LIST_SIDE_BAR_DEFAULT_WIDTH),
      newSelection: null
    }
  );

  const setState = (newState: Partial<ComponentState>) => {
    changeState(prev => ({
      ...prev,
      ...newState,
    }));
  };

  const updateHistoryPathname = (pathname: string) => {
    const newUrl = window.location.origin + pathname + window.location.search;

    if (newUrl !== window.location.href) {
      history.push({
        pathname,
        search:  window.location.search
      });
    }
  };

  const updateHistorySearch = (search: string) => {
    const newUrl = window.location.origin + window.location.pathname + search;

    if (newUrl !== window.location.href) {
      history.push({
        search,
        pathname: window.location.pathname
      });
    }
  };

  useEffect(() => {
    if (!fullScreenEditView && params.id && (!state.threeColumn || alwaysFullScreenCreateView)) {
      setListFullScreenEditView(true);
    }
    if (fullScreenEditView && !params.id) {
      setListFullScreenEditView(false);
    }
  }, [params.id, state.threeColumn, alwaysFullScreenCreateView, fullScreenEditView]);

  const toggleFullWidthView = (fullScreenState: boolean) => {
    if (alwaysFullScreenCreateView && creatingNew) {
      resetEditView();
      updateSelection([]);
    }

    if (!fullScreenState && params.id) {
      updateHistoryPathname(url.replace(`/${params.id}`, ""));
      resetEditView();
    }
  };

  const onSelection = newSelection => {
    if (newSelection?.length === 1 && selection?.length === 1 && newSelection[0] === selection[0]) {
      return;
    }

    if (isDirty) {
      setState({ newSelection });
      showConfirm(
        {
          onConfirm: () => {
            onSelection(newSelection);
            if (isDirty) {
              resetEditView();
            }
          }
        },
      );
      return;
    }

    if (newSelection && !newSelection.length) {
      updateHistoryPathname(params.id ? url.replace(`/${params.id}`, "") : url + "");
      resetEditView();
    }

    if (
      state.threeColumn
      && newSelection && newSelection.length
      && (!editRecord || !editRecord.id || editRecord.id.toString() !== newSelection[0])
    ) {
      updateHistoryPathname(params.id ? url.replace(`/${params.id}`, `/${newSelection[0]}`) : url + `/${newSelection[0]}`);
    }

    if (newSelection) updateSelection(newSelection);
  };

  const showConfirm = (props: ConfirmProps) => {

    const afterSubmitButtonHandler = () => {
      fullScreenEditView ? toggleFullWidthView(false) : onSelection(state.newSelection);
    };

    const confirmButton = (
      <Button
        classes={{
          root: "saveButtonEditView",
          disabled: "saveButtonEditViewDisabled"
        }}
        disabled={isInvalid}
        startIcon={isInvalid && <ErrorOutline color="error" />}
        variant="contained"
        color="primary"
        onClick={() => {
          submitForm();
          setTimeout(afterSubmitButtonHandler, 1000);
          closeConfirm();
        }}
      >
        {$t('save')}
      </Button>
    );

    if (!props.confirmMessage && !props.cancelButtonText) {
      openConfirm(
        {
          cancelButtonText: "DISCARD CHANGES",
          confirmCustomComponent: confirmButton,
          onCancelCustom: props.onConfirm
        },
      );
    } else {
      openConfirm(props);
    }
  };

  const setCreateNew = () => {
    updateHistoryPathname(params.id ? url.replace(`/${params.id}`, "/new") : url + "/new");
    updateSelection(["new"]);
    onInit();
  };

  const onCreateRecord = () => {
    if (customOnCreate) {
      if (typeof customOnCreate === "function") {
        customOnCreate(setCreateNew);
        return;
      }

      onInit();
      return;
    }

    setCreateNew();
  };

  useEffect(() => {
    setEntity(rootEntity);
    getCustomFieldTypes(rootEntity);

    sendGAEvent("screenview", `${rootEntity}ListView`);
    window.performance.mark("ListViewStart");

    getScripts();
    getListViewPreferences();

    if (location.search) {
      const searchParams = new URLSearchParams(location.search);
      const openShare = searchParams.has("openShare");

      if (openShare) {
        setTimeout(() => {
          setState({
            showExportDrawer: true
          });
        }, 1000);
        searchParams.delete("openShare");
      }

      history.replace({
        search: searchParams.toString(),
        pathname: url
      });
    }

    if (params.id === 'new') {
      onCreateRecord();
    }
    setState({ mounted: true });
  }, []);

  const getUrlSearch = searchParam => {
    if (searchParam.getAll("customSearch").length) {
      let customSearch = searchParam.getAll("customSearch")[0];

      const entityState = JSON.parse(LSGetItem(ENTITY_AQL_STORAGE_NAME)) as FindEntityState;
      if (entityState) {
        for (let i = 0; i < entityState.data.length; i++) {
          if (entityState.data[i].id === customSearch) {
            saveCategoryAQLLink({ AQL: "", id: customSearch, action: "remove" });
            customSearch = entityState.data[i].AQL;
          }
        }
      }
      return customSearch;
    }
    return searchParam.getAll("search")[0];
  };

  const onChangeFilters = (filters: FilterGroup[] | FormMenuTag[], type: 'filters' | 'tags' | 'checkedChecklists' | 'uncheckedChecklists') => {
    const searchParams = new URLSearchParams(location.search);

    if (type === "filters") {
      setFilterGroups(filters as FilterGroup[]);
      const filtersString = getFiltersNameString(filters as FilterGroup[]);
      if (filtersString) {
        searchParams.set("filter", filtersString);
      } else {
        searchParams.delete("filter");
      }
    }

    if (["tags", "checkedChecklists", "uncheckedChecklists"].includes(type)) {
      setListMenuTags({
        tags: menuTags,
        checkedChecklists,
        uncheckedChecklists,
        ...{ [type]: filters as FormMenuTag[] }
      });
      const tagsString = getActiveTags(filters as FormMenuTag[]).map(t => t.tagBody.id).toString();
      if (tagsString) {
        searchParams.set(type, tagsString);
      } else {
        searchParams.delete(type);
      }
    }
    const resultUrlSearchString = decodeURIComponent(searchParams.toString());
    updateHistorySearch(resultUrlSearchString ? "?" + resultUrlSearchString : "" );
  };

  const synchronizeAllFilters = () => {
    const searchParams = new URLSearchParams(location.search);
    const targetFilters = filterGroups.length ? [...filterGroupsInitial, ...filterGroups] : filterGroupsInitial || [];

    if (searchParams.size) {
      const listSearchString = getUrlSearch(searchParams);
      const tagsSearch = searchParams.get("tags");
      const filtersSearch = searchParams.get("filter");
      const checkedChecklistsUrlString = searchParams.get("checkedChecklists");
      const uncheckedChecklistsUrlString = searchParams.get("uncheckedChecklists");

      onChangeFilters(
        setActiveFiltersBySearch(filtersSearch, targetFilters),
        "filters"
      );

      // Sync tags by search
      if (tagsSearch && menuTags) {
        const tagIds = tagsSearch
          .split(",")
          .map(f => Number(f));
        onChangeFilters(getTagsUpdatedByIds(menuTags, tagIds), "tags");
      }

      // Sync checked checklists by search
      if (checkedChecklistsUrlString && checkedChecklists) {
        const ids = checkedChecklistsUrlString
          .split(",")
          .map(f => Number(f));
        onChangeFilters(getTagsUpdatedByIds(checkedChecklists, ids), "checkedChecklists");
      }

      // Sync unchecked checklists by search
      if (uncheckedChecklistsUrlString && uncheckedChecklists) {
        const ids = uncheckedChecklistsUrlString
          .split(",")
          .map(f =>   Number(f));
        onChangeFilters(getTagsUpdatedByIds(uncheckedChecklists, ids), "uncheckedChecklists");
      }

      if (listSearchString) {
        setListUserAQLSearch(`${listSearchString} `);
      }
    } else if (targetFilters) {
      onChangeFilters(
        targetFilters,
        "filters"
      );
    }

    setState({
      filtersSynchronized: true
    });
  };

  const onGetEditRecord = id => {
    sendGAEvent("screenview", `${rootEntity}EditView`);
    window.performance.mark("EditViewStart");
    getEditRecord(id);
  };

  const onQuerySearchChange = searchValue => {
    // reset scroll on records filtering
    if (containerNode.current) {
      containerNode.current.scrollTop = 0;
    }

    onSearch(searchValue);

    resetEditView();

    onSelection([]);
  };

  const updateDeleteCondition = val => {
    setState({
      deleteEnabled: val
    });
  };

  useEffect(() => {
    if (state.filtersSynchronized) {
      onSearch(userAQLSearch);
    }
  }, [
    state.filtersSynchronized
  ]);

  useEffect(() => {
    if (!state.filtersSynchronized && filterGroupsLoaded && (noListTags || menuTagsLoaded)) {
      synchronizeAllFilters();
    }
  }, [
    filterGroupsLoaded,
    menuTagsLoaded,
    noListTags,
    state.filtersSynchronized
  ]);

  useEffect(() => {
    setState({
      threeColumn: records.layout === "Three column"
    });
  }, [
    records.layout
  ]);

  useEffect(() => {
    setState({
      sidebarWidth: records.filterColumnWidth,
      mainContentWidth: getMainContentWidth(preferences[LISTVIEW_MAIN_CONTENT_WIDTH], records.filterColumnWidth)
    });
  }, [
    records.filterColumnWidth
  ]);

  useEffect(() => {
    if (window.performance.getEntriesByName("ListViewStart").length && !fetch.pending) {
      window.performance.mark("ListViewEnd");
      window.performance.measure("ListView", "ListViewStart", "ListViewEnd");
      sendGAEvent("timing", `${rootEntity}ListView`, window.performance.getEntriesByName("ListView")[0].duration);
      window.performance.clearMarks("ListViewStart");
      window.performance.clearMarks("ListViewEnd");
      window.performance.clearMeasures("ListView");
    }
  }, [
    fetch.pending
  ]);

  useEffect(() => {
    if (!fullScreenEditView && rootEntity) {
      document.title = `${customTabTitle || getEntityDisplayName(rootEntity)} (${records.filteredCount || 0} found)`;
      if (records.filteredCount === null) {
        document.title = `${customTabTitle || getEntityDisplayName(rootEntity)}`;
      }
    }
  }, [
    fullScreenEditView,
    records.filteredCount
  ]);

  useEffect(() => {
    if (state.mounted && params.id) {
      if (!editRecordFetching
      && !creatingNew
      && (!editRecord
        || !editRecord.id
        || editRecord.id.toString() !== params.id)
      && params.id !== "new"
      ) {
        setListEditRecordFetching();
        onGetEditRecord(params.id);

        if (!state.threeColumn && !fullScreenEditView) {
          toggleFullWidthView(true);
        }
      }
    }
  }, [
    params.id,
    editRecord?.id,
    state.mounted,
    state.threeColumn,
    editRecordFetching,
    creatingNew,
    fullScreenEditView
  ]);

  const prevSearch = usePrevious(search);

  useEffect(() => {
    if (state.filtersSynchronized && prevSearch !== search) {
      const currentUrlSearch = new URLSearchParams(location.search);
      const prevUrlSearch = new URLSearchParams(prevSearch);
      const filtersUrlString = currentUrlSearch.get("filter");
      const tagsUrlString = currentUrlSearch.get("tags");

      // Update AQL search by url
      if (search) {
        currentUrlSearch.set("search", encodeURIComponent(search));
      } else {
        currentUrlSearch.delete("search");
      }
      const resultUrlSearchString = decodeURIComponent(currentUrlSearch.toString());
      updateHistorySearch(resultUrlSearchString ? "?" + resultUrlSearchString : "" );

      // Update filters by url
      if (prevUrlSearch.get("filter") !== filtersUrlString) {
        const filtersString = getFiltersNameString(filterGroups);
        if (filtersString !== filtersUrlString) {
          onChangeFilters(setActiveFiltersBySearch(filtersUrlString, filterGroups), "filters");
        }
      }

      // Update tags by url
      if (prevUrlSearch.get("tags") !== tagsUrlString) {
        const activeString = getActiveTags(menuTags).map(t => t.tagBody.id).toString();
        if (activeString !== tagsUrlString) {
          const tagIds = tagsUrlString ? tagsUrlString
            .split(",")
            .map(f => Number(f)) : [];
          onChangeFilters(getTagsUpdatedByIds(menuTags, tagIds), "tags");
        }
      }
    }
  }, [search, location.search]);

  useEffect(() => {
    if (state.filtersSynchronized && !fetch.pending) {
      onQuerySearchChange(records.search);
    }
  }, [
    menuTags,
    filterGroups
  ]);

  useEffect(() => {
    if (selection.length && selection[0] !== "new" && typeof deleteDisabledCondition === "function") {
      updateDeleteCondition(!deleteDisabledCondition(props));
    }
  }, [
    selection,
    deleteDisabledCondition
  ]);

  useEffect(() => {
    if (!params.id && !state.threeColumn) return;
    if (!selection || selection[0] !== params.id) {
      onSelection(params.id ? [params.id] : []);
    }
  }, [
    params.id
  ]);

  useEffect(() => {
    setState({
      deleteEnabled: !defaultDeleteDisabled
    });
  }, [defaultDeleteDisabled]);

  const onSave = (val, dispatch, formProps) => {
    if (fetch.pending) {
      return;
    }

    let value = val;

    if (typeof preformatBeforeSubmit === "function") {
      value = preformatBeforeSubmit(val);
    }

    if (creatingNew) {
      onCreate(value);
      return;
    }

    if (typeof onBeforeSave === "function") {
      const argsObj = {
        onSave,
        formProps,
        onSaveArgs: [selection[0], value]
      };

      onBeforeSave(argsObj);
      return;
    }

    props.onSave(value);
  };

  const onDelete = id => {
    if (!deleteWithoutConfirmation) {
      openConfirm(
        {
          onConfirm: () => {
            props.onDelete(id);
          },
          confirmMessage: "Record will be permanently deleted. This action can not be undone",
          confirmButtonText: "DELETE"
        }
      );
    } else {
      props.onDelete(id);
    }
  };

  const onDeleteFilter = (id: number, entity: string, checked: boolean) => {
    updateSelection([]);
    deleteFilter(id, entity, checked);
  };

  const changeQueryView = querySearch => {
    setState({
      querySearch
    });
  };

  const toggleExportDrawer = () => {
    changeState(prevState => ({
      ...prevState,
      showExportDrawer: !prevState.showExportDrawer
    }));
  };

  const toggleBulkEditDrawer = () => {
    changeState(prevState => ({
      ...prevState,
      showBulkEditDrawer: !prevState.showBulkEditDrawer
    }));
  };

  const getContainerNode = node => {
    containerNode.current = node;
  };

  const onChangeModel = (model: TableModel, listUpdate?: boolean) => {
    if (records?.columns.length || model?.columns?.length) updateTableModel(model, listUpdate);
  };

  const checkDirty = (handler, args, reset?: boolean) => {
    if (isDirty && (records.layout === 'Two column' ? fullScreenEditView : true)) {
      showConfirm({
        onConfirm: () => {
          handler(...args);
          if (reset) {
            resetEditView();
          }
        }
      });
      return;
    }
    if (creatingNew && selection[0] === "new") {
      showConfirm({
        onConfirm: () => {
          handler(...args);
          if (reset) {
            resetEditView();
            setTimeout(onCreateRecord, 200);
          }
        }
      });
      return;
    }
    handler(...args);
  };

  const onRowDoubleClick = id => {
    updateHistoryPathname(params.id ? url.replace(`/${params.id}`, `/${id}`) : url + `/${id}`);

    if (state.threeColumn) {
      toggleFullWidthView(true);
    }
  };

  const switchLayout = () => {
    // eslint-disable-next-line react/no-access-state-in-setstate
    const updatedLayout = !state.threeColumn;
    const layout = updatedLayout ? "Three column" : "Two column";

    updateLayout(layout);
    updateSelection([]);
    resetEditView();

    if (params.id) {
      updateHistoryPathname(url.replace(`/${params.id}`, ""));
    }

    setTimeout(() => {
      if (records?.columns.length) updateTableModel({ layout });
    }, 500);

    setState({
      threeColumn: updatedLayout
    });
  };

  const handleResizeCallBack = (...resizeProps) => {
    const sidebarWidth = resizeProps[2].getClientRects()[0].width;

    setState({
      sidebarWidth
    });

    setTimeout(() => {
      if (records?.columns.length) updateTableModel({ filterColumnWidth: sidebarWidth });
    }, 500);
  };

  const handleResizeMainContentCallBack = (...resizeProps) => {
    const mainContentWidth = resizeProps[2].getClientRects()[0].width;

    setState({
      mainContentWidth
    });

    setTimeout(() => {
      setListviewMainContentWidth(String(mainContentWidth));
    }, 500);
  };

  const onCreateRecordWithDirtyCheck = onCreate
    ? (...args) => checkDirty(onCreateRecord, args, true)
    : undefined;

  const switchLayoutWithDirtyCheck = (...args) => checkDirty(switchLayout, args, true);

  const querySearchChangeWithDirtyCheck = (...args) => checkDirty(onQuerySearchChange, args, true);

  const onDeleteFilterWithDirtyCheck = (...args) => {
    const confirmMessage = args.length >= 4 && args[3]
      ? "The filter will be permanently deleted. This action cannot be undone"
      : "This filter is currently being shared with other users. The filter will be permanently deleted. This action cannot be undone";

    openConfirm(
      {
        onConfirm: () => checkDirty(onDeleteFilter, args, true),
        confirmMessage,
        confirmButtonText: 'DELETE'
      }
    );
  };

  const onChangeFiltersWithDirtyCheck = (...args) => checkDirty(onChangeFilters, args, true);

  const {
    querySearch, threeColumn, deleteEnabled, sidebarWidth, mainContentWidth, showExportDrawer, showBulkEditDrawer
  } = state;

  const hasFilters = Boolean(filterGroups.length || menuTags.length || savingFilter);

  const table = <ReactTableList
    {...listProps}
    mainContentWidth={mainContentWidth}
    onLoadMore={onLoadMore}
    selection={selection}
    records={records}
    threeColumn={threeColumn}
    shortCurrencySymbol={currency.shortCurrencySymbol}
    onRowDoubleClick={onRowDoubleClick}
    onSelectionChange={onSelection}
    onChangeModel={onChangeModel}
    getContainerNode={getContainerNode}
    sidebarWidth={sidebarWidth}
  />;

  return (
    <div className={classes.root}>
      <LoadingIndicator transparentBackdrop allowInteractions />

      <FullScreenEditView
        {...editViewProps}
        customTableModel={customTableModel}
        shouldAsyncValidate={shouldAsyncValidate}
        rootEntity={rootEntity}
        form={LIST_EDIT_VIEW_FORM_NAME}
        fullScreenEditView={fullScreenEditView}
        toogleFullScreenEditView={toggleFullWidthView}
        EditViewContent={EditViewContent}
        onSubmit={onSave}
        onSubmitFail={onSubmitFail}
        hasSelected={Boolean(selection.length)}
        creatingNew={creatingNew}
        updateDeleteCondition={updateDeleteCondition}
        showConfirm={showConfirm}
        threeColumn={threeColumn}
      />

      <ShareContainer
        showExportDrawer={showExportDrawer}
        toggleExportDrawer={toggleExportDrawer}
        count={records.filteredCount}
        selection={selection}
        rootEntity={rootEntity}
        sidebarWidth={hasFilters ? sidebarWidth : 0}
        AlertComponent={ShareContainerAlertComponent}
      />

      <BulkEditContainer
        showBulkEditDrawer={showBulkEditDrawer}
        toggleBulkEditDrawer={toggleBulkEditDrawer}
        count={records.filteredCount}
        selection={selection}
        rootEntity={rootEntity}
        sidebarWidth={hasFilters ? sidebarWidth : 0}
        manualLink={editViewProps.manualLink}
        getCustomBulkEditFields={getCustomBulkEditFields}
      />

      {hasFilters && (
        <ResizableWrapper
          ignoreScreenWidth
          onResizeStop={handleResizeCallBack}
          sidebarWidth={sidebarWidth}
          maxWidth="65%"
        >
          <ThemeProvider theme={sideBarTheme}>
            <SideBar
              fetching={fetching}
              savingFilter={savingFilter}
              onChangeFilters={onChangeFiltersWithDirtyCheck}
              filterGroups={filterGroups}
              rootEntity={rootEntity}
              filterEntity={filterEntity}
              deleteFilter={onDeleteFilterWithDirtyCheck}
            />
          </ThemeProvider>
        </ResizableWrapper>
      )}

      <div className="flex-fill d-flex flex-column overflow-hidden user-select-none">
        <div className="flex-fill d-flex relative">
          {threeColumn ? (
            <ResizableWrapper
              onResizeStop={handleResizeMainContentCallBack}
              sidebarWidth={mainContentWidth}
              ignoreScreenWidth
              minWidth="30%"
              maxWidth="65%"
              classes={{ sideBarWrapper: classes.resizableItemList }}
            >
              {table}
            </ResizableWrapper>
          ) : table }

          {threeColumn && !fullScreenEditView && (
            <div className="d-flex flex-fill overflow-hidden">
              <EditView
                {...editViewProps}
                customTableModel={customTableModel}
                shouldAsyncValidate={shouldAsyncValidate}
                form={LIST_EDIT_VIEW_FORM_NAME}
                rootEntity={rootEntity}
                EditViewContent={EditViewContent}
                onSubmitFail={onSubmitFail}
                onSubmit={onSave}
                hasSelected={Boolean(selection.length)}
                creatingNew={creatingNew}
                updateDeleteCondition={updateDeleteCondition}
                showConfirm={showConfirm}
                toogleFullScreenEditView={toggleFullWidthView}
              />
            </div>
          )}
        </div>
        <BottomAppBar
          dispatch={dispatch}
          findRelatedByFilter={findRelatedByFilter}
          getScripts={getScripts}
          scripts={scripts}
          emailTemplatesWithKeyCode={emailTemplatesWithKeyCode}
          createButtonDisabled={createButtonDisabled}
          searchMenuItemsRenderer={searchMenuItemsRenderer}
          querySearch={querySearch}
          threeColumn={threeColumn}
          deleteEnabled={deleteEnabled}
          showExportDrawer={showExportDrawer}
          toggleExportDrawer={toggleExportDrawer}
          showBulkEditDrawer={showBulkEditDrawer}
          toggleBulkEditDrawer={toggleBulkEditDrawer}
          filteredCount={records.filteredCount}
          rootEntity={rootEntity}
          fetch={fetch}
          selection={selection}
          hasShareTypes={pdfReports.length || exportTemplates.length}
          onDelete={onDelete}
          onQuerySearch={querySearchChangeWithDirtyCheck}
          changeQueryView={changeQueryView}
          switchLayout={switchLayoutWithDirtyCheck}
          onCreate={onCreateRecordWithDirtyCheck}
          findRelated={findRelated}
          CogwheelAdornment={CogwheelAdornment}
          showConfirm={showConfirm}
          CustomFindRelatedMenu={CustomFindRelatedMenu}
          records={records}
          searchComponentNode={searchComponentNode}
          searchQuery={searchQuery}
          scriptsFilterColumn={scriptsFilterColumn}
        />
      </div>
    </div>
  );
}

const mapStateToProps = (state: State) => ({
  fetch: state.fetch,
  currency: state.location.currency,
  isDirty: isDirty(LIST_EDIT_VIEW_FORM_NAME)(state),
  isInvalid: isInvalid(LIST_EDIT_VIEW_FORM_NAME)(state),
  syncErrors: getFormSyncErrors(LIST_EDIT_VIEW_FORM_NAME)(state),
  ...state.list,
  ...state.share,
  preferences: state.userPreferences,
});

const mapDispatchToProps = (dispatch: Dispatch<IAction>, ownProps) => ({
  dispatch,
  sendGAEvent: (event: GAEventTypes, screen: string, time?: number) => dispatch(pushGTMEvent(event, screen, time)),
  setEntity: entity => dispatch(setListEntity(entity)),
  resetEditView: () => {
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, null, false));
    dispatch(setListEditRecord(null));
  },
  updateSelection: (selection: string[]) => dispatch(setListSelection(selection)),
  updateLayout: (layout: LayoutType) => dispatch(setListLayout(layout)),
  deleteFilter: (id: number, entity: string, checked: boolean) => dispatch(deleteCustomFilter(id, entity, checked)),
  setFilterGroups: (filterGroups: FilterGroup[]) => dispatch(setFilterGroups(filterGroups)),
  setListMenuTags: ({ tags, checkedChecklists, uncheckedChecklists }) => dispatch(setListMenuTags(tags, checkedChecklists, uncheckedChecklists)),
  setListUserAQLSearch: (userAQLSearch: string) => dispatch(setListUserAQLSearch(userAQLSearch)),
  getScripts: () => dispatch(getScripts(ownProps.rootEntity)),
  getCustomFieldTypes: (entity: EntityName) => dispatch(getCustomFieldTypes(entity)),
  openConfirm: props => dispatch(showConfirm(props)),
  setListFullScreenEditView: (fullScreenEditView: boolean) => dispatch(setListFullScreenEditView(fullScreenEditView)),
  updateTableModel: (model: TableModel, listUpdate?: boolean) => dispatch(updateTableModel(ownProps.rootEntity, model, listUpdate)),
  onLoadMore: (stopIndex: number, resolve: any) => dispatch(getRecords(
    {
     entity: ownProps.rootEntity, listUpdate: true, ignoreSelection: false, stopIndex, resolve
    }
  )),
  onSearch: search => dispatch(setSearch(search, ownProps.rootEntity)),
  setListEditRecordFetching: () => dispatch(setListEditRecordFetching()),
  getListViewPreferences: () => dispatch(getUserPreferences([LISTVIEW_MAIN_CONTENT_WIDTH])),
  setListviewMainContentWidth: (value: string) => dispatch(setUserPreference({ key: LISTVIEW_MAIN_CONTENT_WIDTH, value })),
  submitForm: () => dispatch(submit(LIST_EDIT_VIEW_FORM_NAME)),
  closeConfirm: () => dispatch(closeConfirm()),
  findRelatedByFilter: (filter, list) => dispatch(findRelatedByFilter(filter, list))
});

const mergeProps = (stateToProps, dispatchToProps, ownProps) => {
  const dispatch = dispatchToProps.dispatch;
  const entityName = stateToProps.customTableModel || ownProps.rootEntity;

  return {
    ...stateToProps,
    ...dispatchToProps,
    ...ownProps,
    onCreate: (item: any) => dispatch(createEntityRecord(item, entityName)),
    onDelete: (id: number) => dispatch(deleteEntityRecord(id, entityName)),
    onSave: (item: any) => dispatch(updateEntityRecord(item.id, entityName, item)),
    getEditRecord: (id: number) => dispatch(getEntityRecord(id, entityName)),
  };
};

export default connect(
  mapStateToProps,
  mapDispatchToProps,
  mergeProps)(
    withStyles(
      withRouter<ListCompProps, typeof ListView>(ListView), {
  root: {
    position: "relative",
    display: "flex",
    flexDirection: 'row',
    width: "100vw",
    height: "100vh",
    overflow: "hidden"
  }
}));