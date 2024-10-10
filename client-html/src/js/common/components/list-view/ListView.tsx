/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Currency, ExportTemplate, LayoutType, Report, TableModel } from "@api/model";
import ErrorOutline from "@mui/icons-material/ErrorOutline";
import { createTheme } from '@mui/material';
import Button from "@mui/material/Button";
import { createStyles, ThemeProvider, withStyles } from "@mui/styles";
import {
  AnyArgFunction,
  BooleanArgFunction,
  ConfirmProps,
  NoArgFunction,
  ResizableWrapper,
  ShowConfirmCaller,
  StringArgFunction
} from "ish-ui";
import React from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { Dispatch } from "redux";
import { getFormSyncErrors, initialize, isDirty, isInvalid, submit } from "redux-form";
import {
  ENTITY_AQL_STORAGE_NAME,
  LIST_MAIN_CONTENT_DEFAULT_WIDTH,
  LIST_SIDE_BAR_DEFAULT_WIDTH,
  LISTVIEW_MAIN_CONTENT_WIDTH
} from "../../../constants/Config";
import {
  createEntityRecord,
  deleteEntityRecord,
  getEntityRecord,
  updateEntityRecord
} from "../../../containers/entities/common/actions";
import { getCustomFieldTypes } from "../../../containers/entities/customFieldTypes/actions";
import { Fetch } from "../../../model/common/Fetch";
import {
  EditViewContainerProps,
  FilterGroup,
  FindRelatedItem,
  ListAqlMenuItemsRenderer
} from "../../../model/common/ListView";
import { EntityName, FindEntityState } from "../../../model/entities/common";
import { FormMenuTag } from "../../../model/tags";
import { State } from "../../../reducers/state";
import { closeConfirm, getScripts, getUserPreferences, setUserPreference, showConfirm } from "../../actions";
import { UserPreferencesState } from "../../reducers/userPreferencesReducer";
import { getEntityDisplayName } from "../../utils/getEntityDisplayName";
import { onSubmitFail } from "../../utils/highlightFormErrors";
import { saveCategoryAQLLink } from "../../utils/links";
import { LSGetItem } from "../../utils/storage";
import { pushGTMEvent } from "../google-tag-manager/actions";
import { GAEventTypes } from "../google-tag-manager/services/GoogleAnalyticsService";
import LoadingIndicator from "../progress/LoadingIndicator";
import {
  clearListState,
  deleteCustomFilter,
  findRelatedByFilter,
  getRecords,
  setFilterGroups,
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
  updateTableModel,
} from "./actions";
import BottomAppBar from "./components/bottom-app-bar/BottomAppBar";
import BulkEditContainer from "./components/bulk-edit/BulkEditContainer";
import EditView from "./components/edit-view/EditView";
import FullScreenEditView from "./components/full-screen-edit-view/FullScreenEditView";
import ReactTableList, { TableListProps } from "./components/list/ReactTableList";
import ShareContainer from "./components/share/ShareContainer";
import SideBar from "./components/side-bar/SideBar";
import { LIST_EDIT_VIEW_FORM_NAME } from "./constants";
import {
  getActiveTags,
  getFiltersNameString,
  getTagsUpdatedByIds,
  setActiveFiltersBySearch
} from "./utils/listFiltersUtils";
import { shouldAsyncValidate } from "./utils/listFormUtils";

const styles = () => createStyles({
  root: {
    position: "relative",
    display: "flex",
    flexDirection: 'row',
    width: "100vw",
    height: "100vh",
    overflow: "hidden"
  },
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

interface OwnProps {
  onCreate?: (item: any) => void;
  onDelete?: (id: number) => void;
  getEditRecord?: (id: number) => void;
  onSave?: (item: any) => void;
  location?: any;
  history?: any;
  match?: any;
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
  dispatch?: Dispatch;
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
  clearListState?: NoArgFunction;
  setListCreatingNew?: BooleanArgFunction;
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
  preformatBeforeSubmit?: AnyArgFunction;
  deleteDisabledCondition?: (props) => boolean;
  noListTags?: boolean;
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

class ListView extends React.PureComponent<Props & OwnProps & State["list"] & State["share"], ComponentState> {
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
      sidebarWidth: LIST_SIDE_BAR_DEFAULT_WIDTH,
      mainContentWidth: this.getMainContentWidth(LIST_MAIN_CONTENT_DEFAULT_WIDTH, LIST_SIDE_BAR_DEFAULT_WIDTH),
      newSelection: null
    };
  }

  componentDidMount() {
    const {
      getScripts,
      getCustomFieldTypes,
      history,
      sendGAEvent,
      rootEntity,
      setEntity,
      match: { url, params },
      filterGroupsInitial = [],
      selection,
      getListViewPreferences
    } = this.props;

    setEntity(rootEntity);
    getCustomFieldTypes(rootEntity);

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

    if (params.id && !selection.includes(params.id)) {
      this.ignoreCheckDirtyOnSelection = true;
      this.onSelection([params.id]);
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
      checkedChecklists,
      uncheckedChecklists,
      customTabTitle
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
      document.title = `${customTabTitle || getEntityDisplayName(rootEntity)} (${records.filteredCount || 0} found)`;
      if (records.filteredCount === null) {
        document.title = `${customTabTitle || getEntityDisplayName(rootEntity)}`;
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
      } else {
        this.onCreateRecord();
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
      const tagsUrlString = currentUrlSearch.get("tags");
      const checkedChecklistsUrlString = currentUrlSearch.get("checkedChecklists");
      const uncheckedChecklistsUrlString = currentUrlSearch.get("uncheckedChecklists");
      const searchString = currentUrlSearch.get("search");

      if (prevUrlSearch.get("filter") !== filtersUrlString) {
        const filtersString = getFiltersNameString(filterGroups);
        if (filtersString !== filtersUrlString) {
          this.onChangeFilters(setActiveFiltersBySearch(filtersUrlString, filterGroups), "filters");
        }
      }

      // Update tags by url
      if (prevUrlSearch.get("tags") !== tagsUrlString) {
        const activeString = getActiveTags(menuTags).map(t => t.tagBody.id).toString();
        if (activeString !== tagsUrlString) {
          const tagIds = tagsUrlString ? tagsUrlString
            .split(",")
            .map(f => Number(f)) : [];
          this.onChangeFilters(getTagsUpdatedByIds(menuTags, tagIds), "tags");
        }
      }

      // Update checked tags by url
      if (prevUrlSearch.get("checkedChecklists") !== checkedChecklistsUrlString) {
        const activeString = getActiveTags(checkedChecklists).map(t => t.tagBody.id).toString();
        if (activeString !== checkedChecklistsUrlString) {
          const ids = checkedChecklistsUrlString ? checkedChecklistsUrlString
            .split(",")
            .map(f => Number(f)) : [];
          this.onChangeFilters(getTagsUpdatedByIds(checkedChecklists, ids), "checkedChecklists");
        }
      }

      // Update tags by url
      if (prevUrlSearch.get("uncheckedChecklists") !== uncheckedChecklistsUrlString) {
        const activeString = getActiveTags(uncheckedChecklists).map(t => t.tagBody.id).toString();
        if (activeString !== uncheckedChecklistsUrlString) {
          const ids = uncheckedChecklistsUrlString ? uncheckedChecklistsUrlString
            .split(",")
            .map(f => Number(f)) : [];
          this.onChangeFilters(getTagsUpdatedByIds(uncheckedChecklists, ids), "uncheckedChecklists");
        }
      }

      // Update filters by url
      if (prevUrlSearch.get("search") !== searchString) {
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

  synchronizeAllFilters = () => {
    const {
      setListUserAQLSearch,
      filterGroupsInitial = [],
      filterGroups,
      menuTags,
      checkedChecklists,
      uncheckedChecklists,
      onSearch
    } = this.props;

    const targetFilters = filterGroups.length ? [...filterGroupsInitial, ...filterGroups] : filterGroupsInitial || [];

    if (this.props.location.search) {
      const searchParams = new URLSearchParams(this.props.location.search);

      const search = this.getUrlSearch(searchParams);
      const filtersSearch = searchParams.get("filter");
      const tagsSearch = searchParams.get("tags");
      const checkedChecklistsUrlString = searchParams.get("checkedChecklists");
      const uncheckedChecklistsUrlString = searchParams.get("uncheckedChecklists");

      this.onChangeFilters(filtersSearch ? setActiveFiltersBySearch(filtersSearch, targetFilters) : targetFilters, "filters");

      // Sync tags by search
      if (tagsSearch && menuTags) {
        const tagIds = tagsSearch
          .split(",")
          .map(f => Number(f));
        this.onChangeFilters(getTagsUpdatedByIds(menuTags, tagIds), "tags");
      }

      // Sync checked checklists by search
      if (checkedChecklistsUrlString && checkedChecklists) {
        const ids = checkedChecklistsUrlString
          .split(",")
          .map(f => Number(f));
        this.onChangeFilters(getTagsUpdatedByIds(checkedChecklists, ids), "checkedChecklists");
      }

      // Sync unchecked checklists by search
      if (uncheckedChecklistsUrlString && uncheckedChecklists) {
        const ids = uncheckedChecklistsUrlString
          .split(",")
          .map(f => Number(f));
        this.onChangeFilters(getTagsUpdatedByIds(uncheckedChecklists, ids), "uncheckedChecklists");
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

  onGetEditRecord = id => {
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

    if (newSelection.length === 1 && selection.length === 1 && newSelection[0] === selection[0]) {
      return;
    }

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

  onChangeFilters = (filters: FilterGroup[] | FormMenuTag[], type: string) => {
    const {
     setFilterGroups, setListMenuTags, location: { search }, match: { url }, menuTags, checkedChecklists, uncheckedChecklists
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

    onSave(value);
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
    const { isDirty, selection, creatingNew, fullScreenEditView, records } = this.props;

    if (isDirty && (records.layout === 'Two column' ? fullScreenEditView : true)) {
      this.showConfirm({
        onConfirm: () => {
          handler(...args);
          if (reset) {
            this.props.resetEditView();
          }
        }
      });
      return;
    }
    if (creatingNew && selection[0] === "new") {
      this.showConfirm({
        onConfirm: () => {
          handler(...args);
          if (reset) {
            this.props.resetEditView();
            setTimeout(this.onCreateRecord, 200);
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
          onCancelCustom: props.onConfirm
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

  render() {
    const {
      classes,
      rootEntity,
      fetch,
      EditViewContent,
      filterGroups,
      records,
      fetching,
      selection,
      findRelated,
      editViewProps = {},
      CogwheelAdornment,
      savingFilter,
      CustomFindRelatedMenu,
      alwaysFullScreenCreateView,
      ShareContainerAlertComponent,
      pdfReports,
      exportTemplates,
      searchMenuItemsRenderer,
      menuTags,
      createButtonDisabled,
      creatingNew,
      fullScreenEditView,
      searchQuery,
      getCustomBulkEditFields,
      filterEntity,
      emailTemplatesWithKeyCode,
      scripts,
      listProps,
      onLoadMore,
      currency,
      dispatch,
      getScripts,
      findRelatedByFilter,
      scriptsFilterColumn,
      customTableModel
    } = this.props;

    const {
      querySearch, threeColumn, deleteEnabled, sidebarWidth, mainContentWidth, showExportDrawer, showBulkEditDrawer
    } = this.state;

    const hasFilters = Boolean(filterGroups.length || menuTags.length || savingFilter);

    const table = <ReactTableList
      {...listProps}
      mainContentWidth={mainContentWidth}
      onLoadMore={onLoadMore}
      selection={selection}
      records={records}
      threeColumn={threeColumn}
      shortCurrencySymbol={currency.shortCurrencySymbol}
      onRowDoubleClick={this.onRowDoubleClick}
      onSelectionChange={this.onSelection}
      onChangeModel={this.onChangeModel}
      getContainerNode={this.getContainerNode}
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
          toogleFullScreenEditView={this.toggleFullWidthView}
          EditViewContent={EditViewContent}
          onSubmit={this.onSave}
          onSubmitFail={onSubmitFail}
          hasSelected={Boolean(selection.length)}
          creatingNew={creatingNew}
          updateDeleteCondition={this.updateDeleteCondition}
          showConfirm={this.showConfirm}
          alwaysFullScreenCreateView={alwaysFullScreenCreateView}
          threeColumn={threeColumn}
        />

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

        {hasFilters && (
          <ResizableWrapper
            ignoreScreenWidth
            onResizeStop={this.handleResizeCallBack}
            sidebarWidth={sidebarWidth}
            minWidth="265px"
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

        <div className="flex-fill d-flex flex-column overflow-hidden user-select-none">
          <div className="flex-fill d-flex relative">
            {threeColumn ? (
              <ResizableWrapper
                onResizeStop={this.handleResizeMainContentCallBack}
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
                  onSubmit={this.onSave}
                  hasSelected={Boolean(selection.length)}
                  creatingNew={creatingNew}
                  updateDeleteCondition={this.updateDeleteCondition}
                  showConfirm={this.showConfirm}
                  toogleFullScreenEditView={this.toggleFullWidthView}
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
            toggleExportDrawer={this.toggleExportDrawer}
            showBulkEditDrawer={showBulkEditDrawer}
            toggleBulkEditDrawer={this.toggleBulkEditDrawer}
            filteredCount={records.filteredCount}
            rootEntity={rootEntity}
            fetch={fetch}
            selection={selection}
            hasShareTypes={pdfReports.length || exportTemplates.length}
            onDelete={this.onDelete}
            onQuerySearch={this.querySearchChangeWithDirtyCheck}
            changeQueryView={this.changeQueryView}
            switchLayout={this.switchLayoutWithDirtyCheck}
            onCreate={this.onCreateRecordWithDirtyCheck}
            findRelated={findRelated}
            CogwheelAdornment={CogwheelAdornment}
            showConfirm={this.showConfirm}
            CustomFindRelatedMenu={CustomFindRelatedMenu}
            records={records}
            searchComponentNode={this.searchComponentNode}
            searchQuery={searchQuery}
            scriptsFilterColumn={scriptsFilterColumn}
          />
        </div>
      </div>
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
});

const mapDispatchToProps = (dispatch: Dispatch, ownProps) => ({
  dispatch,
  sendGAEvent: (event: GAEventTypes, screen: string, time?: number) => dispatch(pushGTMEvent(event, screen, time)),
  setEntity: entity => dispatch(setListEntity(entity)),
  resetEditView: () => {
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, null));
    dispatch(setListEditRecord(null));
  },
  clearListState: () => dispatch(clearListState()),
  updateSelection: (selection: string[]) => dispatch(setListSelection(selection)),
  updateLayout: (layout: LayoutType) => dispatch(setListLayout(layout)),
  deleteFilter: (id: number, entity: string, checked: boolean) => dispatch(deleteCustomFilter(id, entity, checked)),
  setFilterGroups: (filterGroups: FilterGroup[]) => dispatch(setFilterGroups(filterGroups)),
  setListMenuTags: ({ tags, checkedChecklists, uncheckedChecklists }) => dispatch(setListMenuTags(tags, checkedChecklists, uncheckedChecklists)),
  setListUserAQLSearch: (userAQLSearch: string) => dispatch(setListUserAQLSearch(userAQLSearch)),
  getScripts: () => dispatch(getScripts(ownProps.rootEntity)),
  getCustomFieldTypes: (entity: EntityName) => dispatch(getCustomFieldTypes(entity)),
  openConfirm: props => dispatch(showConfirm(props)),
  setListCreatingNew: (creatingNew: boolean) => dispatch(setListCreatingNew(creatingNew)),
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
  mergeProps)
(withStyles(styles)(withRouter(ListView))) as React.FC<Props>;