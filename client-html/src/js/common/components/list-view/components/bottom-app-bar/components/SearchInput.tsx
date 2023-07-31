/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import withStyles from "@mui/styles/withStyles";
import { green } from "@mui/material/colors";
import IconButton from "@mui/material/IconButton";
import Clear from "@mui/icons-material/Clear";
import BookmarkBorder from "@mui/icons-material/BookmarkBorder";
import BookmarkTwoTone from "@mui/icons-material/BookmarkTwoTone";
import createStyles from "@mui/styles/createStyles";
import HelpOutline from "@mui/icons-material/HelpOutline";
import clsx from "clsx";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { darken } from "@mui/material/styles";
import debounce from "lodash.debounce";
import { CustomFieldType, ProductType } from "@api/model";
import EditInPlaceQuerySelect from "../../../../form/formFields/EditInPlaceQuerySelect";
import QuerySaveMenu from "./QuerySaveMenu";
import { State } from "../../../../../../reducers/state";
import { StringArgFunction } from  "ish-ui";
import { setIndeterminate } from "../../../utils/listFiltersUtils";
import {
 setFilterGroups, setListSavingFilter, setListUserAQLSearch
} from "../../../actions";
import { FormMenuTag } from "../../../../../../model/tags";
import { FilterGroup, ListAqlMenuItemsRenderer, SavingFilterState } from "../../../../../../model/common/ListView";
import { FILTER_TAGS_REGEX } from "../../../../../../constants/Config";
import { AppTheme } from  "ish-ui";
import { getSaleEntityName } from "../../../../../../containers/entities/sales/utils";
import { QueryFieldSuggestion } from "../../../../../../model/common/Fields";
import { getAllMenuTags } from  "ish-ui";

export const styles = (theme: AppTheme) => createStyles({
    container: {
      transition: `flex ${theme.transitions.duration.standard}ms ${theme.transitions.easing.sharp}`,
      willChange: "flex",
      display: "flex",
      alignItems: "center"
    },
    content: {
      display: "flex",
      alignItems: "center",
      width: "auto",
      zIndex: 1,
      top: "50%",
      backgroundColor:
        theme.palette.mode === "light" ? theme.palette.primary.main : darken(theme.palette.background.default, 0.4)
    },
    search: {
      width: "100%",
      minWidth: "260px",
      backgroundColor: theme.palette.background.paper,
      borderRadius: "4px",
      height: "36px",
      lineHeight: 0,
      display: "flex",
      alignItems: "center",
      paddingLeft: theme.spacing(1)
    },
    inputIcon: {
      height: "34px",
      margin: "0 2px",
      padding: "5px"
    },
    shiftedIcon: {
      marginRight: "-5px"
    },
    validationIcon: {
      width: theme.spacing(3),
      marginRight: theme.spacing(1)
    },
    bookmarkIconValid: {
      color: green[600]
    },
    hidden: {
      display: "none"
    },
    plus: {
      fontSize: "12px",
      marginLeft: "-5px"
    },
    textField: {
      width: "260px"
    }
  });

interface Props {
  querySearch: boolean;
  rootEntity: string;
  userAQLSearch: string;
  savingFilter: SavingFilterState;
  tags: FormMenuTag[];
  filterGroups: FilterGroup[];
  setListUserAQLSearch: StringArgFunction;
  onQuerySearch: StringArgFunction;
  setFilterGroups: (groups: FilterGroup[]) => void;
  setListSavingFilter: (saving: SavingFilterState) => void;
  classes?: any;
  changeQueryView?: any;
  startAdornment?: any;
  queryInputRef?: any;
  queryComponentRef?: any;
  alwaysExpanded?: boolean;
  placeholder?: string;
  searchServerError?: boolean;
  searchMenuItemsRenderer?: ListAqlMenuItemsRenderer;
  customFieldTypes?: CustomFieldType[];
}

interface SearchInputState {
  expanded: boolean;
  querySaveMenuAnchor: HTMLElement;
  tagsSuggestions: QueryFieldSuggestion[];
  filtersSuggestions: QueryFieldSuggestion[];
  customFieldsSuggestions: string[];
  tagsPrefixes?: string[];
}

const getFilterNamesSuggestions = (filterGroups: FilterGroup[]): QueryFieldSuggestion[] => filterGroups
    .flatMap(i => i.filters)
    .map(i => {
      const name = i.name.replace(/\s/g, "_");

      return {
        token: "Identifier",
        value: name,
        label: name
      };
    });

const getTagNamesSuggestions = (tags: FormMenuTag[]): QueryFieldSuggestion[] => {
  const childTags = tags.flatMap(t => t.children);

  return getAllMenuTags(childTags).map(i => {
    const name = i.tagBody.name;
    const suggestion: QueryFieldSuggestion = {
      token: "Identifier",
      value: ` "${name}"`,
      label: name
    };

    if (i.queryPrefix) {
      suggestion.queryPrefix = i.queryPrefix;
    }

    if (i.prefix) {
      suggestion.prefix = i.prefix;
    }

    return suggestion;
  });
};

const getCustomFieldsSuggestions = (customFields: CustomFieldType[]): string[] => customFields.map(f => f.fieldKey);

const mapTags = (tags: FormMenuTag[], parent?: FormMenuTag) => tags.map(t => {
    const updated = { ...t, parent };

    if (updated.children.length) {
      updated.children = mapTags(updated.children, updated);
    }

    return updated;
  });

class SearchInput extends React.PureComponent<Props, SearchInputState> {
  private inputNode: any;

  private queryComponentNode: any;

  private isActionIconHovered: boolean = false;

  constructor(props) {
    super(props);

    this.state = {
      querySaveMenuAnchor: null,
      expanded: props.alwaysExpanded,
      tagsSuggestions: null,
      filtersSuggestions: null,
      customFieldsSuggestions: null
    };
  }

  componentDidUpdate(prevProps) {
    const {
     filterGroups, tags, customFieldTypes, userAQLSearch
    } = this.props;

    if (prevProps.userAQLSearch && !userAQLSearch) {
      this.inputNode.value = "";
    }

    if (tags.length && !this.state.tagsSuggestions) {
      this.setState({
        tagsSuggestions: getTagNamesSuggestions(this.props.tags)
      });

      if (tags.some(t => t.queryPrefix)) {
        const prefixes = new Set([]);

        tags.forEach(t => {
          if (t.queryPrefix) {
            prefixes.add(t.queryPrefix);
          }
        });

        this.setState({
          tagsPrefixes: Array.from(prefixes)
        });
      }
    }

    if (
      (filterGroups.length && !this.state.filtersSuggestions)
      || prevProps.filterGroups.length !== filterGroups.length
    ) {
      this.setState({
        filtersSuggestions: getFilterNamesSuggestions(filterGroups)
      });
    }

    if (prevProps.customFieldTypes !== customFieldTypes && customFieldTypes.length) {
      this.setState({
        customFieldsSuggestions: getCustomFieldsSuggestions(customFieldTypes)
      });
    }
  }

  onActionIconOver = () => {
    this.isActionIconHovered = true;
  };

  onActionIconOut = () => {
    this.isActionIconHovered = false;
  };

  setExpanded = expanded => {
    if (!expanded && this.isActionIconHovered) {
      return;
    }

    this.setState({
      expanded
    });

    const { changeQueryView } = this.props;

    if (typeof changeQueryView === "function") {
      changeQueryView(expanded);
    }
  };

  setInputNode = node => {
    const { queryInputRef } = this.props;
    this.inputNode = node;

    if (queryInputRef) {
      queryInputRef.current = node;
    }
  };

  openQuerySaveMenu = e => {
    this.setState({
      querySaveMenuAnchor: e.currentTarget
    });
  };

  closeQuerySaveMenu = () => {
    this.setState(
      {
        querySaveMenuAnchor: null
      },
      this.onBlur
    );
  };

  getAqlExpression = (value: string, setUsersSearch?: boolean) => {
    const {
      filterGroups, tags, setListUserAQLSearch
    } = this.props;

    if (setUsersSearch) {
      setListUserAQLSearch(value);
    }

    const updatedGroups = filterGroups.map(f => ({ ...f, filters: f.filters.map(f => ({ ...f })) }));

    const menuTags = mapTags(tags);

    const filters = updatedGroups.flatMap(i => i.filters);

    const activeFilters = filters.filter(i => i.active);

    const activeFiltersMatch = [];

    const listTags = getAllMenuTags(menuTags);

    const activeTags = listTags.filter(t => t.active);

    const activeTagsMatch = [];

    const expression = value
      .replace(FILTER_TAGS_REGEX, str => {
        const replaced = str.replace(/@/g, "").replace(/_/g, " ");

        const filter = filters.find(f => f.name === replaced);

        if (filter && filter.active) {
          activeFiltersMatch.push(filter);
        }

        if (filter && !filter.active) {
          activeFiltersMatch.push(filter);
          filter.active = true;
        }

        return filter ? `(${filter.expression})` : "";
      });

    activeFilters.forEach(f => {
      if (!activeFiltersMatch.find(i => i.name === f.name)) {
        f.active = false;
      }
    });

    activeTags.forEach(t => {
      if (!activeTagsMatch.find(i => i.tagBody.id === t.tagBody.id)) {
        t.active = false;
        if (t.parent) {
          setIndeterminate(t.parent);
        }
      }
    });

    return expression;
  };

  searchByQuery = () => {
    const { onQuerySearch, savingFilter, setListSavingFilter } = this.props;

    if (savingFilter) {
      setListSavingFilter(null);
    }

    const expression = this.getAqlExpression(this.inputNode.value, true);

    onQuerySearch(expression);
  };

  debounceSearch = debounce(this.searchByQuery, 500);

  setSavingFilterState = (isPrivate: boolean) => {
    this.isActionIconHovered = false;

    const { setListSavingFilter } = this.props;

    setListSavingFilter({
      isPrivate,
      aqlSearch: this.getAqlExpression(this.inputNode.value, false)
    });

    this.closeQuerySaveMenu();
  };

  openHelp = () => {
    this.isActionIconHovered = false;

    window.open("https://www.ish.com.au/s/onCourse/doc/latest/manual/#search-advanced", "_blank");

    this.onBlur();
  };

  getQueryComponentRef = node => {
    const { queryComponentRef } = this.props;

    if (node) {
      this.queryComponentNode = node;
      if (queryComponentRef) {
        queryComponentRef.current = node;
      }
    }
  };

  onBlur = () => {
    const { expanded } = this.state;
    const { alwaysExpanded } = this.props;

    if (expanded && this.isActionIconHovered) {
      return;
    }

    if (!alwaysExpanded) {
      this.setExpanded(false);
    }
  };

  onFocus = () => {
    this.setExpanded(true);
  };

  clear = () => {
    this.isActionIconHovered = false;

    const { onQuerySearch, alwaysExpanded } = this.props;
    const { expanded } = this.state;

    onQuerySearch(this.getAqlExpression("", true));

    this.queryComponentNode.reset();

    if (!alwaysExpanded && expanded) {
      this.setExpanded(false);
    }
  };

  render() {
    const {
      classes,
      rootEntity,
      placeholder,
      userAQLSearch,
      startAdornment,
      alwaysExpanded,
      searchServerError,
      searchMenuItemsRenderer
    } = this.props;

    const {
      querySaveMenuAnchor,
      expanded,
      tagsSuggestions,
      filtersSuggestions,
      customFieldsSuggestions
    } = this.state;

    return (
      <>
        <div
          className={clsx(classes.container, {
            "flex-fill": expanded
          })}
        >
          <div
            className={clsx(classes.content, {
              "w-100": expanded,
              "pr-2": expanded
            })}
          >
            <div className={classes.search}>
              {startAdornment}

              <EditInPlaceQuerySelect
                inline
                ref={this.getQueryComponentRef}
                tagSuggestions={tagsSuggestions || []}
                filterTags={filtersSuggestions || []}
                customFields={customFieldsSuggestions || []}
                setInputNode={this.setInputNode}
                rootEntity={rootEntity}
                className="flex-fill"
                placeholder={placeholder}
                input={{
                  value: userAQLSearch
                }}
                performSearch={this.debounceSearch}
                onFocus={alwaysExpanded ? undefined : this.onFocus}
                onBlur={this.onBlur}
                itemRenderer={searchMenuItemsRenderer}
                disableUnderline
                disableErrorText
              />

              {userAQLSearch && (
                <IconButton
                  className={clsx(classes.inputIcon, expanded && classes.shiftedIcon)}
                  onClick={this.clear}
                  onMouseEnter={this.onActionIconOver}
                  onMouseLeave={this.onActionIconOut}
                >
                  <Clear />
                </IconButton>
              )}

              {expanded && (
                <IconButton
                  className={clsx(classes.inputIcon, classes.shiftedIcon)}
                  onClick={this.openHelp}
                  onMouseEnter={this.onActionIconOver}
                  onMouseLeave={this.onActionIconOut}
                >
                  <HelpOutline />
                </IconButton>
              )}

              {expanded && (
                <IconButton
                  disabled={!userAQLSearch || searchServerError}
                  className={classes.inputIcon}
                  onClick={this.openQuerySaveMenu}
                  onMouseEnter={this.onActionIconOver}
                  onMouseLeave={this.onActionIconOut}
                >
                  {searchServerError ? (
                    <BookmarkTwoTone color="error" />
                  ) : (
                    <BookmarkBorder className={userAQLSearch ? classes.bookmarkIconValid : undefined} />
                  )}
                </IconButton>
              )}
            </div>
          </div>
        </div>

        <QuerySaveMenu
          setSavingFilterState={this.setSavingFilterState}
          anchor={querySaveMenuAnchor}
          closeQuerySaveMenu={this.closeQuerySaveMenu}
          classes={classes}
        />
      </>
    );
  }
}

export const SearchInputBase = withStyles(styles)(SearchInput);

const mapStateToProps = (state: State, ownProps: Props) => ({
  savingFilter: state.list.savingFilter,
  filterGroups: state.list.filterGroups,
  userAQLSearch: state.list.userAQLSearch,
  searchServerError: state.list.searchError,
  tags: state.list.menuTags,
  customFieldTypes: ownProps.rootEntity === "ProductItem"
    ? Object.keys(ProductType).reduce((p, c) => [...p, ...state.customFieldTypes.types[getSaleEntityName(c as any)] || []], [])
    : state.customFieldTypes.types[ownProps.rootEntity]
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setListSavingFilter: (savingFilter?: SavingFilterState) => dispatch(setListSavingFilter(savingFilter)),
  setFilterGroups: (filterGroups: FilterGroup[]) => dispatch(setFilterGroups(filterGroups)),
  setListUserAQLSearch: (userAQLSearch: string) => dispatch(setListUserAQLSearch(userAQLSearch))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(SearchInputBase);
