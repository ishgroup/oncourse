/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect } from 'react';
import { formatMoney } from '../../../common/utils/FormatUtils';
import { IshState, SuggestionsState } from '../../../services/IshState';
import { CourseClass, Product } from '../../../model';

interface Props {
  courses?: IshState['courses'];
  products?: IshState['products'];
  getSuggestions?: () => void;
  isCartEmpty?: boolean;
  suggestions?: SuggestionsState;
}

const Suggestions: React.FC<Props> = (props) => {
  const {
    getSuggestions, isCartEmpty, suggestions, products, courses
  } = props;

  useEffect(() => {
    getSuggestions();
  }, []);

  const onAdd = useCallback((e) => {
    e.preventDefault();
    //
  }, []);

  const getDescriptionText = (description) => {
    if (description) {
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = description;
      const text = tempDiv.textContent || tempDiv.innerText || '';
      const truncateText = 80;
      return text.length ? text.slice(0, truncateText) + (text.length > truncateText ? '...' : '') : '';
    }
    return null;
  };

  const renderSuggestion = (suggestion: CourseClass | Product) => suggestion && (
    <li key={suggestion.id}>
      <i className="suggestion-icon-plus" onClick={onAdd} />
      <div className="suggestion-content">
        <h5 className="suggestion-name">{suggestion instanceof CourseClass ? suggestion.course.name : suggestion.name}</h5>
        <p className="suggestion-description">
          {/* <span dangerouslySetInnerHTML={{ __html: suggestion.description }} />
                  {suggestion.description ? "... " : " "} */}
          {getDescriptionText(suggestion instanceof CourseClass ? suggestion.course.description : suggestion.description)}
          <a href={`/product/${suggestion.code}`} className="suggestion-more-link">[More]</a>
        </p>
      </div>
      <div className="suggestion-action">
        <span className="suggestion-price">{formatMoney(suggestion instanceof CourseClass ? suggestion.price.fee : suggestion.price)}</span>
        <a href="#" onClick={onAdd} className="suggestion-add-button">
          Add
        </a>
      </div>
    </li>
  );

  return !isCartEmpty && (
    <ul className="suggestion-list">
      {suggestions?.products?.map((id) => renderSuggestion(products[id]))}
      {suggestions?.courseClasses?.map((id) => renderSuggestion(courses[id]))}
    </ul>
  );
};

export default Suggestions;
