/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect } from 'react';
import { formatMoney } from '../../../common/utils/FormatUtils';
import { IshState, SuggestionsState } from '../../../services/IshState';
import { CourseClass, Product } from '../../../model';

interface Props {
  courses?: IshState['courses'];
  products?: IshState['products'];
  getSuggestions?: () => void;
  isCartEmpty?: boolean;
  suggestions?: SuggestionsState;
  addProduct: (product: Product) => void;
  addCourse: (courseClass: CourseClass) => void;
}

const Suggestions: React.FC<Props> = (props) => {
  const {
    getSuggestions, isCartEmpty, suggestions, products, courses, addProduct, addCourse
  } = props;

  useEffect(() => {
    getSuggestions();
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

  const renderSuggestion = (suggestion: CourseClass | Product) => {
    const suggestionProps = suggestion instanceof CourseClass
      ? {
        name: suggestion.course.name,
        description: suggestion.course.description,
        price: suggestion.price.fee,
        href: `/course/${suggestion.code}`,
        onAdd: () => addCourse(suggestion)
      }
      : {
        name: suggestion.name,
        description: suggestion.description,
        price: suggestion.price,
        href: `/product/${suggestion.code}`,
        onAdd: () => addProduct(suggestion)
      };

    return suggestion && (
      <li key={suggestion.id}>
        <i className="suggestion-icon-plus" onClick={suggestionProps.onAdd} />
        <div className="suggestion-content">
          <h5 className="suggestion-name">{suggestionProps.name}</h5>
          <p className="suggestion-description">
            {/* <span dangerouslySetInnerHTML={{ __html: suggestion.description }} />
                  {suggestion.description ? "... " : " "} */}
            {getDescriptionText(suggestionProps.description)}
            <a href={suggestionProps.href} className="suggestion-more-link">[More]</a>
          </p>
        </div>
        <div className="suggestion-action">
          <span className="suggestion-price">{formatMoney(suggestionProps.price)}</span>
          <a href="#" onClick={suggestionProps.onAdd} className="suggestion-add-button">
            Add
          </a>
        </div>
      </li>
    );
  };

  return !isCartEmpty && (
    <ul className="suggestion-list">
      {suggestions?.products?.map((id) => renderSuggestion(products[id]))}
      {suggestions?.courseClasses?.map((id) => renderSuggestion(courses[id]))}
    </ul>
  );
};

export default Suggestions;
