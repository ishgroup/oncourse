/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { withRouter } from "react-router-dom";
import { getFormSyncErrors, initialize, isDirty, isInvalid, submit } from "redux-form";
import clsx from "clsx";
import { ThemeProvider } from "@mui/material/styles";
import { createStyles, withStyles } from "@mui/styles";
import Grid from "@mui/material/Grid";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { Column, Currency, ExportTemplate, LayoutType, Report, SearchQuery, TableModel } from "@api/model";
import { createTheme } from '@mui/material';
import ErrorOutline from "@mui/icons-material/ErrorOutline";
import Button from "@mui/material/Button";
import { UserPreferencesState } from "../../reducers/userPreferencesReducer";
import { onSubmitFail } from "../../utils/highlightFormClassErrors";
import SideBar from "./components/side-bar/SideBar";
import BottomAppBar from "./components/bottom-app-bar/BottomAppBar";
import EditView from "./components/edit-view/EditView";
import ShareContainer from "./components/share/ShareContainer";
import BulkEditContainer from "./components/bulk-edit/BulkEditContainer";
import { State } from "../../../reducers/state";
import { Fetch } from "../../../model/common/Fetch";
import LoadingIndicator from "../layout/LoadingIndicator";
import FullScreenEditView from "./components/full-screen-edit-view/FullScreenEditView";
import {
  clearListState,
  deleteCustomFilter,
  getListNestedEditRecord,
  getRecords,
  setFilterGroups,
  setListColumns,
  setListCreatingNew,
  setListEditRecord,
  setListEditRecordFetching,
  setListEntity,
  setListFullScreenEditView,
  setListLayout,
  setListMenuTags,
  setListSelection,
  setListUserAQLSearch,
  setSearch,
  setShowColoredDots,
  updateTableModel,
} from "./actions";
import NestedEditView from "./components/full-screen-edit-view/NestedEditView";
import { closeConfirm, getScripts, getUserPreferences, setUserPreference, showConfirm } from "../../actions";
import ResizableWrapper from "../layout/resizable/ResizableWrapper";
import { MenuTag } from "../../../model/tags";
import { pushGTMEvent } from "../google-tag-manager/actions";
import { GAEventTypes } from "../google-tag-manager/services/GoogleAnalyticsService";
import {
  AnyArgFunction,
  BooleanArgFunction,
  NoArgFunction,
  StringArgFunction
} from "../../../model/common/CommonFunctions";
import {
  EditViewContainerProps,
  FilterGroup,
  FindRelatedItem,
  ListAqlMenuItemsRenderer,
  ListState
} from "../../../model/common/ListView";
import { LIST_EDIT_VIEW_FORM_NAME } from "./constants";
import { getEntityDisplayName } from "../../utils/getEntityDisplayName";
import { ENTITY_AQL_STORAGE_NAME, LISTVIEW_MAIN_CONTENT_WIDTH } from "../../../constants/Config";
import { ConfirmProps, ShowConfirmCaller } from "../../../model/common/Confirm";
import { EntityName, FindEntityState } from "../../../model/entities/common";
import { saveCategoryAQLLink } from "../../utils/links";
import ReactTableList, { ListProps } from "./components/list/ReactTableList";
import { getActiveTags, getFiltersNameString, getTagsUpdatedByIds } from "./utils/listFiltersUtils";
import { setSwipeableDrawerDirtyForm } from "../layout/swipeable-sidebar/actions";
import { LSGetItem } from "../../utils/storage";

export const ListSideBarDefaultWidth = 200;
export const ListMainContentDefaultWidth = 774;

const styles = () => createStyles({
  gridWithEditColumn: {
    minHeight: 0
  },
  editViewColumn: {
    borderLeft: "1px solid rgba(0,0,0,0.1)",
    maxHeight: "100%"
  },
  threeColumnList: {
    width: "200px",
    position: "unset"
  },
  resizableItemList: {
    "& > div > div > div > div": {
      width: "100% !important",
      "& > div": {
        width: "100% !important"
      }
    }
  }
});

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

interface Props extends Partial<ListState> {
  listProps: ListProps;
  getEditRecord: (id: string) => void;
  rootEntity: EntityName;
  EditViewContent: any;
  onLoadMore?: (startIndex: number, stopIndex: number, resolve: AnyArgFunction) => void;
  updateTableModel?: (model: TableModel, listUpdate?: boolean) => void;
  aqlEntity?: string;
  selection?: string[];
  editRecord?: any;
  onSave?: any;
  onBeforeSave?: any;
  onDelete?: any;
  onCreate?: any;
  classes?: any;
  isDirty?: boolean;
  isInvalid?: boolean;
  fullScreenEditView?: boolean;
  fetching?: boolean;
  savingFilter?: any;
  defaultDeleteDisabled?: boolean;
  createButtonDisabled?: boolean;
  nestedEditFields?: { [key: string]: (props: any) => React.ReactNode };
  fetch?: Fetch;
  menuTags?: MenuTag[];
  filterEntity?: EntityName;
  filterGroups?: FilterGroup[];
  filterGroupsInitial?: FilterGroup[];
  onSearch?: StringArgFunction;
  setFilterGroups?: (filterGroups: FilterGroup[]) => void;
  setListMenuTags?: (tags: MenuTag[]) => void;
  deleteFilter?: (id: number, entity: string, checked: boolean) => void;
  exportTemplates?: ExportTemplate[];
  pdfReports?: Report[];
  updateLayout?: (layout: LayoutType) => void;
  updateColumns?: (columns: Column[]) => void;
  updateSelection?: (selection: string[]) => void;
  setListUserAQLSearch?: (userAQLSearch: string) => void;
  getScripts?: NoArgFunction;
  openNestedEditView?: (entity: string, id: number, threeColumn?: boolean) => void;
  openConfirm?: ShowConfirmCaller;
  resetEditView?: NoArgFunction;
  clearListState?: NoArgFunction;
  onInit?: NoArgFunction;
  findRelated?: FindRelatedItem[];
  setListCreatingNew?: BooleanArgFunction;
  setListFullScreenEditView?: BooleanArgFunction;
  editViewProps?: {
    nameCondition?: EditViewContainerProps["nameCondition"];
    manualLink?: EditViewContainerProps["manualLink"];
    validate?: any;
    asyncValidate?: any;
    shouldAsyncValidate?: AnyArgFunction<boolean>;
    asyncBlurFields?: string[];
    asyncChangeFields?: string[];
    hideFullScreenAppBar?: EditViewContainerProps["hideFullScreenAppBar"];
    disabledSubmitCondition?: boolean;
    enableReinitialize?: boolean;
    keepDirtyOnReinitialize?: boolean;
    hideTitle?: boolean;
  };
  CogwheelAdornment?: React.ReactNode;
  location?: any;
  history?: any;
  match?: any;
  sendGAEvent?: (event: GAEventTypes, screen: string, time?: number) => void;
  alwaysFullScreenCreateView?: any;
  currency?: Currency;
  CustomFindRelatedMenu?: React.ReactNode;
  ShareContainerAlertComponent?: any;
  searchMenuItemsRenderer?: ListAqlMenuItemsRenderer;
  customOnCreate?: any;
  preformatBeforeSubmit?: AnyArgFunction;
  userAQLSearch?: string;
  listSearch?: string;
  creatingNew?: boolean;
  showColoredDots?: boolean;
  editRecordFetching?: boolean;
  recordsLeft?: number;
  searchQuery?: SearchQuery;
  setListEditRecordFetching?: any;
  search?: string;
  onSwipeableDrawerDirtyForm?: (isDirty: boolean, resetEditView: any) => void;
  deleteDisabledCondition?: (props) => boolean;
  noListTags?: boolean;
  setEntity?: (entity: EntityName) => void;
  getListViewPreferences?: () => void;
  preferences?: UserPreferencesState;
  setListviewMainContentWidth?: (value: string) => void;
  submitForm?: any;
  closeConfirm?: () => void;
  setShowColoredDots?: (value: boolean) => void;
  deleteWithoutConfirmation?: boolean;
  getCustomBulkEditFields?: any;
}

interface ComponentState {
  showExportDrawer: boolean;
  showBulkEditDrawer: boolean;
  querySearch: boolean;
  deleteEnabled: boolean;
  threeColumn: boolean;
  sidebarWidth: number;
  mainContentWidth: number;
  newSelection: string[] | null;
}

class ListView extends React.PureComponent<Props, ComponentState> {
  private containerNode;

  private searchComponentNode: React.RefObject<any>;

  private ignoreCheckDirtyOnSelection: boolean;

  private filtersSynchronized: boolean = false;

  constructor(props) {
    super(props);

    this.searchComponentNode = React.createRef();

    this.state = {
      showExportDrawer: false,
      showBulkEditDrawer: false,
      querySearch: false,
      deleteEnabled: !props.defaultDeleteDisabled,
      threeColumn: false,
      sidebarWidth: ListSideBarDefaultWidth,
      mainContentWidth: this.getMainContentWidth(ListMainContentDefaultWidth, ListSideBarDefaultWidth),
      newSelection: null,
    };
  }

  componentDidMount() {
    const {
      getScripts,
      history,
      sendGAEvent,
      rootEntity,
      setEntity,
      match: { url },
      filterGroupsInitial = [],
      getListViewPreferences
    } = this.props;

    setEntity(rootEntity);

    sendGAEvent("screenview", `${rootEntity}ListView`);
    window.performance.mark("ListViewStart");

    getScripts();
    getListViewPreferences();

    if (this.props.location.search) {
      filterGroupsInitial.forEach(g => {
        g.filters.forEach(f => {
          f.active = false;
        });
      });

      const searchParams = new URLSearchParams(this.props.location.search);
      const openShare = searchParams.has("openShare");

      if (openShare) {
        setTimeout(() => {
          this.setState({
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
  }

  componentDidUpdate(prevProps) {
    const {
      records,
      selection,
      fetch,
      sendGAEvent,
      rootEntity,
      defaultDeleteDisabled,
      filterGroups,
      menuTags,
      match: { params, url },
      location,
      search,
      fullScreenEditView,
      editRecord,
      editRecordFetching,
      setListEditRecordFetching,
      creatingNew,
      resetEditView,
      setListUserAQLSearch,
      setListCreatingNew,
      deleteDisabledCondition,
      menuTagsLoaded,
      filterGroupsLoaded,
      noListTags,
      preferences,
      showColoredDots,
    } = this.props;

    const { threeColumn } = this.state;

    if (!this.filtersSynchronized && filterGroupsLoaded && (noListTags || menuTagsLoaded)) {
      this.synchronizeAllFilters();
    }

    if (prevProps.selection.length && !selection.length && params.id) {
      this.updateHistory(url.replace(`/${params.id}`, ""), location.search);
    }

    if (prevProps.defaultDeleteDisabled !== defaultDeleteDisabled) {
      this.updateDeleteCondition(!defaultDeleteDisabled);
    }

    if (records.layout && !prevProps.records.layout) {
      this.setState({
        threeColumn: records.layout === "Three column"
      });

      this.setState({
        sidebarWidth: records.filterColumnWidth
      });

      this.setState({
        mainContentWidth: this.getMainContentWidth(preferences[LISTVIEW_MAIN_CONTENT_WIDTH], records.filterColumnWidth)
      });
    }

    if (window.performance.getEntriesByName("ListViewStart").length && prevProps.fetch.pending && !fetch.pending) {
      window.performance.mark("ListViewEnd");
      window.performance.measure("ListView", "ListViewStart", "ListViewEnd");
      sendGAEvent("timing", `${rootEntity}ListView`, window.performance.getEntriesByName("ListView")[0].duration);
      window.performance.clearMarks("ListViewStart");
      window.performance.clearMarks("ListViewEnd");
      window.performance.clearMeasures("ListView");
    }

    if (!fullScreenEditView && rootEntity) {
      document.title = `${getEntityDisplayName(rootEntity)} (${records.filteredCount || 0} found)`;
      if (records.filteredCount === null) {
        document.title = `${getEntityDisplayName(rootEntity)}`;
      }
    }

    if (params.id && creatingNew && params.id !== "new") {
      setListCreatingNew(false);
    }

    // saving entity id in browser history
    if (params.id
      && !editRecordFetching
      && !creatingNew
      && (!editRecord
        || !editRecord.id
        || editRecord.id.toString() !== params.id)
        ) {
      const isNew = params.id === "new";

      if (!isNew) {
        setListEditRecordFetching();
        this.onGetEditRecord(params.id);

        if (!threeColumn) {
          this.toggleFullWidthView();
        }
      } else if (prevProps.records.layout) {
        this.onCreateRecord();
      }

      if (!selection.includes(params.id)) {
        this.ignoreCheckDirtyOnSelection = true;
        this.onSelection([params.id]);
      }
    }

    if (prevProps.match.params.id && !params.id) {
      resetEditView();
      if (!threeColumn && (fullScreenEditView || creatingNew)) {
        this.toggleFullWidthView();
      }
      if (threeColumn) {
        this.ignoreCheckDirtyOnSelection = true;
        this.onSelection([]);
      }
    }

    if (prevProps.search !== search) {
      const currentUrlSearch = new URLSearchParams(location.search);
      if (search) {
        currentUrlSearch.set("search", encodeURIComponent(search));
      } else {
        currentUrlSearch.delete("search");
      }
      const resultErlSearchString = decodeURIComponent(currentUrlSearch.toString());
      this.updateHistory(url, resultErlSearchString ? "?" + resultErlSearchString : "" );
    }

    if (prevProps.location.search !== location.search) {
      const prevUrlSearch = new URLSearchParams(prevProps.location.search);
      const currentUrlSearch = new URLSearchParams(location.search);

      const filtersUrlString = currentUrlSearch.get("filter");
      const tagsUrlString = currentUrlSearch.get("tag");
      const searchString = currentUrlSearch.get("search");

      if (prevUrlSearch.get("filter") !== filtersUrlString) {
        const filtersString = getFiltersNameString(filterGroups);
        if (filtersString !== filtersUrlString) {
          const updated = [...filterGroups];
          this.setActiveFiltersBySearch(filtersUrlString, filterGroups);
          this.onChangeFilters(updated, "filters");
        }
      }

      if (prevUrlSearch.get("tag") !== tagsUrlString) {
        const tagsString = getActiveTags(menuTags).map(t => t.tagBody.id).toString();

        if (tagsString !== tagsUrlString) {
          const tagIds = tagsUrlString ? tagsUrlString
            .split(",")
            .map(f => Number(f)) : [];
          this.onChangeFilters(getTagsUpdatedByIds(menuTags, tagIds), "tags");
        }
      }

      if (prevUrlSearch.get("search") !== searchString) {
        this.onQuerySearchChange(searchString);
        setListUserAQLSearch(searchString);
      }
    }

    if (!fetch.pending && ((prevProps.menuTags.length && prevProps.menuTags !== menuTags)
      || (prevProps.filterGroups.length && prevProps.filterGroups !== filterGroups)
    )) {
      this.onQuerySearchChange(records.search);
    }

    if (selection.length && selection[0] !== "new" && typeof deleteDisabledCondition === "function") {
      this.updateDeleteCondition(!deleteDisabledCondition(this.props));
    }
  }

  componentWillUnmount() {
    this.props.clearListState();
  }

  setActiveFiltersBySearch = (search: string, filters: FilterGroup[]) => {
    const filterNames = search ? search.replace(/[@_]/g, " ")
        .split(",")
        .map(f => f.trim()) : [];
    filters.forEach(g => {
        g.filters.forEach(f => {
          // eslint-disable-next-line no-param-reassign
          f.active = filterNames.includes(f.name);
        });
      });
  };

  synchronizeAllFilters = () => {
    const {
      setListUserAQLSearch,
      filterGroupsInitial = [],
      filterGroups,
      menuTags,
      onSearch
    } = this.props;

    const targetFilters = filterGroups.length ? [...filterGroupsInitial, ...filterGroups] : filterGroupsInitial || [];

    if (this.props.location.search) {
      const searchParams = new URLSearchParams(this.props.location.search);

      const search = this.getUrlSearch(searchParams);
      const filtersSearch = searchParams.get("filter");
      const tagsSearch = searchParams.get("tag");

      if (filtersSearch) {
        this.setActiveFiltersBySearch(filtersSearch, targetFilters);
      }
      this.onChangeFilters(targetFilters, "filters");

      if (tagsSearch && menuTags) {
        const tagIds = tagsSearch
          .split(",")
          .map(f => Number(f));
        this.onChangeFilters(getTagsUpdatedByIds(menuTags, tagIds), "tags");
      }

      if (search) {
        setListUserAQLSearch(`${search} `);
        onSearch(search);
      } else {
        onSearch("");
      }
    } else {
      if (targetFilters) {
        this.onChangeFilters(targetFilters, "filters");
      }
      onSearch("");
    }

    this.filtersSynchronized = true;
  };

  onGetEditRecord = (id: string) => {
    const { sendGAEvent, getEditRecord, rootEntity } = this.props;

    sendGAEvent("screenview", `${rootEntity}EditView`);
    window.performance.mark("EditViewStart");

    getEditRecord(id);
  };

  onQuerySearchChange = searchValue => {
    const { match: { params, url }, location: { search } } = this.props;

    // reset scroll on records filtering
    if (this.containerNode) {
      this.containerNode.scrollTop = 0;
    }

    const {
     onSearch, resetEditView
    } = this.props;

    if (params.id) {
      this.updateHistory( url.replace(`/${params.id}`, "" ), search);
    }

    this.onSelection([]);

    onSearch(searchValue);

    resetEditView();

    this.ignoreCheckDirtyOnSelection = true;
  };

  onSelection = newSelection => {
    const {
      isDirty,
      updateSelection,
      selection,
      creatingNew,
      editRecord,
      setListCreatingNew,
      resetEditView,
      match: { url, params },
      location: { search }
    } = this.props;

    const { threeColumn } = this.state;

    if ((isDirty || (creatingNew && selection[0] === "new")) && !this.ignoreCheckDirtyOnSelection) {
      this.setState({ newSelection });
      this.showConfirm(
        {
          onConfirm: () => {
            this.ignoreCheckDirtyOnSelection = true;
            this.onSelection(newSelection);
            if (isDirty) {
              resetEditView();
            }
          }
        },
        );
      return;
    }

    if (creatingNew) {
      setListCreatingNew(false);
    }

    if (newSelection && !newSelection.length) {
      this.updateHistory(params.id ? url.replace(`/${params.id}`, "") : url + "", search );
      resetEditView();
    }

    if (
      threeColumn
      && newSelection && newSelection.length
      && (!editRecord || !editRecord.id || editRecord.id.toString() !== newSelection[0])
    ) {
      this.updateHistory(params.id ? url.replace(`/${params.id}`, `/${newSelection[0]}`) : url + `/${newSelection[0]}`, search);
    }

    if (this.ignoreCheckDirtyOnSelection) {
      this.ignoreCheckDirtyOnSelection = false;
    }

    if (newSelection) updateSelection(newSelection);
  };

  onChangeFilters = (filters: FilterGroup[] | MenuTag[], type: string) => {
    const {
     setFilterGroups, setListMenuTags, location: { search }, match: { url }
    } = this.props;

    const searchParams = new URLSearchParams(search);

    if (type === "filters") {
      setFilterGroups(filters as FilterGroup[]);
      const filtersString = getFiltersNameString(filters as FilterGroup[]);
      if (filtersString) {
        searchParams.set("filter", filtersString);
      } else {
        searchParams.delete("filter");
      }
    }

    if (type === "tags") {
      setListMenuTags(filters as MenuTag[]);
      const tagsString = getActiveTags(filters as MenuTag[]).map(t => t.tagBody.id).toString();
      if (tagsString) {
        searchParams.set("tag", tagsString);
      } else {
        searchParams.delete("tag");
      }
    }

    const resultErlSearchString = decodeURIComponent(searchParams.toString());
    this.updateHistory(url, resultErlSearchString ? "?" + resultErlSearchString : "" );
  };

  onSave = (val, dispatch, formProps) => {
    const {
      onCreate, onSave, selection, onBeforeSave, preformatBeforeSubmit, creatingNew, fetch: { pending }
    } = this.props;

    if (pending) {
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

    onSave(selection[0], value);
  };

  onDelete = id => {
    const { openConfirm, onDelete, deleteWithoutConfirmation } = this.props;

    if (!deleteWithoutConfirmation) {
      openConfirm(
        {
          onConfirm: () => {
            onDelete(id);
          },
          confirmMessage: "Record will be permanently deleted. This action can not be undone",
          confirmButtonText: "DELETE"
        }
      );
    } else {
      onDelete(id);
    }
  };

  onDeleteFilter = (id: number, entity: string, checked: boolean) => {
    this.props.updateSelection([]);
    this.props.deleteFilter(id, entity, checked);
  };

  setCreateNew = () => {
    const {
     onInit, setListCreatingNew, updateSelection, match: { params, url }, location: { search }
    } = this.props;

    this.updateHistory(params.id ? url.replace(`/${params.id}`, "/new") : url + "/new", search);

    setListCreatingNew(true);
    updateSelection(["new"]);
    onInit();
  };

  onCreateRecord = () => {
    const { onInit, customOnCreate } = this.props;

    if (customOnCreate) {
      if (typeof customOnCreate === "function") {
        customOnCreate(this.setCreateNew);
        return;
      }

      onInit();
      return;
    }

    this.setCreateNew();
  };

  changeQueryView = querySearch => {
    this.setState({
      querySearch
    });
  };

  toggleExportDrawer = () => {
    this.setState(prev => ({ showExportDrawer: !prev.showExportDrawer }));
  };

  toggleBulkEditDrawer = () => {
    this.setState(prev => ({ showBulkEditDrawer: !prev.showBulkEditDrawer }));
  };

  getContainerNode = node => {
    this.containerNode = node;
  };

  onChangeModel = (model: TableModel, listUpdate?: boolean) => {
    if (this.props.records?.columns.length || model?.columns?.length) this.props.updateTableModel(model, listUpdate);
  };

  checkDirty = (handler, args, reset?: boolean) => {
    const { isDirty, selection, creatingNew } = this.props;
    if (isDirty || (creatingNew && selection[0] === "new")) {
      this.showConfirm({
        onConfirm: () => {
          handler(...args);
          if (reset && this.props.isDirty) {
            this.props.resetEditView();
          }
        }
      });
      return;
    }
    handler(...args);
  };

  showConfirm = (props: ConfirmProps) => {
    const {
     closeConfirm, openConfirm, isInvalid, fullScreenEditView, submitForm
    } = this.props;

    const afterSubmitButtonHandler = () => {
      fullScreenEditView ? this.toggleFullWidthView() : this.onSelection(this.state.newSelection);
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
          this.ignoreCheckDirtyOnSelection = true;
          setTimeout(afterSubmitButtonHandler, 1000);
          closeConfirm();
        }}
      >
        SAVE
      </Button>
    );

    if (!props.confirmMessage && !props.cancelButtonText) {
      openConfirm(
        {
          cancelButtonText: "DISCARD CHANGES",
          confirmCustomComponent: confirmButton,
          onCancel: props.onConfirm
        },
      );
    } else {
      openConfirm(props);
    }
  };

  toggleFullWidthView = () => {
    const {
      alwaysFullScreenCreateView,
      updateSelection,
      resetEditView,
      creatingNew,
      setListCreatingNew,
      fullScreenEditView,
      setListFullScreenEditView,
      match: { params, url },
      location: { search }
    } = this.props;

    const { threeColumn } = this.state;

    if (alwaysFullScreenCreateView && creatingNew) {
      resetEditView();
      updateSelection([]);
    }

    const fullScreenState = creatingNew ? false : !fullScreenEditView;

    if ((!threeColumn || (alwaysFullScreenCreateView && creatingNew)) && !fullScreenState && params.id) {
      this.updateHistory(url.replace(`/${params.id}`, ""), search);
      resetEditView();
    }

    setListFullScreenEditView(fullScreenState);

    setListCreatingNew(false);
  };

  onRowDoubleClick = id => {
    const { match: { params, url }, location: { search } } = this.props;
    const { threeColumn } = this.state;

    this.updateHistory(params.id ? url.replace(`/${params.id}`, `/${id}`) : url + `/${id}`, search);

    if (threeColumn) {
      this.toggleFullWidthView();
    }
  };

  switchLayout = () => {
    const {
      updateLayout,
      updateTableModel,
      updateSelection,
      resetEditView,
      setListCreatingNew,
      creatingNew,
      match: { params, url },
      location: { search }
    } = this.props;
    // eslint-disable-next-line react/no-access-state-in-setstate
    const updatedLayout = !this.state.threeColumn;
    const layout = updatedLayout ? "Three column" : "Two column";

    if (params.id) {
      this.updateHistory(url.replace(`/${params.id}`, ""), search);
    }

    updateLayout(layout);
    updateSelection([]);
    resetEditView();

    setTimeout(() => {
      if (this.props.records?.columns.length) updateTableModel({ layout });
    }, 500);

    this.setState({
      threeColumn: updatedLayout
    });

    if (creatingNew) {
      setListCreatingNew(false);
    }
  };

  updateHistory = (pathname, search) => {
    const { history } = this.props;
    const newUrl = window.location.origin + pathname + search;

    if (newUrl !== window.location.href) {
      history.push({
        pathname,
        search
      });
    }
  };

  updateDeleteCondition = val => {
    this.setState({
      deleteEnabled: val
    });
  };

  handleResizeCallBack = (...props) => {
    const sidebarWidth = props[2].getClientRects()[0].width;

    this.setState({
      sidebarWidth
    });

    setTimeout(() => {
      if (this.props.records?.columns.length) this.props.updateTableModel({ filterColumnWidth: sidebarWidth });
    }, 500);
  };

  handleResizeMainContentCallBack = (...props) => {
    const mainContentWidth = props[2].getClientRects()[0].width;

    this.setState({
      mainContentWidth
    });

    setTimeout(() => {
      this.props.setListviewMainContentWidth(String(mainContentWidth));
    }, 500);
  };

  getMainContentWidth = (mainContentWidth, sidebarWidth) =>
    (mainContentWidth ? Number(mainContentWidth) : window.screen.width - sidebarWidth - 368);

  openNestedEditViewWithDirtyCheck = (...args) => this.checkDirty(this.props.openNestedEditView, args);

  onCreateRecordWithDirtyCheck = this.props.onCreate
    ? (...args) => this.checkDirty(this.onCreateRecord, args, true)
    : undefined;

  switchLayoutWithDirtyCheck = (...args) => this.checkDirty(this.switchLayout, args, true);

  querySearchChangeWithDirtyCheck = (...args) => this.checkDirty(this.onQuerySearchChange, args, true);

  onDeleteFilterWithDirtyCheck = (...args) => {
    const { openConfirm } = this.props;

    const confirmMessage = args.length >= 4 && args[3]
      ? "The filter will be permanently deleted. This action cannot be undone"
      : "This filter is currently being shared with other users. The filter will be permanently deleted. This action cannot be undone";

    openConfirm(
      {
        onConfirm: () => this.checkDirty(this.onDeleteFilter, args, true),
        confirmMessage,
        confirmButtonText: 'DELETE'
      }
    );
  };

  onChangeFiltersWithDirtyCheck = (...args) => this.checkDirty(this.onChangeFilters, args, true);

  getUrlSearch = searchParam => {
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

  renderTableList = () => {
    const {
      listProps, onLoadMore, selection, records, recordsLeft, currency, updateColumns, setShowColoredDots, showColoredDots
    } = this.props;
    const { threeColumn } = this.state;
    return (
      <ReactTableList
        {...listProps}
        onLoadMore={onLoadMore}
        selection={selection}
        records={records}
        recordsLeft={recordsLeft}
        threeColumn={threeColumn}
        updateColumns={updateColumns}
        setShowColoredDots={setShowColoredDots}
        showColoredDots={showColoredDots}
        shortCurrencySymbol={currency.shortCurrencySymbol}
        onRowDoubleClick={this.onRowDoubleClick}
        onSelectionChange={this.onSelection}
        onChangeModel={this.onChangeModel}
        getContainerNode={this.getContainerNode}
      />
    );
  }

  render() {
    const {
      classes,
      rootEntity,
      aqlEntity,
      fetch,
      EditViewContent,
      filterGroups,
      records,
      fetching,
      selection,
      nestedEditFields,
      getEditRecord,
      findRelated,
      editViewProps = {},
      CogwheelAdornment,
      savingFilter,
      CustomFindRelatedMenu,
      alwaysFullScreenCreateView,
      onBeforeSave,
      ShareContainerAlertComponent,
      updateSelection,
      pdfReports,
      exportTemplates,
      searchMenuItemsRenderer,
      menuTags,
      createButtonDisabled,
      creatingNew,
      fullScreenEditView,
      searchQuery,
      getCustomBulkEditFields,
      filterEntity
    } = this.props;

    const {
      querySearch, threeColumn, deleteEnabled, sidebarWidth, mainContentWidth, showExportDrawer, showBulkEditDrawer
    } = this.state;

    const hasFilters = Boolean(filterGroups.length || menuTags.length || savingFilter);

    return (
      <Grid container className="root" direction="row" wrap="nowrap">
        {hasFilters && (
          <ResizableWrapper
            ignoreScreenWidth
            onResizeStop={this.handleResizeCallBack}
            sidebarWidth={sidebarWidth}
            minWidth="10%"
            maxWidth="65%"
          >
            <ThemeProvider theme={sideBarTheme}>
              <SideBar
                fetching={fetching}
                savingFilter={savingFilter}
                onChangeFilters={this.onChangeFiltersWithDirtyCheck}
                filterGroups={filterGroups}
                rootEntity={rootEntity}
                filterEntity={filterEntity}
                deleteFilter={this.onDeleteFilterWithDirtyCheck}
              />
            </ThemeProvider>
          </ResizableWrapper>
        )}

        <Grid item className="flex-fill overflow-hidden flex-column user-select-none">
          <ShareContainer
            showExportDrawer={showExportDrawer}
            toggleExportDrawer={this.toggleExportDrawer}
            count={records.filteredCount}
            selection={selection}
            rootEntity={rootEntity}
            sidebarWidth={hasFilters ? sidebarWidth : 0}
            AlertComponent={ShareContainerAlertComponent}
          />
          <BulkEditContainer
            showBulkEditDrawer={showBulkEditDrawer}
            toggleBulkEditDrawer={this.toggleBulkEditDrawer}
            count={records.filteredCount}
            selection={selection}
            rootEntity={rootEntity}
            sidebarWidth={hasFilters ? sidebarWidth : 0}
            manualLink={editViewProps.manualLink}
            getCustomBulkEditFields={getCustomBulkEditFields}
          />
          <Grid container className={clsx("flex-fill relative overflow-hidden", classes.gridWithEditColumn)}>
            <LoadingIndicator transparentBackdrop allowInteractions />

            {threeColumn ? (
              <ResizableWrapper
                onResizeStop={this.handleResizeMainContentCallBack}
                sidebarWidth={mainContentWidth}
                ignoreScreenWidth
                minWidth="30%"
                maxWidth="65%"
                classes={{ sideBarWrapper: classes.resizableItemList }}
              >
                {this.renderTableList()}
              </ResizableWrapper>
            ) : this.renderTableList() }

            {threeColumn && !fullScreenEditView && (
              <Grid item xs className={clsx("d-flex overflow-y-auto", classes.editViewColumn)}>
                <EditView
                  {...editViewProps}
                  form={LIST_EDIT_VIEW_FORM_NAME}
                  rootEntity={rootEntity}
                  EditViewContent={EditViewContent}
                  onSubmitFail={onSubmitFail}
                  onSubmit={this.onSave}
                  hasSelected={Boolean(selection.length)}
                  creatingNew={creatingNew}
                  updateDeleteCondition={this.updateDeleteCondition}
                  showConfirm={this.showConfirm}
                  openNestedEditView={this.openNestedEditViewWithDirtyCheck}
                  toogleFullScreenEditView={this.toggleFullWidthView}
                />
              </Grid>
            )}
          </Grid>
          <BottomAppBar
            createButtonDisabled={createButtonDisabled}
            searchMenuItemsRenderer={searchMenuItemsRenderer}
            querySearch={querySearch}
            threeColumn={threeColumn}
            deleteEnabled={deleteEnabled}
            showExportDrawer={showExportDrawer}
            toggleExportDrawer={this.toggleExportDrawer}
            showBulkEditDrawer={showBulkEditDrawer}
            toggleBulkEditDrawer={this.toggleBulkEditDrawer}
            filteredCount={records.filteredCount}
            rootEntity={rootEntity}
            aqlEntity={aqlEntity}
            fetch={fetch}
            selection={selection}
            hasShareTypes={pdfReports.length || exportTemplates.length}
            onDelete={this.onDelete}
            onQuerySearch={this.querySearchChangeWithDirtyCheck}
            changeQueryView={this.changeQueryView}
            switchLayout={this.switchLayoutWithDirtyCheck}
            onCreate={this.onCreateRecordWithDirtyCheck}
            toggleFullWidthView={this.toggleFullWidthView}
            findRelated={findRelated}
            CogwheelAdornment={CogwheelAdornment}
            showConfirm={this.showConfirm}
            openNestedEditView={this.openNestedEditViewWithDirtyCheck}
            CustomFindRelatedMenu={CustomFindRelatedMenu}
            records={records}
            searchComponentNode={this.searchComponentNode}
            searchQuery={searchQuery}
          />
        </Grid>

        <FullScreenEditView
          {...editViewProps}
          rootEntity={rootEntity}
          form={LIST_EDIT_VIEW_FORM_NAME}
          fullScreenEditView={fullScreenEditView}
          toogleFullScreenEditView={this.toggleFullWidthView}
          EditViewContent={EditViewContent}
          onSubmit={this.onSave}
          onSubmitFail={onSubmitFail}
          hasSelected={Boolean(selection.length)}
          creatingNew={creatingNew}
          updateDeleteCondition={this.updateDeleteCondition}
          showConfirm={this.showConfirm}
          openNestedEditView={this.openNestedEditViewWithDirtyCheck}
          alwaysFullScreenCreateView={alwaysFullScreenCreateView}
          threeColumn={threeColumn}
        />

        <NestedEditView
          {...editViewProps}
          EditViewContent={EditViewContent}
          rootEntity={rootEntity}
          nestedEditFields={nestedEditFields}
          updateRoot={getEditRecord}
          onBeforeSave={onBeforeSave}
          creatingNew={creatingNew}
          showConfirm={this.showConfirm}
          onSave={this.onSave}
          updateSelection={updateSelection}
          openNestedEditView={this.openNestedEditViewWithDirtyCheck}
        />
      </Grid>
    );
  }
}
const mapStateToProps = (state: State) => ({
  fetch: state.fetch,
  currency: state.currency,
  isDirty: isDirty(LIST_EDIT_VIEW_FORM_NAME)(state),
  isInvalid: isInvalid(LIST_EDIT_VIEW_FORM_NAME)(state),
  syncErrors: getFormSyncErrors(LIST_EDIT_VIEW_FORM_NAME)(state),
  ...state.list,
  ...state.share,
  preferences: state.userPreferences,
  showColoredDots: state.list.showColoredDots,
});

const mapDispatchToProps = (dispatch: Dispatch<any>, ownProps) => ({
  sendGAEvent: (event: GAEventTypes, screen: string, time?: number) => dispatch(pushGTMEvent(event, screen, time)),
  setEntity: entity => dispatch(setListEntity(entity)),
  resetEditView: () => {
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, null));
    dispatch(setListEditRecord(null));
  },
  clearListState: () => dispatch(clearListState()),
  updateSelection: (selection: string[]) => dispatch(setListSelection(selection)),
  updateLayout: (layout: LayoutType) => dispatch(setListLayout(layout)),
  updateColumns: (columns: Column[]) => dispatch(setListColumns(columns)),
  deleteFilter: (id: number, entity: string, checked: boolean) => dispatch(deleteCustomFilter(id, entity, checked)),
  setFilterGroups: (filterGroups: FilterGroup[]) => dispatch(setFilterGroups(filterGroups)),
  setListMenuTags: (tags: MenuTag[]) => dispatch(setListMenuTags(tags)),
  setListUserAQLSearch: (userAQLSearch: string) => dispatch(setListUserAQLSearch(userAQLSearch)),
  getScripts: () => dispatch(getScripts(ownProps.rootEntity)),
  openNestedEditView: (entity: string, id: number, threeColumn: boolean) => dispatch(getListNestedEditRecord(entity, id, null, threeColumn)),
  openConfirm: props => dispatch(showConfirm(props)),
  setListCreatingNew: (creatingNew: boolean) => dispatch(setListCreatingNew(creatingNew)),
  setListFullScreenEditView: (fullScreenEditView: boolean) => dispatch(setListFullScreenEditView(fullScreenEditView)),
  updateTableModel: (model: TableModel, listUpdate?: boolean) => dispatch(updateTableModel(ownProps.rootEntity, model, listUpdate)),
  onLoadMore: (startIndex: number, stopIndex: number, resolve: AnyArgFunction) => dispatch(getRecords(ownProps.rootEntity, true, false, startIndex, stopIndex, resolve)),
  onSearch: search => dispatch(setSearch(search, ownProps.rootEntity)),
  setListEditRecordFetching: () => dispatch(setListEditRecordFetching()),
  onSwipeableDrawerDirtyForm: (isDirty: boolean, resetEditView: any) => dispatch(setSwipeableDrawerDirtyForm(isDirty, resetEditView)),
  getListViewPreferences: () => dispatch(getUserPreferences([LISTVIEW_MAIN_CONTENT_WIDTH])),
  setListviewMainContentWidth: (value: string) => dispatch(setUserPreference({ key: LISTVIEW_MAIN_CONTENT_WIDTH, value })),
  submitForm: () => dispatch(submit(LIST_EDIT_VIEW_FORM_NAME)),
  closeConfirm: () => dispatch(closeConfirm()),
  setShowColoredDots: (value: boolean) => dispatch(setShowColoredDots(value)),
});

export default connect<any, any, Props>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(withRouter(ListView)));
