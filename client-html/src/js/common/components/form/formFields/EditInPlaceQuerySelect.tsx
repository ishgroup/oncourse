/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { AqlLexer } from '@aql/AqlLexer';
import { AqlParser } from '@aql/AqlParser';
import * as Entities from '@aql/queryLanguageModel';
import DateRange from '@mui/icons-material/DateRange';
import QueryBuilder from '@mui/icons-material/QueryBuilder';
import { ListItemButton } from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import { DatePicker, TimePicker as Time } from '@mui/x-date-pickers';
import { CodeCompletionCore } from 'antlr4-c3';
import { ANTLRInputStream, CommonTokenStream } from 'antlr4ts';
import clsx from 'clsx';
import { format as formatDate } from 'date-fns';
import {
  DD_MM_YYYY_SLASHED,
  getHighlightedPartLabel,
  HH_MM_COLONED,
  makeAppStyles,
  stubComponent,
  useSelectStyles
} from 'ish-ui';
import React, { useCallback, useEffect, useImperativeHandle, useLayoutEffect, useMemo, useRef, useState } from 'react';
import {
  FILTER_TAGS_REGEX,
  SIMPLE_SEARCH_QUOTES_AND_NO_WHITESPACE_REGEX,
  SIMPLE_SEARCH_QUOTES_REGEX,
  TAGS_REGEX
} from '../../../../constants/Config';
import { COMMON_PLACEHOLDER } from '../../../../constants/Forms';
import { CustomFieldTypesState } from '../../../../containers/entities/customFieldTypes/reducers/state';
import { EditInPlaceQueryFieldProps, QueryFieldSuggestion } from '../../../../model/common/Fields';
import getCaretCoordinates from '../../../utils/DOM/getCaretCoordinates';
import { useAppSelector } from '../../../utils/hooks';

const useQueryStyles = makeAppStyles()(theme => ({
  inputRoot: {
    '&&&': {
      paddingRight: 0
    }
  },
  queryMenuItem: {
    minHeight: "unset",
    fontSize: "0.9rem"
  },
  menuCorner: {
    "&:after": {
      content: "''",
      width: "16px",
      height: "16px",
      background: theme.palette.background.paper,
      position: "absolute",
      boxShadow: "0 0 0 1px hsla(0,0%,0%,0.1), 0 4px 11px hsla(0,0%,0%,0.1)",
      bottom: "-1px"
    }
  },
  cornerLeft: {
    "&:after": {
      transform: "rotate(90deg) skewX(45deg)"
    }
  },
  cornerRight: {
    "&:after": {
      transform: "rotate(90deg) skewX(-45deg)",
      right: "0px"
    }
  },
  menuShadow: {
    boxShadow: "0 0 0 1px hsla(0,0%,0%,0.1), 0 4px 11px hsla(0,0%,0%,0.1)"
  },
  editable: {
    color: theme.palette.text.primaryEditable,
    fontWeight: 400,
  }
}));

const TimePicker: any = Time;

// workaround for proper code minifying
const ENUM_CONSTRUCTOR_NAME = Entities.Enum.prototype.constructor.name;

const completeSuggestions = (
  token: string,
  operatorsFilter: string,
  pathFilter: string,
  rootEntity: string,
  filterTags?: QueryFieldSuggestion[],
  tagSuggestions?: QueryFieldSuggestion[],
  customFields?: string[]
) => {
  let variants = [token];

  switch (token.replace(/'/g, "")) {
    case "AND": {
      variants = ["and"];
      break;
    }
    case "OR": {
      variants = ["or"];
      break;
    }
    case "NOT": {
      variants = ["not"];
      break;
    }
    case "EQ": {
      variants = ["is"];
      break;
    }
    case "NE": {
      variants = ["not is"];
      break;
    }
    case "BooleanLiteral": {
      variants = operatorsFilter === "Boolean" ? ["true", "false"] : [];
      break;
    }
    case "Identifier": {
      const match = Entities[pathFilter || rootEntity];

      variants = (operatorsFilter && operatorsFilter !== ENUM_CONSTRUCTOR_NAME) || !match ? [] : Object.keys(match);

      if (!pathFilter && !operatorsFilter && customFields) {
        variants = [...variants, ...customFields];
      }

      // Audits System user exception
      if (pathFilter === "SystemUser") {
        variants = [...variants, "me"];
      }

      break;
    }
    case "@": {
      variants = filterTags && filterTags.length ? [token] : [];
      break;
    }
    case "#": {
      variants = tagSuggestions && tagSuggestions.length ? [token] : [];
      break;
    }
    case "~":
    case "contains":
    case "starts with":
    case "!~":
    case "not contains":
    case "not starts with": {
      variants = ["String", "RichText"].includes(operatorsFilter) ? [token] : [];
      break;
    }
    case "ends with":
    case "not ends with": {
      variants = operatorsFilter === "String" ? [token] : [];
      break;
    }
    case "LIKE": {
      variants = ["String", "RichText"].includes(operatorsFilter) ? ["~", "like"] : [];
      break;
    }
    case "NOT_LIKE": {
      variants = ["String", "RichText"].includes(operatorsFilter) ? ["!~", "not like"] : [];
      break;
    }
    case "<=":
    case ">=":
    case "!==":
    case "not is":
    case "<":
    case ">":
    case "-":
    case "+":
    case "*":
    case "/":
    case "%":
    case "in": {
      variants = operatorsFilter === "Date" || operatorsFilter === "Number" ? [token] : [];
      break;
    }
    case "today":
    case "yesterday":
    case "tomorrow":
    case "last week":
    case "this week":
    case "next week":
    case "last month":
    case "this month":
    case "next month":
    case "last year":
    case "this year":
    case "next year":
    case "after":
    case "before":
    case "now": {
      variants = operatorsFilter === "Date" ? [token] : [];
      break;
    }
    case "MainDateFormat": {
      variants = operatorsFilter === "Date" ? ["DATE"] : [];
      break;
    }
    case "Time24": {
      variants = ["TIME"];
      break;
    }
    case "Time12":
    case "IsoDateFormat":
    case "IntegerLiteral":
    case "FloatingPointLiteral":
    case "SingleQuotedStringLiteral":
    case "DoubleQuotedStringLiteral":
    case "RichTextLiteral": {
      variants = [];
      break;
    }
    case "NullLiteral": {
      variants = ["empty"];
      break;
    }
  }

  return variants.map(i => ({
    token,
    value: i.replace(/[']/g, ""),
    label: i.replace(/[']/g, "")
  }));
};

const getPickerValue = (pickerOpened, pickerValue) => pickerOpened === "DATE" ? formatDate(pickerValue, DD_MM_YYYY_SLASHED) + " " : formatDate(pickerValue, HH_MM_COLONED) + " ";

interface OwnState {
  value: object[];
  options: QueryFieldSuggestion[];
  menuIsOpen: boolean;
  pickerOpened: "DATE" | "TIME";
  pickerValue: Date;
  inputValue: string;
  searchValue: string;
  caretCoordinates: any;
}

interface InnerProps extends EditInPlaceQueryFieldProps {
  customFieldTypes?: CustomFieldTypesState;
}

/**
 * Class-like state container: every update is applied to a ref synchronously, so that the callbacks below always
 * read the freshest value, and an optional callback is flushed once the update is committed to the DOM.
 */
function useStateWithCallback<S extends object>(initialState: S) {
  const [state, setState] = useState<S>(initialState);

  const stateRef = useRef<S>(initialState);
  const callbacks = useRef<(() => void)[]>([]);

  useLayoutEffect(() => {
    if (!callbacks.current.length) return;

    const pending = callbacks.current;
    callbacks.current = [];
    pending.forEach(cb => cb());
  });

  const updateState = useCallback((patch: Partial<S> | ((prev: S) => Partial<S>), callback?: () => void) => {
    stateRef.current = {
      ...stateRef.current,
      ...(typeof patch === "function" ? patch(stateRef.current) : patch)
    };

    if (callback) {
      callbacks.current.push(callback);
    }

    setState(stateRef.current);
  }, []);

  return [state, updateState, stateRef] as const;
}

const EditInPlaceQuerySelect = React.forwardRef<any, EditInPlaceQueryFieldProps>((props, ref) => {
  const {
    input,
    meta,
    label,
    disabled,
    className,
    inline,
    placeholder,
    endAdornment,
    disableUnderline,
    disableErrorText,
    fieldClasses = {},
    onClick
  } = props;

  const customFieldTypes = useAppSelector(state => state.customFieldTypes);

  const { classes: selectClasses } = useSelectStyles();
  const { classes: queryClasses } = useQueryStyles();

  const classes = useMemo(() => ({ ...selectClasses, ...queryClasses }), [selectClasses, queryClasses]);

  const [state, updateState, stateRef] = useStateWithCallback<OwnState>({
    value: [],
    options: [],
    menuIsOpen: false,
    pickerOpened: null,
    pickerValue: null,
    inputValue: (input && input.value) || "",
    searchValue: "",
    caretCoordinates: null
  });

  // always holds the latest props, so that every callback below can stay referentially stable
  const propsRef = useRef<InnerProps>(null);
  propsRef.current = { ...props, customFieldTypes, classes };

  const inputNode = useRef<any>(null);
  const pathFilter = useRef<string>(null);
  const operatorsFilter = useRef<string>(null);
  const simpleSearchChecked = useRef<boolean>(false);
  const autoQuotesAdded = useRef<boolean>(false);
  const dateAnchor = useRef<any>(null);

  const filterOptions = useCallback((item: QueryFieldSuggestion) => item.label
    .toLowerCase()
    .trim()
    .startsWith(stateRef.current.searchValue.trim().toLowerCase()), []);

  const filterOptionsInner = useCallback(options => options.filter(filterOptions), []);

  const parseInputString = useCallback(val => {
    if (!val) {
      val = "";
    }
    const simpleSearchQuotesMatch = val.match(SIMPLE_SEARCH_QUOTES_REGEX);
    const tagMatch = val.match(TAGS_REGEX);
    const filterMatch = val.match(FILTER_TAGS_REGEX);

    let input = simpleSearchQuotesMatch ? `~${val}` : val;

    if (tagMatch) {
      input = input.replace(TAGS_REGEX, v => `#"${v.replace("#", "")}"`);
    }

    if (filterMatch) {
      input = input.replace(FILTER_TAGS_REGEX, v => `@"${v.replace("@", "")}"`);
    }

    input = input.length ? input : `#""`;

    const chars = new ANTLRInputStream(input);
    const lexer = new AqlLexer(chars);
    const tokens = new CommonTokenStream(lexer);
    const parser = new AqlParser(tokens);
    parser.query();

    return { tokens, parser } as any;
  }, []);

  const setMenuPosition = useCallback(position => {
    updateState({
      caretCoordinates: getCaretCoordinates(inputNode.current, position)
    });
  }, []);

  const setCaret = useCallback(() => {
    const el = inputNode.current;
    const selectionEnd = el.value.length;

    const isScrollable = el.scrollWidth > el.clientWidth;

    if (isScrollable) {
      el.scrollLeft = el.scrollWidth;
    }
    if (el.setSelectionRange) {
      el.focus();
      el.setSelectionRange(selectionEnd, selectionEnd);
    }

    if (propsRef.current.inline) {
      if (isScrollable) {
        updateState({
          caretCoordinates: {
            left: el.clientWidth
          }
        });
      } else {
        setMenuPosition(selectionEnd);
      }
    }
  }, []);

  const getAutocomplete = useCallback((input, position?) => {
    const { parser } = parseInputString(input);
    const {
      rootEntity, filterTags, tagSuggestions, customFields
    } = propsRef.current;

    const core = new CodeCompletionCore(parser);
    core.showRuleStack = true;
    core.ignoredTokens = new Set([AqlLexer.EOF, AqlLexer.SEPARATOR, AqlLexer.T__17]);
    const candidates = core.collectCandidates(
      typeof position === "number" ? position : 0
    );
    const keywords: any = [];

    if (operatorsFilter.current === "SEPARATOR" && input[input.length - 1] !== " ") {
      operatorsFilter.current = null;

      updateState(
        prev => ({
          inputValue: prev.inputValue + "."
        }),
        setCaret
      );

      return getAutocomplete(input + ".", position);
    }

    for (const candidate of candidates.tokens) {
      const suggestions = completeSuggestions(
        parser.vocabulary.getDisplayName(candidate[0]),
        operatorsFilter.current,
        pathFilter.current,
        rootEntity,
        filterTags,
        tagSuggestions,
        customFields
      );

      if (suggestions[0] && (suggestions[0].value === "DATE" || suggestions[0].value === "TIME")) {
        keywords.splice(0, 0, suggestions);
      } else {
        keywords.push(suggestions);
      }
    }

    let variants = [];

    keywords.forEach(keyword => {
      keyword.forEach(list => {
        variants.push(list);
      });
    });

    const hasSuggestionsForIncomplete = variants.some(
      v => !["AND", "OR", "'+'", "'-'", "'%'", "'*'", "'/'", "','", "Time12", "Time24"].includes(v.token)
    );

    if (hasSuggestionsForIncomplete) {
      variants = variants.filter(v => !["AND", "OR"].includes(v.token));
    }

    return variants;
  }, []);

  const setIdentifierFilters = useCallback(tokenText => {
    const { rootEntity, customFields, customFieldTypes } = propsRef.current;

    if (customFields && customFields.includes(tokenText)) {

      const types = rootEntity === "ProductItem"
        ? [...(customFieldTypes?.types["Article"] || []), ...(customFieldTypes?.types["Voucher"] || []), ...(customFieldTypes?.types["Membership"] || [])]
        : customFieldTypes?.types[rootEntity];

      const isDateField = types?.some(t => t.fieldKey === tokenText && ["Date time", "Date"].includes(t.dataType));
      operatorsFilter.current = isDateField ? "Date" : "String";
      return;
    }

    let propType;

    if (Entities[pathFilter.current]) {
      propType = Entities[pathFilter.current][tokenText] || Entities[pathFilter.current].hasOwnProperty(tokenText);
    } else {
      propType = Entities[rootEntity][tokenText];
    }

    if (Entities[propType]) {
      pathFilter.current = propType;

      if (Entities[propType].constructor.name === ENUM_CONSTRUCTOR_NAME) {
        operatorsFilter.current = ENUM_CONSTRUCTOR_NAME;
        return;
      }

      operatorsFilter.current = "SEPARATOR";
      return;
    }

    if (propType) {
      operatorsFilter.current = propType;
    }
  }, []);

  const updateAutocomplete = useCallback(value => {
    const { tokens, parser } = parseInputString(value);
    const { filterTags, tagSuggestions } = propsRef.current;
    const { options } = stateRef.current;

    const parsedTokens = tokens.tokens;

    let lastIdentifier = null;

    for (const token of [...parsedTokens].reverse()) {
      if (token.type === AqlLexer.Identifier && token.text !== stateRef.current.searchValue) {
        lastIdentifier = token;
        break;
      }
    }

    if (lastIdentifier) {
      setIdentifierFilters(lastIdentifier.text);
    }

    if (!lastIdentifier && pathFilter.current) {
      pathFilter.current = "";
    }

    const lastToken = parsedTokens[parsedTokens.length - 2];
    const lastTokenType = lastToken && parser.vocabulary.getDisplayName(lastToken._type);

    const preLastToken = parsedTokens[parsedTokens.length - 3];
    const preLastTokenType = preLastToken && parser.vocabulary.getDisplayName(preLastToken._type);

    const prePreLastToken = parsedTokens[parsedTokens.length - 4];
    const prePreLastTokenType = prePreLastToken && parser.vocabulary.getDisplayName(prePreLastToken._type);

    if (preLastTokenType === "'@'" || preLastTokenType === "'#'") {
      updateState({
        searchValue: stateRef.current.searchValue.replace(/"/g, "")
      });
      return;
    }

    if (lastTokenType === "'@'") {
      updateState({
        searchValue: "",
        options: (filterTags || [])
      });
      return;
    }

    if (lastTokenType === "'#'") {
      updateState({
        searchValue: "",
        options: (tagSuggestions || [])
      });
      return;
    }

    if (
      ["DoubleQuotedStringLiteral", "SingleQuotedStringLiteral", "RichTextLiteral"].includes(lastTokenType)
      && value.match(SIMPLE_SEARCH_QUOTES_AND_NO_WHITESPACE_REGEX)
    ) {
      simpleSearchChecked.current = false;

      const inputValue = value.replace(/[",']/g, "");

      updateState({
        inputValue,
        searchValue: inputValue,
        options: getAutocomplete("", 0).filter(filterOptions)
      });

      return;
    }

    if (preLastTokenType === "AND" || preLastTokenType === "OR") {
      operatorsFilter.current = "";
      pathFilter.current = "";
    }

    if (lastTokenType === "WS") {
      if (autoQuotesAdded.current && preLastTokenType === "Identifier" && ["String", "RichText"].includes(operatorsFilter.current)) {
        autoQuotesAdded.current = false;
      }

      if (
        !autoQuotesAdded.current
        && ["String", "RichText"].includes(operatorsFilter.current)
        && !["Identifier", "DoubleQuotedStringLiteral", "SingleQuotedStringLiteral", "RichTextLiteral"].includes(preLastTokenType)
      ) {
        const { inputValue } = stateRef.current;

        autoQuotesAdded.current = true;

        const position = inputValue.length + 1;

        updateState(
          {
            inputValue: inputValue + `""`,
            options: []
          },
          () => {
            inputNode.current.setSelectionRange(position, position);
            setMenuPosition(position);
          }
        );

        return;
      }

      updateState(
        {
          searchValue: ""
        },
        () => {
          updateState({
            options: getAutocomplete(value, inputNode.current?.selectionStart).filter(filterOptions)
          });
        }
      );

      return;
    }

    if (lastToken && lastToken.text === ".") {
      operatorsFilter.current = "";
      updateState(
        {
          searchValue: ""
        },
        () => {
          updateState({
            options: getAutocomplete(value).filter(filterOptions)
          });
        }
      );

      return;
    }

    if (lastTokenType === "Identifier") {
      if (stateRef.current.searchValue !== lastToken.text) {
        updateState({
          options: getAutocomplete(value).filter(filterOptions)
        });
      } else {
        if (!lastIdentifier) {
          operatorsFilter.current = "";
        }

        if (options.length === 1 && options[0].value === lastToken.text) {
          setIdentifierFilters(lastToken.text);
          if (operatorsFilter.current === "SEPARATOR") {
            updateState({
              searchValue: "",
              options: getAutocomplete(lastToken.text).filter(filterOptions)
            });
          }
          return;
        }

        if (prePreLastTokenType === "AND" || prePreLastTokenType === "OR") {
          operatorsFilter.current = "";
          pathFilter.current = "";
        }

        if (operatorsFilter.current === "SEPARATOR" || preLastTokenType === "'.'") {
          operatorsFilter.current = "";
        }

        updateState({
          options: getAutocomplete(value.replace(new RegExp(lastToken.text + "$"), "")).filter(filterOptions)
        });
      }
    }
  }, []);

  const performSearch = useCallback(() => {
    const { performSearch } = propsRef.current;

    if (performSearch) {
      performSearch();
    }
  }, []);

  const onInputClick = useCallback(e => {
    if (propsRef.current.inline) {
      const isScrollable = inputNode.current.scrollWidth > inputNode.current.clientWidth;

      if (isScrollable) {
        updateState({
          caretCoordinates: {
            left: e.offsetX
          }
        });
      } else {
        setMenuPosition(inputNode.current.selectionEnd);
      }
    }
  }, []);

  const setInputNode = useCallback(node => {
    if (node) {
      inputNode.current = node;

      node.addEventListener("click", onInputClick);

      const { setInputNode, inline } = propsRef.current;

      if (setInputNode && inline) {
        setInputNode(node);
      }
    }
  }, []);

  const onBlur = useCallback(() => {
    if (stateRef.current.pickerOpened) return;

    const { onBlur } = propsRef.current;

    if (onBlur) {
      onBlur();
    }

    updateState({
      menuIsOpen: false
    });
  }, []);

  const onFocus = useCallback(e => {
    const { inline, input, onFocus } = propsRef.current;

    if (onFocus) {
      onFocus();
    }

    if (!inline) {
      input.onFocus(e);
    }

    if (simpleSearchChecked.current && !inputNode.current.value) {
      simpleSearchChecked.current = false;
    }

    // expand animation timeout
    setTimeout(() => {
      updateState(
        {
          menuIsOpen: true
        },
        () => {
          setCaret();
          updateAutocomplete(inputNode.current.value || "");
        }
      );
    }, 300);
  }, []);

  const openPicker = useCallback(pickerOpened => {
    updateState({
      pickerOpened
    });
  }, []);

  const closePicker = useCallback(() => {
    updateState({
      pickerOpened: null,
      pickerValue: null
    });

    updateAutocomplete(stateRef.current.inputValue);
    propsRef.current.performSearch && propsRef.current.performSearch();
  }, []);

  const handlePickerChange = useCallback(newPickerValue => {
    const { pickerOpened, pickerValue } = stateRef.current;

    updateState({ pickerValue: newPickerValue });

    if (!newPickerValue) return;

    const dateTimeCurrent = getPickerValue(pickerOpened, newPickerValue);
    const dateTimePrev = pickerValue && getPickerValue(pickerOpened, pickerValue);

    const inputValue = stateRef.current.inputValue.replace(dateTimePrev, '') + dateTimeCurrent;

    updateState({
      inputValue
    });
  }, []);

  const handleChange = useCallback((e, value, action) => {
    const { inline, input, rootEntity } = propsRef.current;

    if (action === "clear" || action === "remove-option") {
      operatorsFilter.current = "";
      pathFilter.current = "";

      updateState(
        {
          inputValue: ""
        },
        performSearch
      );

      if (!inline) {
        input.onChange("");
      }

      return;
    }

    if (!value || (value && !value[0])) return;

    let propType;

    if (value[0].label === "DATE" || value[0].label === "TIME") {
      openPicker(value[0].label);
      return;
    }

    if (value[0].token === "AND" || value[0].token === "OR") {
      operatorsFilter.current = "";
    }

    if (value[0].token === "Identifier") {
      propType = (Entities[pathFilter.current] && Entities[pathFilter.current][value[0].value])
        || Entities[rootEntity][value[0].value];
    }

    const { inputValue: currentValue, searchValue } = stateRef.current;

    let inputValue = (currentValue || "").replace(
        new RegExp((searchValue.match(/[+*()]/) ? "\\" : "") + searchValue + "$"),
        searchValue.match(/\s/) ? " " : ""
      )
      + value[0].value
      + (value[0].token === "SEPARATOR" || value[0].token === "'@'" || value[0].token === "'#'"
        ? ""
        : Entities[propType] && Entities[propType].constructor.name !== ENUM_CONSTRUCTOR_NAME
          ? ""
          : " ");

    if (value[0].queryPrefix) {
      const tagStr = "#" + value[0].value;
      inputValue = inputValue.replace(tagStr, `${value[0].queryPrefix} ${tagStr}`);
    }

    if (!simpleSearchChecked.current) {
      simpleSearchChecked.current = true;
    }

    updateState(
      {
        inputValue,
        searchValue: ""
      },
      () => {
        setCaret();
        updateAutocomplete(inputValue);
        performSearch();
        if (!inline) input.onChange(inputValue);
      }
    );
  }, []);

  const handleInputChange = useCallback(e => {
    const { input, inline } = propsRef.current;

    const value = e.target.value;

    if (!value && !value.match(/\s/)) {
      simpleSearchChecked.current = false;
    }

    const { tokens: { tokens } } = parseInputString(value);

    if (!value) {
      updateState(
        {
          inputValue: "",
          searchValue: ""
        },
        () => {
          setMenuPosition(inputNode.current.selectionStart);
          operatorsFilter.current = "";
          pathFilter.current = "";
          updateState(
            {
              options: getAutocomplete("").filter(filterOptions)
            },
            performSearch
          );
          if (!inline) input.onChange("");
        }
      );
      return;
    }

    const lastToken = tokens[tokens.length - 2];

    updateState(
      {
        inputValue: value,
        searchValue: lastToken ? lastToken.text : ""
      },
      () => {
        setMenuPosition(inputNode.current.selectionStart);
        updateAutocomplete(value);
        performSearch();
        if (!inline) input.onChange(value);
      }
    );
  }, []);

  // checking if aql starts with simple search
  const checkSimpleSearch = useCallback((inputValue, options) => {
    if (inputValue && !simpleSearchChecked.current) {
      simpleSearchChecked.current = true;

      if (
        !options.some(o => o.value === inputValue)
        && !inputValue.match(SIMPLE_SEARCH_QUOTES_REGEX)
        && !inputValue.match(/[~#@\s.]/)
      ) {
        updateState(
          {
            inputValue: `"${inputValue}"`,
            options: []
          },
          () => {
            inputNode.current.setSelectionRange(inputValue.length + 1, inputValue.length + 1);
          }
        );
      }
    }
  }, []);

  const onKeyDown = useCallback(e => {
    const { inputValue, options } = stateRef.current;

    switch (e.keyCode) {
      case 32: {
        if (!inputValue) {
          e.preventDefault();
        }

        checkSimpleSearch(inputValue, options);
        break;
      }

      case 27: {
        inputNode.current?.blur();
      }
    }
  }, []);

  const reset = useCallback(() => {
    pathFilter.current = null;
    operatorsFilter.current = null;
    simpleSearchChecked.current = false;

    updateState({
      value: [],
      options: getAutocomplete("", 0).filter(filterOptions),
      menuIsOpen: false,
      pickerOpened: null,
      inputValue: "",
      searchValue: "",
      caretCoordinates: null
    });
  }, []);

  const getInlineMenuStyles = useCallback(() => {
    const { caretCoordinates, menuIsOpen, options } = stateRef.current;
    const { classes } = propsRef.current;

    const rightAligned = caretCoordinates && caretCoordinates.left >= inputNode.current.clientWidth;

    const isDisplayed = menuIsOpen && Boolean(options.filter(filterOptions).length);

    return {
      className: clsx(classes.menuCorner, rightAligned ? classes.cornerRight : classes.cornerLeft),
      style: {
        display: isDisplayed ? "block" : "none",
        position: "absolute" as any,
        marginBottom: "12px",
        width: "auto",
        transform: "translateY(calc(-100% - 8px))",
        top: 0,
        ...(rightAligned ? {
            left: inputNode.current.clientWidth,
          } : {
            left: caretCoordinates ? caretCoordinates.left : 0,
          })
      }
    };
  }, []);

  const getOptionLabel = useCallback(option => option.label, []);

  const renderOption = useCallback((optionProps, data) => {
    const { itemRenderer } = propsRef.current;
    const { searchValue } = stateRef.current;

    const label = getOptionLabel(data);

    let option = getHighlightedPartLabel(label, searchValue, optionProps);

    if (label === "DATE" || label === "TIME") {
      option = (
        <ListItemButton {...optionProps} ref={dateAnchor} className="heading centeredFlex">
          {label}
          {label === "DATE" && <DateRange className="ml-1"/>}
          {label === "TIME" && <QueryBuilder className="ml-1"/>}
        </ListItemButton>
      );
    }

    if (typeof itemRenderer === "function") {
      return itemRenderer(option, data, searchValue);
    }

    return option as any;
  }, []);

  const popperAdapter = useCallback(({ anchorEl, disablePortal, className, style, ...params }) => (
    <div {...params} {...getInlineMenuStyles()} />), []);

  useImperativeHandle(ref, () => ({ reset }), []);

  useEffect(() => {
    updateState({
      options: getAutocomplete(input && input.value ? input.value : "").filter(filterOptions)
    });
  }, []);

  const prevRootEntity = useRef(props.rootEntity);

  useEffect(() => {
    if (prevRootEntity.current === props.rootEntity) return;

    prevRootEntity.current = props.rootEntity;

    const { input } = propsRef.current;

    updateState(
      {
        value: [],
        inputValue: input.value || "",
        searchValue: ""
      },
      () => {
        updateState({
          options: getAutocomplete(propsRef.current.input.value || "").filter(filterOptions)
        });
      }
    );

    operatorsFilter.current = "";
    pathFilter.current = "";
  }, [props.rootEntity]);

  const prevInputValue = useRef(input && input.value);

  useEffect(() => {
    const { input } = propsRef.current;

    if (!input || prevInputValue.current === input.value) return;

    prevInputValue.current = input.value;

    updateState({
      inputValue: input.value
    });
  }, [input && input.value]);

  const {
    pickerValue, menuIsOpen, options, value, inputValue, pickerOpened
  } = state;

  return (
    <div className={className} id={input.name}>
      <div className="d-none">
        <DatePicker
          value={pickerValue}
          closeOnSelect={false}
          onChange={handlePickerChange}
          onClose={closePicker}
          open={pickerOpened === "DATE"}
          slots={{
            field: TextField
          }}
          slotProps={{
            popper: {
              placement: "top",
              anchorEl: dateAnchor.current
            }
          }}
        />

        <TimePicker
          value={pickerValue}
          closeOnSelect={false}
          onChange={handlePickerChange}
          onClose={closePicker}
          open={pickerOpened === "TIME"}
          renderInput={props => <TextField {...props} />}
          PopperProps={{
            placement: "top",
            anchorEl: dateAnchor.current
          }}
        />
      </div>

      <div
        className={clsx("relative", {
          "pointer-events-none": disabled,
          [classes.bottomPadding]: !inline
        })}
      >
        <Autocomplete
          value={value}
          open={menuIsOpen && Boolean(options.length)}
          options={options}
          onChange={handleChange}
          renderOption={renderOption}
          filterOptions={filterOptionsInner}
          getOptionLabel={getOptionLabel}
          slots={inline ? { popper: popperAdapter as any } : undefined}
          classes={inline ? {
            root: classes.root,
            paper: classes.menuShadow,
            listbox: "p-0 relative zIndex1 paperBackgroundColor",
            hasPopupIcon: classes.hasPopup,
            hasClearIcon: classes.hasClear,
            inputRoot: classes.inputRoot
          } : null}
          renderInput={params => (
            <TextField
              {...params}
              variant="standard"
              onKeyDown={onKeyDown}
              slotProps={{
                inputLabel: {
                  ...params.slotProps.inputLabel,
                  shrink: true
                },
                input: {
                  ...params.slotProps.input,
                  disableUnderline,
                  classes: {
                    root: fieldClasses.text,
                    underline: fieldClasses.underline
                  },
                  endAdornment
                },
                htmlInput: {
                  ...params.slotProps.htmlInput,
                  value: inputValue || ""
                }
              }}
              error={meta?.invalid}
              helperText={(
                <span className="d-block shakingError">
                  {!disableErrorText && (meta?.invalid ? meta.error || "Expression is invalid" : "")}
                </span>
              )}
              onChange={handleInputChange}
              inputRef={setInputNode}
              onFocus={onFocus}
              onBlur={onBlur}
              onClick={onClick}
              label={label}
              placeholder={placeholder || COMMON_PLACEHOLDER}
            />
          )}
          popupIcon={stubComponent()}
          disableListWrap
          openOnFocus
          multiple
        />
      </div>
    </div>
  );
});

export default EditInPlaceQuerySelect;
