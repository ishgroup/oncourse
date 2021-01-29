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
  aqlEntities?: string[];
  values: NestedListItem[];
  searchValues: NestedListItem[];
  title: string;
  name?: string;
  titleCaption?: string;
  classes?: any;
  searchPlaceholder?: string;
  aqlPlaceholderPrefix?: string;
  onAdd: (items: NestedListItem[]) => void;
  onDelete: (item: NestedListItem, index?: number) => void;
  onDeleteAll?: () => void;
  onSearch: (search: string, entity?: string) => void;
  clearSearchResult: (pending: boolean, entity?: string) => void;
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
  CustomCell?: React.ReactNode;
}

interface NestedListState {
  searchEnabled: boolean;
  searchExpression?: string;
  toggleEnabled?: boolean;
  formError?: string;
  isValidAqlQuery?: boolean;
  searchTags?: Suggestion[];
  selectedAqlEntity?: string;
}

class NestedList extends React.Component<Props, NestedListState> {
  private readonly inputRef: any;

  private readonly aqlComponentRef: any;

  constructor(props) {
    super(props);
    this.inputRef = React.createRef();
    this.aqlComponentRef = React.createRef();

    this.state = {
      searchEnabled: false,
      isValidAqlQuery: false,
      toggleEnabled: Boolean(props.values && props.values.length),
      searchExpression: "",
      selectedAqlEntity: props.aqlEntities ? props.aqlEntities[0] : null,
      formError: null,
      searchTags: []
    };
  }

  componentDidMount() {
    const {
      entityTags,
      aqlEntities
    } = this.props;

    const { selectedAqlEntity } = this.state;


    if (aqlEntities && selectedAqlEntity) {
      const searchTags = entityTags[selectedAqlEntity] && entityTags[selectedAqlEntity].length
        ? getTagNamesSuggestions(entityTags[selectedAqlEntity])
          .filter((t, index, self) => self.findIndex(s => s.label === t.label) === index)
        : [];

      this.setState({
        searchTags
      });
    }
  }

  componentDidUpdate(prevProps, prevState) {
    const {
      resetSearch,
      values,
      formId,
      clearSearchResult,
      entityTags,
      aqlEntities
    } = this.props;

    const { selectedAqlEntity } = this.state;

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
        searchExpression: ""
      });
      clearSearchResult(false, selectedAqlEntity);
    }

    if (aqlEntities && selectedAqlEntity !== prevState.selectedAqlEntity) {
      const searchTags = entityTags[selectedAqlEntity] && entityTags[selectedAqlEntity].length
        ? getTagNamesSuggestions(entityTags[selectedAqlEntity])
          .filter((t, index, self) => self.findIndex(s => s.label === t.label) === index)
        : [];

      this.setState({
        searchTags
      });
    }
  }

  setSelectedEntity = newEntity => {
    const { clearSearchResult } = this.props;
    const { selectedAqlEntity } = this.state;

    clearSearchResult(false, selectedAqlEntity);

    this.setState({
      selectedAqlEntity: newEntity,
      searchExpression: ""
    },
    () => setTimeout(() => this.inputRef.current && this.inputRef.current.focus(), 300));
    this.aqlComponentRef.current.reset();
  }

  validateAql = isValidAqlQuery => {
    this.setState({
      isValidAqlQuery
    });
  };

  triggerSearch = () => {
    const { onSearch, searchType } = this.props;
    const { searchExpression, selectedAqlEntity } = this.state;

    if (searchExpression.length > 0 || searchType === "immediate") {
      onSearch(searchExpression, selectedAqlEntity);
    }
  };

  triggerAqlSearch = () => {
    const { isValidAqlQuery, selectedAqlEntity } = this.state;
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
     clearSearchResult, onSearch
    } = this.props;

    clearSearchResult(true, selectedAqlEntity);

    this.setState({ searchExpression: this.inputRef.current.value }, () => {
      if (value.length > 0 || searchType === "immediate") {
        onSearch(value, selectedAqlEntity);
      }
    });
  };

  debounceSearch = debounce(this.triggerSearch, 500);

  onAqlSearchChange = debounce(this.triggerAqlSearch, 500);

  toggleSearch = () => {
    const {
     clearSearchResult, onToggleSearch, aqlEntities
    } = this.props;

    const { searchEnabled, selectedAqlEntity } = this.state;

    if (!searchEnabled) {
      if (typeof onToggleSearch === "function") {
        onToggleSearch();
      }

      if (!aqlEntities) {
        setTimeout(() => {
          this.inputRef.current.focus();
        }, 300);
      }
    }

    if (searchEnabled) {
      clearSearchResult(false, selectedAqlEntity);
    }

    if (aqlEntities) {
      if (searchEnabled) {
        this.aqlComponentRef.current.reset();
      } else {
        setTimeout(() => this.inputRef.current && this.inputRef.current.focus(), 300);
      }
    }

    this.setState({
      searchEnabled: !searchEnabled,
      searchExpression: ""
    });
  };

  onSearchEscape = event => {
    if (event.keyCode === 27) {
      this.toggleSearch();
    }
  };

  onAqlSearchClear = () => {
    const { clearSearchResult } = this.props;
    const { selectedAqlEntity } = this.state;

    clearSearchResult(true, selectedAqlEntity);

    this.setState(
      {
        searchExpression: ""
      },
      () => this.aqlComponentRef.current && this.aqlComponentRef.current.reset()
    );
  }

  onSearchChange = event => {
    const { clearSearchResult } = this.props;
    const { selectedAqlEntity } = this.state;

    clearSearchResult(true, selectedAqlEntity);

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
    this.setState(
      {
        toggleEnabled: checked
      },
      () => {
        if (!checked) {
          this.props.onDeleteAll();
          this.toggleSearch();
        } else {
          setTimeout(() => this.inputRef.current && this.inputRef.current.focus(), 300);
        }
      }
    );
  };

  onBlur = () => {
    const { searchType, aqlEntities } = this.props;
    const { searchExpression } = this.state;

    if (!searchExpression && searchType !== "immediate" && (aqlEntities ? aqlEntities.length === 1 : true)) {
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
      aqlPlaceholderPrefix,
      searchType,
      titleCaption,
      hideAddButton,
      searchEnabled,
      searchExpression,
      toggleEnabled,
      disabled,
      formError,
      searchValuesToShow,
      selectedAqlEntity,
      isValidAqlQuery,
      searchTags,
      secondaryHeading,
      disableAddAll,
      aqlEntities
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
            aqlPlaceholderPrefix={aqlPlaceholderPrefix}
            searchValuesToShow={searchValuesToShow}
            onAqlSearchClear={this.onAqlSearchClear}
            onSearchChange={this.onSearchChange}
            onAqlSearchChange={this.onAqlSearchChange}
            onSearchEscape={this.onSearchEscape}
            onFocus={this.onFocus}
            onBlur={this.onBlur}
            onAddEvent={this.onAddEvent}
            toggleSearch={this.toggleSearch}
            onSwitchToggle={this.onSwitchToggle}
            validateAql={this.validateAql}
            inputRef={this.inputRef}
            aqlComponentRef={this.aqlComponentRef}
            titleCaption={titleCaption}
            toggleEnabled={toggleEnabled}
            hideAddButton={hideAddButton}
            formError={formError}
            disabled={disabled}
            setSelectedEntity={this.setSelectedEntity}
            aqlEntity={selectedAqlEntity}
            isValidAqlQuery={isValidAqlQuery}
            searchTags={searchTags}
            secondaryHeading={secondaryHeading}
            disableAddAll={disableAddAll}
            aqlEntities={aqlEntities}
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
            aqlPlaceholderPrefix={aqlPlaceholderPrefix}
            searchValuesToShow={searchValuesToShow}
            searchType={searchType}
            onAqlSearchClear={this.onAqlSearchClear}
            onSearchChange={this.onSearchChange}
            onAqlSearchChange={this.onAqlSearchChange}
            onSearchEscape={this.onSearchEscape}
            onFocus={this.onFocus}
            onBlur={this.onBlur}
            onAddEvent={this.onAddEvent}
            toggleSearch={this.toggleSearch}
            validateAql={this.validateAql}
            inputRef={this.inputRef}
            aqlComponentRef={this.aqlComponentRef}
            titleCaption={titleCaption}
            hideAddButton={hideAddButton}
            formError={formError}
            disabled={disabled}
            aqlEntity={selectedAqlEntity}
            setSelectedEntity={this.setSelectedEntity}
            isValidAqlQuery={isValidAqlQuery}
            searchTags={searchTags}
            secondaryHeading={secondaryHeading}
            disableAddAll={disableAddAll}
            aqlEntities={aqlEntities}
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
      CustomCell
    } = this.props;

    const { searchEnabled, searchExpression } = this.state;

    const searchValuesToShow = searchValues ? searchValues.filter(v1 => !values.some(v2 => v1.id === v2.id)) : [];

    return (
      <>
        {name && <Field name={name} validate={validate} component={this.renderInnerFormField} />}

        <this.renderSearchType {...{ ...this.props, ...this.state, searchValuesToShow }} />

        {searchEnabled
          && (searchExpression.length > 0 || searchType === "immediate")
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
              CustomCell={CustomCell}
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
