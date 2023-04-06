import React from "react";
import parse from "autosuggest-highlight/parse";
import match from "autosuggest-highlight/match";

export const getHighlightedPartLabel = (label: string, highlighted: string, options?: any) => {
  const matches = match(label, highlighted, { insideWords: true });
  const parts = parse(label, matches);

  const result = parts.map((part, index) => (
    <span key={index}>{part.highlight ? <strong>{part.text}</strong> : part.text}</span>
  ));

  return options ? (
    <div {...options || {}}>
      {result}
    </div>
  ) : result;
};
