/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Currency, ExportTemplate, LayoutType, Report, TableModel } from '@api/model';
import ErrorOutline from '@mui/icons-material/ErrorOutlineOutlined';
import { Button } from '@mui/material';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import $t from '@t';
import {
  AnyArgFunction,
  BooleanArgFunction,
  ConfirmProps,
  NoArgFunction,
  ResizableWrapper,
  ShowConfirmCaller
} from 'ish-ui';
import React, { useEffect, useMemo, useRef } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';
import { withRouter } from 'react-router-dom';
import { Dispatch } from 'redux';
import { getFormSyncErrors, initialize, isDirty, isInvalid, reset, submit } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import {
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
  ListAqlMenuItemsRenderer,
  ListQueryPayload,
  ListUrlQuery
} from '../../../model/common/ListView';
import { EntityName } from '../../../model/entities/common';
import { FormMenuTag } from '../../../model/tags';
import { State } from '../../../reducers/state';
import { closeConfirm, getScripts, getUserPreferences, setUserPreference, showConfirm } from '../../actions';
import { IAction } from '../../actions/IshAction';
import { UserPreferencesState } from '../../reducers/userPreferencesReducer';
import { getEntityDisplayName } from '../../utils/getEntityDisplayName';
import { onSubmitFail } from '../../utils/highlightFormErrors';
import { pushGTMEvent } from '../google-tag-manager/actions';
import { GAEventTypes } from '../google-tag-manager/services/GoogleAnalyticsService';
import LoadingIndicator from '../progress/LoadingIndicator';
import {
  deleteCustomFilter,
  findRelatedByFilter,
  getRecords,
  setListEditRecord,
  setListEditRecordFetching,
  setListEntity,
  setListFullScreenEditView,
  setListLayout,
  setListQuery,
  setListSelection,
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
  getTagsUpdatedByIds,
  getTagsUpdatedByIdsWithIndeterminate,
  setActiveFiltersBySearch
} from './utils/listFiltersUtils';
import { shouldAsyncValidate } from './utils/listFormUtils';
import {
  buildListUrlSearch,
  expandAqlSearch,
  getListUrlQuery,
  hasListUrlQuery,
  isSameListUrlQuery,
  parseListUrlSearch,
  parseTagSelection,
  removeUrlParams,
  resolveCustomSearch
} from './utils/listSearchUtils';

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
  setListQuery?: (payload: Omit<ListQueryPayload, 'entity'>) => void;
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
  deleteFilter?: (id: number, entity: string, checked: boolean) => void;
  exportTemplates?: ExportTemplate[];
  pdfReports?: Report[];
  updateLayout?: (layout: LayoutType) => void;
  updateSelection?: (selection: string[]) => void;
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

type ListCompProps = Props & OwnProps & State["list"] & State["share"] & RouteComponentProps<any>;

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
    deleteDisabledCondition,
    menuTagsLoaded,
    filterGroupsLoaded,
    noListTags,
    preferences,
    checkedChecklists,
    uncheckedChecklists,
    customTabTitle,
    setListviewMainContentWidth,
    setListQuery,
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

  // whether the next url write records a step the user took, rather than tidying up the url they
  // are already on. Set by whoever changes the query, read once by the write that follows it.
  const pushNextUrlUpdate = useRef(false);

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

  /**
   * The url is a projection of the list state, not a second copy of it: it is always rebuilt
   * from the store in one write.
   *
   * Filtering, tagging and searching are steps the user took and can come back from, so they
   * push. Reconciliation writes - the first sync, canonicalising a query read out of the url,
   * dropping a spent one shot param - describe an url the user is already on and must replace,
   * or back would land on an url that immediately reconciles itself into a new entry and trap
   * them on the list.
   */
  const updateHistorySearch = (query: ListUrlQuery) => {
    const push = pushNextUrlUpdate.current;
    pushNextUrlUpdate.current = false;

    if (isSameListUrlQuery(parseListUrlSearch(window.location.search), query)) {
      return;
    }

    const target = {
      search: buildListUrlSearch(window.location.search, query),
      pathname: window.location.pathname
    };

    if (push) {
      history.push(target);
    } else {
      history.replace(target);
    }
  };

  useEffect(() => {
    if (
      !fullScreenEditView
      && params.id
      && (state.threeColumn
        ? (params.id === 'new' && alwaysFullScreenCreateView && !customOnCreate)
        : (params.id !== 'new' || !customOnCreate)
      )) {
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

    if (new URLSearchParams(window.location.search).has("openShare")) {
      setTimeout(() => {
        setState({
          showExportDrawer: true
        });
      }, 1000);

      history.replace({
        search: removeUrlParams(window.location.search, ["openShare"]),
        pathname: url
      });
    }

    if (params.id === 'new') {
      onCreateRecord();
    }
    setState({ mounted: true });
  }, []);

  /**
   * What the url would look like for the current store state. Everything that writes the url
   * goes through this, so the url can never claim something the request did not use.
   */
  const listUrlQuery = useMemo(
    () => getListUrlQuery({
      userAQLSearch, filterGroups, menuTags, checkedChecklists, uncheckedChecklists
    }),
    [userAQLSearch, filterGroups, menuTags, checkedChecklists, uncheckedChecklists]
  );

  /**
   * `fromUrl` marks a query the user did not trigger from this screen (back/forward, a pasted
   * link): the record they have open is not theirs to close, and the url it came from is already
   * in the history, so the write that follows must not add another entry for it.
   */
  const applyListQuery = (query: Omit<ListQueryPayload, 'entity'>, fromUrl?: boolean) => {
    // reset scroll on records filtering
    if (containerNode.current) {
      containerNode.current.scrollTop = 0;
    }

    pushNextUrlUpdate.current = !fromUrl;

    setListQuery(query);

    if (!fromUrl) {
      resetEditView();

      onSelection([]);
    }
  };

  const onChangeFilters = (filters: FilterGroup[] | FormMenuTag[], type: 'filters' | 'tags' | 'checkedChecklists' | 'uncheckedChecklists') => {
    applyListQuery(type === "filters"
      ? { filterGroups: filters as FilterGroup[] }
      : { [type === "tags" ? "menuTags" : type]: filters as FormMenuTag[] });
  };

  /**
   * Turns a url query into store state. Used both for the first render and for any later url
   * change the list did not make itself (back/forward, a pasted link).
   */
  const getQueryFromUrl = (urlQuery: ListUrlQuery, targetFilters: FilterGroup[]): Omit<ListQueryPayload, 'entity'> => ({
    userAQLSearch: urlQuery.search,
    search: expandAqlSearch(urlQuery.search, targetFilters),
    filterGroups: setActiveFiltersBySearch(urlQuery.filter, targetFilters),
    menuTags: getTagsUpdatedByIdsWithIndeterminate(menuTags, parseTagSelection(urlQuery.tags)),
    checkedChecklists: getTagsUpdatedByIds(checkedChecklists, parseTagSelection(urlQuery.checkedChecklists)),
    uncheckedChecklists: getTagsUpdatedByIds(uncheckedChecklists, parseTagSelection(urlQuery.uncheckedChecklists))
  });

  const synchronizeAllFilters = () => {
    const searchParams = new URLSearchParams(window.location.search);
    const targetFilters = filterGroups.length ? [...filterGroupsInitial, ...filterGroups] : [...filterGroupsInitial];

    // one shot `customSearch` links are resolved and dropped here, before anything reads the url
    const hadCustomSearch = searchParams.has("customSearch");
    const customSearch = resolveCustomSearch(searchParams);
    const urlQuery = parseListUrlSearch(searchParams);

    if (hadCustomSearch) {
      if (customSearch) {
        urlQuery.search = customSearch;
      }
      // the link is spent - put the expression it resolved to in its place straight away
      history.replace({
        search: buildListUrlSearch(window.location.search, urlQuery),
        pathname: window.location.pathname
      });
    }

    // an url carrying no list params at all is an unfiltered entry point, so the entity defaults
    // apply; an url carrying any of them describes the query in full, absent params included
    const query = hasListUrlQuery(searchParams) || customSearch
      ? getQueryFromUrl(urlQuery, targetFilters)
      : {
        userAQLSearch: "",
        search: "",
        filterGroups: targetFilters,
        menuTags,
        checkedChecklists,
        uncheckedChecklists
      };

    setListQuery(query);

    setState({
      filtersSynchronized: true
    });
  };

  const onGetEditRecord = id => {
    sendGAEvent("screenview", `${rootEntity}EditView`);
    window.performance.mark("EditViewStart");
    getEditRecord(id);
  };

  /**
   * `expression` is the AQL actually sent to the server, `value` the text the user typed. Both
   * are stored, so the url can keep the readable text while the request keeps the expanded one.
   */
  const onQuerySearchChange = (expression: string, value?: string) => {
    applyListQuery({
      search: expression,
      userAQLSearch: value === undefined ? expression : value
    });
  };

  const updateDeleteCondition = val => {
    setState({
      deleteEnabled: val
    });
  };

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
    if (!fullScreenEditView && isDirty && !state.threeColumn) {
      dispatch(reset(LIST_EDIT_VIEW_FORM_NAME));
    }
  }, [
    isDirty,
    fullScreenEditView
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

  // state -> url. The store is the source of truth, the url only mirrors it.
  useEffect(() => {
    if (state.filtersSynchronized) {
      updateHistorySearch(listUrlQuery);
    }
  }, [
    state.filtersSynchronized,
    listUrlQuery
  ]);

  // url -> state, for url changes the list did not make itself: back/forward, a pasted link.
  //
  // The router `location` only triggers this; the url is read from `window.location`, the same
  // live value the effect above writes and compares against. Reading the prop instead would put
  // the two directions one commit out of phase - the prop still holds the pre-write url -
  // and they would spend forever undoing each other.
  //
  // Must stay declared after the effect above: within one commit the store has to reconcile the
  // url before this reads it, otherwise a store change looks like an external url change.
  useEffect(() => {
    if (!state.filtersSynchronized) {
      return;
    }

    const urlQuery = parseListUrlSearch(window.location.search);

    if (!isSameListUrlQuery(urlQuery, listUrlQuery)) {
      applyListQuery(getQueryFromUrl(urlQuery, filterGroups), true);
    }
  }, [
    location.search,
    state.filtersSynchronized
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
                showConfirm={showConfirm}
                toogleFullScreenEditView={toggleFullWidthView}
                threeColumn={threeColumn}
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
  setListQuery: (payload: Omit<ListQueryPayload, 'entity'>) =>
    dispatch(setListQuery({ entity: ownProps.rootEntity, ...payload })),
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
})) as React.FC<Props>;