/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Typography } from '@mui/material';
import CircularProgress from '@mui/material/CircularProgress';
import $t from '@t';
import debounce from 'lodash.debounce';
import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Field, Validator } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { SIMPLE_SEARCH_QUOTES_REGEX, SIMPLE_SEARCH_REGEX, TAGS_REGEX } from '../../../../constants/Config';
import { getTagNamesSuggestions } from '../../../../containers/tags/utils';
import { QueryFieldSuggestion } from '../../../../model/common/Fields';
import { State } from '../../../../reducers/state';
import { InputSection, InputSectionWithToggle } from './components/InputSections';
import ListRenderer from './components/ListRenderer';
import PaperListRenderer, { PanelItemChangedMessage } from './components/PaperListRenderer';

const styles = theme => ({
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
  aqlQueryError: boolean;
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
  validate?: Validator | Validator[];
  entityTags?: any;
  CustomCell?: any;
}

interface NestedListState {
  searchEnabled: boolean;
  searchExpression?: string;
  toggleEnabled?: boolean;
  formError?: string;
  searchTags?: QueryFieldSuggestion[];
  selectedAqlEntity?: string;
}

const InnerFormField = React.memo((props: any) => {
  const {
    input: { name },
    meta: { error },
    setFormError
  } = props;

  useEffect(() => {
    if (setFormError) setFormError(error);
  }, [error]);

  return <div className="invisible" id={name}/>;
});

class NestedList extends React.Component<Props, NestedListState> {
  private readonly inputRef: any;

  private readonly aqlComponentRef: any;

  constructor(props) {
    super(props);
    this.inputRef = React.createRef();
    this.aqlComponentRef = React.createRef();

    this.state = {
      searchEnabled: false,
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
  };

  triggerSearch = () => {
    const { onSearch, searchType } = this.props;
    const { searchExpression, selectedAqlEntity } = this.state;

    if (searchExpression.length > 0 || searchType === "immediate") {
      onSearch(searchExpression, selectedAqlEntity);
    }
  };

  triggerAqlSearch = () => {
    const { selectedAqlEntity } = this.state;
    const { searchType } = this.props;

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
    }

    if (searchEnabled) {
      clearSearchResult(false, selectedAqlEntity);
    }

    if (aqlEntities) {
      if (searchEnabled) {
        this.aqlComponentRef.current.reset();
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
  };

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
        }
      }
    );
  };

  onFocus = () => {
    if (!this.state.searchEnabled) {
      this.setState({
        searchEnabled: true
      });
    }
  };

  renderSearchType = React.memo<Props & NestedListState & { searchValuesToShow: any }>(props => {
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
      aqlQueryError,
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
            onAddEvent={this.onAddEvent}
            toggleSearch={this.toggleSearch}
            onSwitchToggle={this.onSwitchToggle}
            inputRef={this.inputRef}
            aqlComponentRef={this.aqlComponentRef}
            titleCaption={titleCaption}
            toggleEnabled={toggleEnabled}
            formError={formError}
            disabled={disabled}
            setSelectedEntity={this.setSelectedEntity}
            aqlEntity={selectedAqlEntity}
            aqlQueryError={aqlQueryError}
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
            onAqlSearchClear={this.onAqlSearchClear}
            onSearchChange={this.onSearchChange}
            onAqlSearchChange={this.onAqlSearchChange}
            onSearchEscape={this.onSearchEscape}
            onFocus={this.onFocus}
            onAddEvent={this.onAddEvent}
            toggleSearch={this.toggleSearch}
            inputRef={this.inputRef}
            aqlComponentRef={this.aqlComponentRef}
            titleCaption={titleCaption}
            hideAddButton={hideAddButton}
            formError={formError}
            disabled={disabled}
            aqlEntity={selectedAqlEntity}
            setSelectedEntity={this.setSelectedEntity}
            aqlQueryError={aqlQueryError}
            searchTags={searchTags}
            secondaryHeading={secondaryHeading}
            disableAddAll={disableAddAll}
            aqlEntities={aqlEntities}
          />
        );
      }
    }
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
        {name && (
          <Field
            name={name}
            validate={validate}
            component={InnerFormField}
            setFormError={formError => this.setState({ formError })}
          />
        )}

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
            <CircularProgress size={24} thickness={5} className={classes.buttonProgress}/>
          ) : (
            <Typography variant="body2" color="textSecondary" className={classes.rowMargin}>
              {$t('no_results_found')}
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

export default connect<any, any, Props>(mapStateToProps)(withStyles(NestedList, styles));
