/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Typography } from "@material-ui/core";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import debounce from "lodash.debounce";
import CircularProgress from "@material-ui/core/CircularProgress";
import { Field, Validator } from "redux-form";
import { connect } from "react-redux";
import { EntityRelationType } from "@api/model";
import PaperListRenderer, { PanelItemChangedMessage } from "./components/PaperListRenderer";
import ListRenderer from "./components/ListRenderer";
import { InputSection, InputSectionWithToggle } from "./components/InputSections";
import { SIMPLE_SEARCH_QUOTES_REGEX, SIMPLE_SEARCH_REGEX, TAGS_REGEX } from "../../../../constants/Config";
import { Suggestion } from "../form-fields/EditInPlaceQuerySelect";
import { State } from "../../../../reducers/state";
import { getTagNamesSuggestions } from "../../../../containers/tags/utils";

const styles = theme => createStyles({
    root__search: {
      minHeight: theme.spacing(6)
    },
    button: {
      whiteSpace: "nowrap",
      minWidth: "80px"
    },
    rowMargin: {
      margin: theme.spacing(1, 0)
    },
    inputMargin: {
      marginBottom: theme.spacing(1)
    },
    expandableInput: {
      flex: 1,
      transition: `flex ${theme.transitions.duration.standard}ms ${theme.transitions.easing.sharp}`,
      willChange: "flex"
    },
    collapsed: {
      flex: 0,
      overflow: "hidden"
    }
  });

type SearchTypes = "withToggle" | "immediate";

interface Props {
  formId: number;
  aqlEntity?: string;
  aqlEntityTags?: string[];
  additionalAqlEntity?: string;
  additionalAqlEntityTags?: string[];
  values: NestedListItem[];
  searchValues: NestedListItem[];
  title: string;
  name?: string;
  titleCaption?: string;
  classes?: any;
  searchPlaceholder?: string;
  additionalSearchPlaceholder?: string;
  onAdd: (items: NestedListItem[]) => void;
  onDelete: (item: NestedListItem, index?: number) => void;
  onDeleteAll?: () => void;
  onSearch: (search: string) => void;
  onAdditionalSearch?: (search: string) => void;
  clearSearchResult: (pending: boolean) => void;
  clearAdditionalSearchResult?: (pending: boolean) => void;
  sort?: (a, b) => void;
  usePaper?: boolean;
  resetSearch?: boolean;
  secondaryHeading?: boolean;
  pending: boolean;
  searchType?: SearchTypes;
  inlineSecondaryText?: boolean;
  panelItems?: NestedListPanelItem[];
  panelCaption?: string;
  onChangePanelItem?: (message: PanelItemChangedMessage) => void;
  hideAddButton?: boolean;
  dataRowClass?: string;
  onToggleSearch?: any;
  disabled?: boolean;
  disableAddAll?: boolean;
  validate?: Validator;
  entityTags?: any;
  relationTypes?: EntityRelationType[];
  formField?: string;
}

interface NestedListState {
  searchEnabled: boolean;
  searchExpression?: string;
  additionalSearchExpression?: string;
  toggleEnabled?: boolean;
  formError?: string;
  isValidAqlQuery?: boolean;
  searchTags?: Suggestion[];
  additionalSearchTags?: Suggestion[];
}

class NestedList extends React.Component<Props, NestedListState> {
  private readonly inputRef: any;

  private readonly aqlComponentRef: any;

  private readonly additionalAqlComponentRef: any;

  constructor(props) {
    super(props);
    this.inputRef = React.createRef();
    this.aqlComponentRef = React.createRef();
    this.additionalAqlComponentRef = React.createRef();

    this.state = {
      searchEnabled: false,
      isValidAqlQuery: false,
      toggleEnabled: Boolean(props.values && props.values.length),
      searchExpression: "",
      additionalSearchExpression: "",
      formError: null,
      searchTags: [],
      additionalSearchTags: []
    };
  }

  componentDidUpdate(prevProps) {
    const {
      resetSearch,
      values,
      formId,
      clearSearchResult,
      clearAdditionalSearchResult,
      aqlEntityTags,
      entityTags,
      additionalAqlEntityTags,
      additionalAqlEntity
    } = this.props;

    const { searchTags, additionalSearchTags } = this.state;

    if (!prevProps.values.length && values.length) {
      this.setState({
        toggleEnabled: true
      });
    }

    if (prevProps.values.length && !values.length) {
      this.setState({
        toggleEnabled: false
      });
    }

    if (prevProps.formId !== formId || (!prevProps.resetSearch && resetSearch)) {
      this.setState({
        searchEnabled: false,
        toggleEnabled: Boolean(values && values.length),
        searchExpression: "",
        additionalSearchExpression: ""
      });

      clearSearchResult(false);

      if (clearAdditionalSearchResult) {
        clearAdditionalSearchResult(false);
      }

      if (additionalAqlEntity) {
        this.clearDoubleSearch();
      }
    }

    if (aqlEntityTags && !searchTags.length) {
      let tagsMatch = 0;

      aqlEntityTags.forEach(t => {
        if (entityTags[t] && entityTags[t].length) {
          tagsMatch++;
        }
      });

      if (tagsMatch === aqlEntityTags.length) {
        this.setState({
          searchTags: aqlEntityTags
            .reduce((p, c) => p.concat(getTagNamesSuggestions(entityTags[c])), [])
            .filter((t, index, self) => self.findIndex(s => s.label === t.label) === index)
        });
      }
    }

    if (additionalAqlEntityTags && !additionalSearchTags.length) {
      let tagsMatch = 0;

      additionalAqlEntityTags.forEach(t => {
        if (entityTags[t]) {
          tagsMatch++;
        }
      });

      if (tagsMatch === additionalAqlEntityTags.length) {
        this.setState({
          additionalSearchTags: additionalAqlEntityTags
            .reduce((p, c) => p.concat(getTagNamesSuggestions(entityTags[c])), [])
            .filter((t, index, self) => self.findIndex(s => s.label === t.label) === index)
        });
      }
    }
  }

  validateAql = isValidAqlQuery => {
    this.setState({
      isValidAqlQuery
    });
  };

  triggerSearch = () => {
    const { onSearch, searchType } = this.props;
    const { searchExpression } = this.state;

    if (searchExpression.length > 0 || searchType === "immediate") {
      onSearch(searchExpression);
    }
  };

  triggerAqlSearch = (additional?: boolean) => {
    const { isValidAqlQuery } = this.state;
    const { searchType } = this.props;

    if (!isValidAqlQuery) {
      return;
    }

    let { value } = this.inputRef.current;

    if (value.match(SIMPLE_SEARCH_QUOTES_REGEX)) {
      value = `~${value}`;
    } else if (value.match(SIMPLE_SEARCH_REGEX)) {
      value = `~"${value}"`;
    }

    if (value.match(TAGS_REGEX)) {
      value = value.replace(TAGS_REGEX, rep => `(# "${rep.replace(/_/g, " ").replace(/#/g, "")}")`);
    }

    const {
     clearSearchResult, onSearch, onAdditionalSearch, clearAdditionalSearchResult
    } = this.props;

    additional ? clearAdditionalSearchResult(true) : clearSearchResult(true);

    const newState = additional
      ? { additionalSearchExpression: this.inputRef.current.value }
      : { searchExpression: this.inputRef.current.value };

    this.setState(newState, () => {
      if (value.length > 0 || searchType === "immediate") {
        additional ? onAdditionalSearch(value) : onSearch(value);
      }
    });
  };

  debounceSearch = debounce(this.triggerSearch, 500);

  onAqlSearchChange = debounce(this.triggerAqlSearch, 500);

  toggleSearch = () => {
    const {
     clearSearchResult, onToggleSearch, aqlEntity, additionalAqlEntity
    } = this.props;

    const { searchEnabled } = this.state;

    if (!searchEnabled) {
      if (typeof onToggleSearch === "function") {
        onToggleSearch();
      }

      if (!aqlEntity) {
        setTimeout(() => {
          this.inputRef.current.focus();
        }, 300);
      }
    }

    if (searchEnabled) {
      clearSearchResult(false);
    }

    if (aqlEntity) {
      if (searchEnabled) {
        this.aqlComponentRef.current.reset();
      } else if (!additionalAqlEntity) {
        setTimeout(() => this.inputRef.current && this.inputRef.current.focus(), 300);
      }
    }

    this.setState({
      searchEnabled: !searchEnabled,
      searchExpression: ""
    });
  };

  clearDoubleSearch = (additional?: boolean) => {
    const { clearSearchResult, clearAdditionalSearchResult } = this.props;
    const { searchExpression, additionalSearchExpression } = this.state;

    if (additional) {
      clearAdditionalSearchResult(false);

      this.additionalAqlComponentRef.current.reset();

      this.setState({
        searchEnabled: Boolean(searchExpression),
        additionalSearchExpression: ""
      });
    } else {
      clearSearchResult(false);

      if (this.aqlComponentRef.current) {
        this.aqlComponentRef.current.reset();
      }

      this.setState({
        searchEnabled: Boolean(additionalSearchExpression),
        searchExpression: ""
      });
    }
  };

  onSearchEscape = event => {
    if (event.keyCode === 27) {
      this.toggleSearch();
    }
  };

  onSearchChange = event => {
    const { clearSearchResult } = this.props;

    clearSearchResult(true);

    const { value } = event.target;

    this.setState(
      {
        searchExpression: value
      },
      this.debounceSearch
    );
  };

  onAddEvent = (valueToAdd?: NestedListItem) => {
    const { onAdd, values, searchValues } = this.props;

    if (valueToAdd && valueToAdd.id) {
      const searchResultCount = searchValues.filter(v1 => !values.some(v2 => v1.id === v2.id)).length;
      onAdd([valueToAdd]);

      if (searchResultCount === 1) {
        this.toggleSearch();
      }
    } else if (searchValues) {
      onAdd(searchValues.filter(v1 => !values.some(v2 => v2.id === v1.id)));
      this.toggleSearch();
    }
  };

  onSwitchToggle = (e, checked) => {
    const { additionalAqlEntity } = this.props;

    this.setState(
      {
        toggleEnabled: checked
      },
      () => {
        if (!checked) {
          this.props.onDeleteAll();
          this.toggleSearch();
        } else if (!additionalAqlEntity) {
          setTimeout(() => this.inputRef.current && this.inputRef.current.focus(), 300);
        }
      }
    );
  };

  onDoubleFieldsBlur = () => {
    const { searchExpression, additionalSearchExpression } = this.state;

    if (this.aqlComponentRef.current && this.additionalAqlComponentRef.current) {
      if (!searchExpression) {
        this.aqlComponentRef.current.reset();
      }

      if (!additionalSearchExpression) {
        this.additionalAqlComponentRef.current.reset();
      }

      if (!searchExpression && !additionalSearchExpression) {
        this.toggleSearch();
      }
    }
  };

  onBlur = () => {
    const { searchType, additionalAqlEntity } = this.props;
    const { searchExpression } = this.state;

    if (additionalAqlEntity) {
      this.onDoubleFieldsBlur();
      return;
    }

    if (!searchExpression && searchType !== "immediate") {
      this.toggleSearch();
    }
  };

  onFocus = () => {
    if (!this.state.searchEnabled) {
      this.setState({
        searchEnabled: true
      });
    }
  };

  renderSearchType = React.memo<any>(props => {
    const {
      title,
      classes,
      searchPlaceholder,
      searchType,
      titleCaption,
      hideAddButton,
      searchEnabled,
      searchExpression,
      toggleEnabled,
      disabled,
      formError,
      searchValuesToShow,
      aqlEntity,
      isValidAqlQuery,
      additionalAqlEntity,
      additionalSearchPlaceholder,
      additionalSearchExpression,
      searchTags,
      additionalSearchTags,
      secondaryHeading,
      disableAddAll
    } = props;

    switch (searchType) {
      case "withToggle": {
        return (
          <InputSectionWithToggle
            classes={classes}
            searchEnabled={searchEnabled}
            title={title}
            searchExpression={searchExpression}
            searchPlaceholder={searchPlaceholder}
            searchValuesToShow={searchValuesToShow}
            onSearchChange={this.onSearchChange}
            onAqlSearchChange={this.onAqlSearchChange}
            onSearchEscape={this.onSearchEscape}
            onFocus={this.onFocus}
            onBlur={this.onBlur}
            onAddEvent={this.onAddEvent}
            toggleSearch={this.toggleSearch}
            onSwitchToggle={this.onSwitchToggle}
            validateAql={this.validateAql}
            clearDoubleSearch={this.clearDoubleSearch}
            inputRef={this.inputRef}
            aqlComponentRef={this.aqlComponentRef}
            additionalAqlComponentRef={this.additionalAqlComponentRef}
            titleCaption={titleCaption}
            toggleEnabled={toggleEnabled}
            hideAddButton={hideAddButton}
            formError={formError}
            disabled={disabled}
            aqlEntity={aqlEntity}
            isValidAqlQuery={isValidAqlQuery}
            additionalAqlEntity={additionalAqlEntity}
            additionalSearchPlaceholder={additionalSearchPlaceholder}
            additionalSearchExpression={additionalSearchExpression}
            searchTags={searchTags}
            additionalSearchTags={additionalSearchTags}
            secondaryHeading={secondaryHeading}
            disableAddAll={disableAddAll}
          />
        );
      }

      default: {
        return (
          <InputSection
            classes={classes}
            searchEnabled={searchEnabled}
            title={title}
            searchExpression={searchExpression}
            searchPlaceholder={searchPlaceholder}
            searchValuesToShow={searchValuesToShow}
            searchType={searchType}
            onSearchChange={this.onSearchChange}
            onAqlSearchChange={this.onAqlSearchChange}
            onSearchEscape={this.onSearchEscape}
            onFocus={this.onFocus}
            onBlur={this.onBlur}
            onAddEvent={this.onAddEvent}
            toggleSearch={this.toggleSearch}
            validateAql={this.validateAql}
            clearDoubleSearch={this.clearDoubleSearch}
            inputRef={this.inputRef}
            aqlComponentRef={this.aqlComponentRef}
            additionalAqlComponentRef={this.additionalAqlComponentRef}
            titleCaption={titleCaption}
            hideAddButton={hideAddButton}
            formError={formError}
            disabled={disabled}
            aqlEntity={aqlEntity}
            isValidAqlQuery={isValidAqlQuery}
            additionalAqlEntity={additionalAqlEntity}
            additionalSearchPlaceholder={additionalSearchPlaceholder}
            additionalSearchExpression={additionalSearchExpression}
            searchTags={searchTags}
            additionalSearchTags={additionalSearchTags}
            secondaryHeading={secondaryHeading}
            disableAddAll={disableAddAll}
          />
        );
      }
    }
  });

  renderInnerFormField = React.memo((props: any) => {
    const {
      meta: { error }
    } = props;

    this.setState({
      formError: error
    });

    return <div className="invisible" />;
  });

  render() {
    const {
      classes,
      values,
      searchValues,
      onDelete,
      usePaper,
      pending,
      searchType,
      inlineSecondaryText,
      panelCaption,
      panelItems,
      onChangePanelItem,
      dataRowClass,
      name,
      validate,
      disabled,
      relationTypes,
      formField
    } = this.props;
    const { searchEnabled, searchExpression, additionalSearchExpression } = this.state;

    const searchValuesToShow = searchValues ? searchValues.filter(v1 => !values.some(v2 => v1.id === v2.id)) : [];

    return (
      <>
        {name && <Field name={name} validate={validate} component={this.renderInnerFormField} />}

        <this.renderSearchType {...{ ...this.props, ...this.state, searchValuesToShow }} />

        {searchEnabled
          && (searchExpression.length > 0 || additionalSearchExpression.length > 0 || searchType === "immediate")
          && (searchValuesToShow.length > 0 ? (
            <ListRenderer
              type="search"
              items={searchValuesToShow}
              onClick={this.onAddEvent}
              dataRowClass={dataRowClass}
            />
          ) : pending ? (
            <CircularProgress size={24} thickness={5} className={classes.buttonProgress} />
          ) : (
            <Typography variant="body2" color="textSecondary" className={classes.rowMargin}>
              No results found
            </Typography>
          ))}

        {Boolean(values.length)
          && (usePaper ? (
            <PaperListRenderer
              items={values}
              onDelete={onDelete}
              fade={searchEnabled}
              inlineSecondaryText={inlineSecondaryText}
              panelCaption={panelCaption}
              panelItems={panelItems}
              onChangePanelItem={onChangePanelItem}
              disabled={disabled}
            />
          ) : (
            <ListRenderer
              type="list"
              items={values}
              onDelete={onDelete}
              fade={searchEnabled}
              dataRowClass={dataRowClass}
              disabled={disabled}
              relationTypes={relationTypes}
              formField={formField}
            />
          ))}
      </>
    );
  }
}

export interface NestedListItem {
  id: string;
  entityId?: number;
  entityName?: string;
  primaryText: string | React.ReactNode;
  secondaryText: string | React.ReactNode;
  link?: string;
  active: boolean;
  panelItemIds?: number[];
  relationId?: number;
}

export interface NestedListPanelItem {
  id: number;
  description: string;
}

const mapStateToProps = (state: State) => ({
  entityTags: state.tags.entityTags
});

export default connect<any, any, Props>(mapStateToProps, null)(withStyles(styles)(withStyles(styles)(NestedList)));
