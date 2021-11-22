/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {useCallback} from "react";
import {Phase} from "../../../enrol/reducers/State";
import {formatMoney} from "../../../common/utils/FormatUtils";

interface SuggestionsState {
  id: number;
  title: string;
  description: string;
  price: number;
  link: string
}

interface Props {
  suggestions?: SuggestionsState[];
  phase: Phase;
}

const Suggestions: React.FC<Props> = ({ suggestions, phase }) => {
  const onAdd = useCallback(e => {
    e.preventDefault();
    //
  }, []);
  return /*[Phase.Summary, Phase.Payment].includes(phase as any) &&*/ (
    <ul className="suggestion-list">
      {suggestions && suggestions.map((suggestion, i) => (
        <li key={suggestion.id}>
          <i className="s-icon-plus" onClick={onAdd}></i>
          <div className="suggestion-desc">
            <h5>{suggestion.title}</h5>
            <p>
              {suggestion.description}
              {"... "}
              <a href={suggestion.link} className="s-more-link">[More]</a>
            </p>
          </div>
          <div className="s-add-item">
            <span className="s-price">{formatMoney(suggestion.price)}</span>
            <a href="#" onClick={onAdd} className="s-add-button">
              Add
            </a>
          </div>
        </li>
      ))}
    </ul>
  );
};

export default Suggestions;