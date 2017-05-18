import * as React from "react";
import Autosuggest from "react-autosuggest";
import {connect} from "react-redux";
import {IshActions} from "../../constants/IshActions";
import {IshState} from "../../services/IshState";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {TextField} from "./TextField";
import {selectSuggestions, Suggestion} from "../../selectors/autocomplete";
import {EventHandler} from "react";

class AutocompleteFieldComponent extends React.Component<AutocompleteFieldProps, {}> {
  constructor() {
    super();
  }

  onSuggestionsFetchRequested = ({value}) => {
    this.props.requestSuggestions(this.props.autocomplete, value);
  };

  onSuggestionsClearRequested = () => {
    this.props.clearSuggestions(this.props.autocomplete);
  };

  render() {
    const {suggestions, input, meta, label} = this.props;

    return (
      <Autosuggest
        id={this.props.autocomplete}
        theme={getTheme()}
        suggestions={suggestions}
        onSuggestionsFetchRequested={this.onSuggestionsFetchRequested}
        onSuggestionsClearRequested={this.onSuggestionsClearRequested}
        renderInputComponent={renderInputComponent}
        shouldRenderSuggestions={shouldRenderSuggestions}
        renderSuggestionsContainer={renderSuggestionsContainer}
        getSuggestionValue={getSuggestionValue}
        renderSuggestion={renderSuggestion}
        inputProps={{
          name: input.name,
          value: input.value,
          onChange: () => {},
          label,
          input,
          meta
        }}
      />
    );
  }
}

export const AutocompleteField = connect(mapStateToProps, mapDispatchToProps)(AutocompleteFieldComponent);

export interface AutocompleteFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps {
  autocomplete: string;
  suggestions: Suggestion[];
  requestSuggestions: (key: string, text: string) => void;
  clearSuggestions: (key: string) => void;
}

function renderInputComponent(inputProps) {

  const custom = {
    onBlur: wrapHandlers(inputProps.onBlur, inputProps.input.onBlur),
    onFocus: wrapHandlers(inputProps.onFocus, inputProps.input.onFocus),
    onDrop: wrapHandlers(inputProps.onDrop, inputProps.input.onDrop),
    onChange: wrapHandlers(inputProps.onChange, inputProps.input.onChange),
  };

  const props = {
    input: {...inputProps.input, ...custom},
    meta: inputProps.meta,
    label: inputProps.label,
    required: inputProps.required
  };

  return (
    <div>
      <TextField {...props} type="text"/>
    </div>
  );
}

function shouldRenderSuggestions(value) {
  return value.trim().length > 1;
}

function getSuggestionValue(suggestion: Suggestion) {
  return suggestion.value;
}

function renderSuggestion(suggestion: Suggestion) {
  return (
    <span>
      {suggestion.key}
    </span>
  );
}

function renderSuggestionsContainer({containerProps, children, query}) {
  return (
    <div {...containerProps}>
      <ul>
        {children}
      </ul>
    </div>
  );
}

function mapStateToProps(state: IshState, ownProps: AutocompleteFieldProps) {
  return {
    suggestions: selectSuggestions(state, ownProps.autocomplete)
  };
}

function mapDispatchToProps(dispatch) {
  return {
    requestSuggestions(key: string, text: string) {
      dispatch({
        type: IshActions.AUTOCOMPLETE,
        payload: {key, text}
      });
    },
    clearSuggestions(key: string) {
      dispatch({
        type: IshActions.AUTOCOMPLETE_CLEAR,
        payload: {key}
      });
    }
  };
}

function wrapHandlers(...handlers: EventHandler<any>[]): EventHandler<any> {
  return function (e: Event) {
    handlers.forEach(handler => handler(e));
  };
}

const theme = {
  container: "autocomplete__container",
  containerOpen: "autocomplete__container--open",
  input: "autocomplete__input",
  inputOpen: "autocomplete__input--open",
  inputFocused: "autocomplete__input--focused",
  suggestionsContainer: "autocomplete__suggestions-container",
  suggestionsContainerOpen: "autocomplete__suggestions-container--open",
  suggestionsList: "autocomplete__suggestions-list",
  suggestion: "autocomplete__suggestion",
  suggestionFirst: "autocomplete__suggestion--first",
  suggestionHighlighted: "autocomplete__suggestion--highlighted",
  sectionContainer: "autocomplete__section-container",
  sectionContainerFirst: "autocomplete__section-container--first",
  sectionTitle: "autocomplete__section-title"
};

function getTheme() {
  return theme;
}

