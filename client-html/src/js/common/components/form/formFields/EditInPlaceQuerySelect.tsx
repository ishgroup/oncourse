/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import TextField from "@mui/material/TextField";
import DateRange from "@mui/icons-material/DateRange";
import QueryBuilder from "@mui/icons-material/QueryBuilder";
import Autocomplete from "@mui/material/Autocomplete";
import React from "react";
import { createStyles, withStyles } from "@mui/styles";
import { format as formatDate } from "date-fns";
import clsx from "clsx";
import { DatePicker, TimePicker as Time } from "@mui/x-date-pickers";
import { CodeCompletionCore } from "antlr4-c3";
import { ANTLRInputStream, CommonTokenStream } from "antlr4ts";
import { AqlLexer } from "@aql/AqlLexer";
import { AqlParser } from "@aql/AqlParser";
import * as Entities from "@aql/queryLanguageModel";
import { stubComponent } from "../../../utils/common";
import { getHighlightedPartLabel } from "../../../utils/formatting";
import getCaretCoordinates from "../../../utils/getCaretCoordinates";
import { selectStyles } from "./SelectCustomComponents";
import { DD_MM_YYYY_SLASHED, HH_MM_COLONED } from "../../../utils/dates/format";
import {
  FILTER_TAGS_REGEX,
  SIMPLE_SEARCH_QUOTES_AND_NO_WHITESPACE_REGEX,
  SIMPLE_SEARCH_QUOTES_REGEX,
  TAGS_REGEX
} from "../../../../constants/Config";
import { EditInPlaceQueryFieldProps } from "../../../../model/common/Fields";

const queryStyles = theme => createStyles({
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
  },
  noOptions: {
    display: "none",
    "& $menuCorner": {
      display: "none"
    }
  }
});

const TimePicker: any = Time;

// workaround for proper code minifying
const ENUM_CONSTRUCTOR_NAME = Entities.Enum.prototype.constructor.name;

const completeSuggestions = (
  token: string,
  operatorsFilter: string,
  pathFilter: string,
  rootEntity: string,
  filterTags?: Suggestion[],
  tags?: Suggestion[],
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
      variants = tags && tags.length ? [token] : [];
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

export interface Suggestion {
  token: string;
  value: string;
  label: string;
  prefix?: string;
  queryPrefix?: string;
}

interface State {
  value: object[];
  options: Suggestion[];
  menuIsOpen: boolean;
  pickerOpened: "DATE" | "TIME";
  inputValue: string;
  searchValue: string;
  caretCoordinates: any;
}

class EditInPlaceQuerySelect extends React.PureComponent<EditInPlaceQueryFieldProps, State> {
  private inputNode: any;

  private pathFilter: string;

  private operatorsFilter: string;

  private simpleSearchChecked: boolean;

  private autoQuotesAdded: boolean;

  constructor(props) {
    super(props);

    this.state = {
      value: [],
      options: [],
      menuIsOpen: false,
      pickerOpened: null,
      inputValue: (props.input && props.input.value) || "",
      searchValue: "",
      caretCoordinates: null
    };
  }

  componentDidMount(): void {
    this.setState({
      options: this.getAutocomplete(this.props.input && this.props.input.value ? this.props.input.value : "").filter(this.filterOptions)
    });
  }

  componentDidUpdate(prev) {
    const {
     input, rootEntity
    } = this.props;

    if (prev.rootEntity !== rootEntity) {
      this.setState(
        {
          value: [],
          inputValue: input.value || "",
          searchValue: ""
        },
        () => {
          this.setState({
            options: this.getAutocomplete(input.value || "").filter(this.filterOptions)
          });
        }
      );

      this.operatorsFilter = "";
      this.pathFilter = "";
    }

    if (input && prev.input.value !== input.value) {
      this.setState({
        inputValue: input.value
      });
    }
  }

  getAutocomplete = (input, position?) => {
    const { parser } = this.parseInputString(input);
    const {
      rootEntity, filterTags, tags, customFields
    } = this.props;

    const core = new CodeCompletionCore(parser);
    core.showRuleStack = true;
    core.ignoredTokens = new Set([AqlLexer.EOF, AqlLexer.SEPARATOR, AqlLexer.T__17]);
    const candidates = core.collectCandidates(
      typeof position === "number" ? position : 0
    );
    const keywords: any = [];

    if (this.operatorsFilter === "SEPARATOR" && input[input.length - 1] !== " ") {
      this.operatorsFilter = null;

      this.setState(
        prev => ({
          inputValue: prev.inputValue + "."
        }),
        this.setCaret
      );

      return this.getAutocomplete(input + ".", position);
    }

    for (const candidate of candidates.tokens) {
      const suggestions = completeSuggestions(
        parser.vocabulary.getDisplayName(candidate[0]),
        this.operatorsFilter,
        this.pathFilter,
        rootEntity,
        filterTags,
        tags,
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
  };

  parseInputString = val => {
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
  };

  setInputNode = node => {
    if (node) {
      this.inputNode = node;

      this.inputNode.addEventListener("click", this.onInputClick);

      const { setInputNode, inline } = this.props;

      if (setInputNode && inline) {
        setInputNode(this.inputNode);
      }
    }
  };

  setCaret = () => {
    const el = this.inputNode;
    const selectionEnd = this.inputNode.value.length;

    const isScrollable = el.scrollWidth > el.clientWidth;

    if (isScrollable) {
      el.scrollLeft = el.scrollWidth;
    }
    if (el.setSelectionRange) {
      el.focus();
      el.setSelectionRange(selectionEnd, selectionEnd);
    }

    if (this.props.inline) {
      if (isScrollable) {
        this.setState({
          caretCoordinates: {
            left: this.inputNode.clientWidth
          }
        });
      } else {
        this.setMenuPosition(selectionEnd);
      }
    }
  };

  setMenuPosition = position => {
    this.setState({
      caretCoordinates: getCaretCoordinates(this.inputNode, position)
    });
  };

  onBlur = () => {
    if (this.state.pickerOpened) return;

    const { onBlur } = this.props;

    if (onBlur) {
      onBlur();
    }

    this.setState({
      menuIsOpen: false,
    });
  };

  onFocus = e => {
    const { inline, input, onFocus } = this.props;

    if (onFocus) {
      onFocus();
    }

    if (!inline) {
      input.onFocus(e);
    }

    if (this.simpleSearchChecked && !this.inputNode.value) {
      this.simpleSearchChecked = false;
    }

    // expand animation timeout
    setTimeout(() => {
      this.setState(
        {
          menuIsOpen: true
        },
        () => {
          this.setCaret();
          this.updateAutocomplete(this.inputNode.value || "");
        }
      );
    }, 300);
  };

  onInputClick = e => {
    if (this.props.inline) {
      const isScrollable = this.inputNode.scrollWidth > this.inputNode.clientWidth;

      if (isScrollable) {
        this.setState({
          caretCoordinates: {
            left: e.offsetX
          }
        });
      } else {
        this.setMenuPosition(this.inputNode.selectionEnd);
      }
    }
  };

  getInlineMenuStyles = () => {
    const { caretCoordinates, menuIsOpen, options } = this.state;
    const { classes } = this.props;

    const rightAligned = caretCoordinates && caretCoordinates.left >= this.inputNode.clientWidth;
    
    return {
      className: clsx(classes.menuCorner, rightAligned ? classes.cornerRight : classes.cornerLeft),
      style: {
        display: menuIsOpen && Boolean(options.length) ? "block" : "none",
        position: "absolute" as any,
        marginBottom: "12px",
        width: "auto",
        transform: "translateY(calc(-100% - 8px))",
        top: 0,
        ...rightAligned
          ? {
            left: this.inputNode.clientWidth,
          }
          : {
            left: caretCoordinates ? caretCoordinates.left : 0,
          }
      }
    };
  };

  filterOptions = item => item.label
      .toLowerCase()
      .trim()
      .startsWith(this.state.searchValue.trim().toLowerCase());

  filterOptionsInner = options => options.filter(this.filterOptions);

  openPicker = pickerOpened => {
    this.setState({
      pickerOpened
    });
  };

  closePicker = () => {
    this.setState({
      pickerOpened: null
    });
  };

  handlePickerChange = (type, date) => {
    if (!date) return;

    const dateTime = type === "DATE" ? formatDate(date, DD_MM_YYYY_SLASHED) + " " : formatDate(date, HH_MM_COLONED) + " ";

    const inputValue = this.state.inputValue + dateTime;
    this.setState(
      {
        inputValue
      },
      () => {
        this.updateAutocomplete(inputValue);
        this.setCaret();
        this.closePicker();
        this.props.performSearch && this.props.performSearch();
      }
    );
  };

  handleChange = (e, value, action) => {
    const { inline, input } = this.props;

    if (action === "clear" || action === "remove-option") {
      this.operatorsFilter = "";
      this.pathFilter = "";

      this.setState(
        {
          inputValue: ""
        },
        this.performSearch
      );

      if (!inline) {
        input.onChange("");
      }

      return;
    }

    if (!value || (value && !value[0])) return;

    let propType;

    if (value[0].label === "DATE" || value[0].label === "TIME") {
      this.openPicker(value[0].label);
      return;
    }

    if (value[0].token === "AND" || value[0].token === "OR") {
      this.operatorsFilter = "";
    }

    if (value[0].token === "Identifier") {
      propType = (Entities[this.pathFilter] && Entities[this.pathFilter][value[0].value])
        || Entities[this.props.rootEntity][value[0].value];
    }

    let inputValue = (this.state.inputValue || "").replace(
        new RegExp((this.state.searchValue.match(/[+*()]/) ? "\\" : "") + this.state.searchValue + "$"),
        this.state.searchValue.match(/\s/) ? " " : ""
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

    if (!this.simpleSearchChecked) {
      this.simpleSearchChecked = true;
    }

    this.setState(
      {
        inputValue,
        searchValue: ""
      },
      () => {
        this.setCaret();
        this.updateAutocomplete(inputValue);
        this.performSearch();
        if (!inline) input.onChange(inputValue);
      }
    );
  };

  handleInputChange = e => {
    const { input, inline } = this.props;

    const value = e.target.value;

    if (!value && !value.match(/\s/)) {
      this.simpleSearchChecked = false;
    }

    const { tokens: { tokens } } = this.parseInputString(value);

    if (!value) {
      this.setState(
        {
          inputValue: "",
          searchValue: ""
        },
        () => {
          this.setMenuPosition(this.inputNode.selectionStart);
          this.operatorsFilter = "";
          this.pathFilter = "";
          this.setState(
            {
              options: this.getAutocomplete("").filter(this.filterOptions)
            },
            this.performSearch
          );
          if (!inline) input.onChange("");
        }
      );
      return;
    }

    const lastToken = tokens[tokens.length - 2];

    this.setState(
      {
        inputValue: value,
        searchValue: lastToken ? lastToken.text : ""
      },
      () => {
        this.setMenuPosition(this.inputNode.selectionStart);
        this.updateAutocomplete(value);
        this.performSearch();
        if (!inline) input.onChange(value);
      }
    );
  };

  updateAutocomplete = value => {
    const { tokens, parser } = this.parseInputString(value);
    const { filterTags, tags } = this.props;
    const { options } = this.state;

    const parsedTokens = tokens.tokens;

    let lastIdentifier = null;

    for (const token of [...parsedTokens].reverse()) {
      if (token.type === AqlLexer.Identifier && token.text !== this.state.searchValue) {
        lastIdentifier = token;
        break;
      }
    }

    if (lastIdentifier) {
      this.setIdentifierFilters(lastIdentifier.text);
    }

    if (!lastIdentifier && this.pathFilter) {
      this.pathFilter = "";
    }

    const lastToken = parsedTokens[parsedTokens.length - 2];
    const lastTokenType = lastToken && parser.vocabulary.getDisplayName(lastToken._type);

    const preLastToken = parsedTokens[parsedTokens.length - 3];
    const preLastTokenType = preLastToken && parser.vocabulary.getDisplayName(preLastToken._type);

    const prePreLastToken = parsedTokens[parsedTokens.length - 4];
    const prePreLastTokenType = prePreLastToken && parser.vocabulary.getDisplayName(prePreLastToken._type);

    if (preLastTokenType === "'@'" || preLastTokenType === "'#'") {
      this.setState({
        searchValue: this.state.searchValue.replace(/"/g, "")
      });
      return;
    }

    if (lastTokenType === "'@'") {
      this.setState({
        searchValue: "",
        options: (filterTags || [])
      });
      return;
    }

    if (lastTokenType === "'#'") {
      this.setState({
        searchValue: "",
        options: (tags || [])
      });
      return;
    }

    if (
      ["DoubleQuotedStringLiteral", "SingleQuotedStringLiteral", "RichTextLiteral"].includes(lastTokenType)
      && value.match(SIMPLE_SEARCH_QUOTES_AND_NO_WHITESPACE_REGEX)
    ) {
      this.simpleSearchChecked = false;

      const inputValue = value.replace(/[",']/g, "");

      this.setState({
        inputValue,
        searchValue: inputValue,
        options: this.getAutocomplete("", 0).filter(this.filterOptions)
      });

      return;
    }

    if (preLastTokenType === "AND" || preLastTokenType === "OR") {
      this.operatorsFilter = "";
      this.pathFilter = "";
    }

    if (lastTokenType === "WS") {
      if (this.autoQuotesAdded && preLastTokenType === "Identifier" && ["String", "RichText"].includes(this.operatorsFilter)) {
        this.autoQuotesAdded = false;
      }

      if (
        !this.autoQuotesAdded
        && ["String", "RichText"].includes(this.operatorsFilter)
        && !["Identifier", "DoubleQuotedStringLiteral", "SingleQuotedStringLiteral", "RichTextLiteral"].includes(preLastTokenType)
      ) {
        const { inputValue } = this.state;

        this.autoQuotesAdded = true;

        const position = inputValue.length + 1;

        this.setState(
          {
            inputValue: inputValue + `""`,
            options: []
          },
          () => {
            this.inputNode.setSelectionRange(position, position);
            this.setMenuPosition(position);
          }
        );

        return;
      }

      this.setState(
        {
          searchValue: ""
        },
        () => {
          this.setState({
            options: this.getAutocomplete(value, this.inputNode?.selectionStart).filter(this.filterOptions)
          });
        }
      );

      return;
    }

    if (lastToken && lastToken.text === ".") {
      this.operatorsFilter = "";
      this.setState(
        {
          searchValue: ""
        },
        () => {
          this.setState({
            options: this.getAutocomplete(value).filter(this.filterOptions)
          });
        }
      );

      return;
    }

    if (lastTokenType === "Identifier") {
      if (this.state.searchValue !== lastToken.text) {
        this.setState({
          options: this.getAutocomplete(value).filter(this.filterOptions)
        });
      } else {
        if (!lastIdentifier) {
          this.operatorsFilter = "";
        }

        if (options.length === 1 && options[0].value === lastToken.text) {
          this.setIdentifierFilters(lastToken.text);
          if (this.operatorsFilter === "SEPARATOR") {
            this.setState({
              searchValue: "",
              options: this.getAutocomplete(lastToken.text).filter(this.filterOptions)
            });
          }
          return;
        }

        if (prePreLastTokenType === "AND" || prePreLastTokenType === "OR") {
          this.operatorsFilter = "";
          this.pathFilter = "";
        }

        if (this.operatorsFilter === "SEPARATOR" || preLastTokenType === "'.'") {
          this.operatorsFilter = "";
        }

        this.setState({
          options: this.getAutocomplete(value.replace(new RegExp(lastToken.text + "$"), "")).filter(this.filterOptions)
        });
      }
    }
  };

  setIdentifierFilters = tokenText => {
    const { rootEntity, customFields } = this.props;

    if (customFields && customFields.includes(tokenText)) {
      this.operatorsFilter = "String";
      return;
    }

    let propType;

    if (Entities[this.pathFilter]) {
      propType = Entities[this.pathFilter][tokenText] || Entities[this.pathFilter].hasOwnProperty(tokenText);
    } else {
      propType = Entities[rootEntity][tokenText];
    }

    // if (!propType) {
    //   const lastPathMatch = value
    //     .split(" ")
    //     .reverse()
    //     .join(" ")
    //     .match(/[a-z](?:\S+?\.)+\S+[a-z]/);
    //
    //   if (!lastPathMatch) {
    //     return;
    //   }
    //
    //   const entries = lastPathMatch[0].split(".");
    //   const tokenIndex = entries.findIndex(e => e === tokenText);
    //
    //   let ob = Entities[rootEntity];
    //
    //   entries.forEach((e, i) => {
    //     if (tokenIndex !== -1 && tokenIndex + 1 === i) {
    //       return;
    //     }
    //
    //     if (ob) {
    //       if (ob[e]) {
    //         propType = ob[e];
    //       }
    //       ob = Entities[ob[e]];
    //     }
    //   });
    // }

    if (Entities[propType]) {
      this.pathFilter = propType;

      if (Entities[propType].constructor.name === ENUM_CONSTRUCTOR_NAME) {
        this.operatorsFilter = ENUM_CONSTRUCTOR_NAME;
        return;
      }

      this.operatorsFilter = "SEPARATOR";
      return;
    }

    if (propType) {
      this.operatorsFilter = propType;
    }
  };

  onKeyDown = e => {
    const { inputValue, options } = this.state;

    switch (e.keyCode) {
      case 32: {
        if (!inputValue) {
          e.preventDefault();
        }

        this.checkSimpleSearch(inputValue, options);
        break;
      }

      case 27: {
        this.inputNode?.blur();
      }
    }
  };

  // checking if aql starts with simple search
  checkSimpleSearch = (inputValue, options) => {
    if (inputValue && !this.simpleSearchChecked) {
      this.simpleSearchChecked = true;

      if (
        !options.some(o => o.value === inputValue)
        && !inputValue.match(SIMPLE_SEARCH_QUOTES_REGEX)
        && !inputValue.match(/[~#@\s.]/)
      ) {
        this.setState(
          {
            inputValue: `"${inputValue}"`,
            options: []
          },
          () => {
            this.inputNode.setSelectionRange(inputValue.length + 1, inputValue.length + 1);
          }
        );
      }
    }
  };

  reset = () => {
    this.pathFilter = null;
    this.operatorsFilter = null;
    this.simpleSearchChecked = false;

    this.setState({
      value: [],
      options: this.getAutocomplete("", 0).filter(this.filterOptions),
      menuIsOpen: false,
      pickerOpened: null,
      inputValue: "",
      searchValue: "",
      caretCoordinates: null
    });
  };

  performSearch = () => {
    const { performSearch } = this.props;

    if (performSearch) {
      performSearch();
    }
  };

  getOptionLabel = option => option.label;

  renderOption = (optionProps, data) => {
    const { itemRenderer } = this.props;
    const { searchValue } = this.state;

    const label = this.getOptionLabel(data);

    const content = getHighlightedPartLabel(label, searchValue, optionProps);

    let option = content;

    if (label === "DATE" || label === "TIME") {
      option = (
        <div className={clsx("heading", "centeredFlex")}>
          {content}
          {label === "DATE" && <DateRange className="ml-1" />}
          {label === "TIME" && <QueryBuilder className="ml-1" />}
        </div>
      );
    }

    if (typeof itemRenderer === "function") {
      return itemRenderer(option, data, searchValue);
    }

    return option as any;
  };

  popperAdapter = ({ anchorEl, disablePortal, className, style,  ...params }) => (<div {...params} {...this.getInlineMenuStyles()} />);

  render() {
    const {
      classes,
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
    } = this.props;

    const {
      menuIsOpen, options, value, inputValue, pickerOpened
    } = this.state;

    return (
      <div className={className} id={input.name}>
        <div className="d-none">
          <DatePicker
            value=""
            onChange={date => this.handlePickerChange("DATE", date)}
            onClose={this.closePicker}
            open={pickerOpened === "DATE"}
            renderInput={props => <TextField {...props} />}
          />

          <TimePicker
            value=""
            onChange={date => this.handlePickerChange("TIME", date)}
            onClose={this.closePicker}
            open={pickerOpened === "TIME"}
            renderInput={props => <TextField {...props} />}
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
            onChange={this.handleChange}
            renderOption={this.renderOption}
            filterOptions={this.filterOptionsInner}
            getOptionLabel={this.getOptionLabel}
            PopperComponent={inline ? this.popperAdapter as any : undefined}
            classes={inline ? {
              root: classes.root,
              paper: classes.menuShadow,
              listbox: "p-0 relative zIndex1 paperBackgroundColor",
              hasPopupIcon: classes.hasPopup,
              hasClearIcon: classes.hasClear,
              inputRoot: classes.inputWrapper,
              noOptions: classes.noOptions
            } : undefined}
            renderInput={params => (
              <TextField
                {...params}
                variant="standard"
                onKeyDown={this.onKeyDown}
                InputLabelProps={{
                  shrink: true
                }}
                InputProps={{
                  ...params.InputProps,
                  disableUnderline,
                  classes: {
                    root: fieldClasses.text,
                    underline: fieldClasses.underline
                  },
                  endAdornment
                }}
                // eslint-disable-next-line react/jsx-no-duplicate-props
                inputProps={{
                  ...params.inputProps,
                  value: inputValue || ""
                }}
                error={meta?.invalid}
                helperText={(
                  <span className="d-block shakingError">
                    {!disableErrorText && (meta?.invalid ? meta.error || "Expression is invalid" : "")}
                  </span>
                )}
                onChange={this.handleInputChange}
                inputRef={this.setInputNode}
                onFocus={this.onFocus}
                onBlur={this.onBlur}
                label={label}
                placeholder={placeholder || "No value"}
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
  }
}

export default withStyles(theme => ({ ...selectStyles(theme), ...queryStyles(theme) }))(
  EditInPlaceQuerySelect
) as React.FC<EditInPlaceQueryFieldProps>;
