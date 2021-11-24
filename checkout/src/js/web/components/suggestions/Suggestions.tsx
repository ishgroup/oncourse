/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {useCallback, useEffect} from "react";
import {Phase} from "../../../enrol/reducers/State";
import {formatMoney} from "../../../common/utils/FormatUtils";
import {SuggestionsState} from "../../../services/IshState";

interface Props {
  suggestions?: SuggestionsState;
  phase: Phase;
  getSuggestions?: () => void;
  isCartEmpty?: boolean;
}

const Suggestions: React.FC<Props> = props => {
  const { suggestions, phase, getSuggestions, isCartEmpty } = props;

  useEffect(() => {
    getSuggestions();
  }, []);

  const onAdd = useCallback(e => {
    e.preventDefault();
    //
  }, []);

  console.log(suggestions);
  const getDescriptionText = description => {
    if (description) {
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = description;
      const text = tempDiv.textContent || tempDiv.innerText || "";
      const truncateText = 80;
      return text.length ? text.slice(0, truncateText) + (text.length > truncateText ? "..." : "") : "";
    }
    return null;
  };

  return !isCartEmpty && /*[Phase.Summary, Phase.Payment].includes(phase as any) &&*/ (
    <ul className="suggestion-list">
      {suggestions && suggestions.result.map((id, i) => {
        const suggestion = suggestions.entities[id];
        return suggestion && (
          <li key={suggestion.id}>
            <i className="suggestion-icon-plus" onClick={onAdd} />
            <div className="suggestion-content">
              <h5 className="suggestion-name">{suggestion.name}</h5>
              <p className="suggestion-description">
                {/*<span dangerouslySetInnerHTML={{ __html: suggestion.description }} />
                {suggestion.description ? "... " : " "}*/}
                {getDescriptionText(suggestion.description)}
                <a href={`/product/${suggestion.code}`} className="suggestion-more-link">[More]</a>
              </p>
            </div>
            <div className="suggestion-action">
              <span className="suggestion-price">{formatMoney(suggestion.price)}</span>
              <a href="#" onClick={onAdd} className="suggestion-add-button">
                Add
              </a>
            </div>
          </li>
        )})}
    </ul>
  );
};

export default Suggestions;