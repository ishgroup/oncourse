import {DEFAULT_CONTENT_MODE_ID} from "../constants";

export const getContentModeId = text => {
  if (!text) return DEFAULT_CONTENT_MODE_ID;
  const markerIndex = text.lastIndexOf("{render:");
  if (markerIndex === -1) return DEFAULT_CONTENT_MODE_ID;

  const markerText = text.slice(markerIndex);

  if (markerText.includes("md")) return "md";
  if (markerText.includes("html")) return "html";
  if (markerText.includes("textile")) return "textile";

  return DEFAULT_CONTENT_MODE_ID;
};

export const removeContentMarker = text => {
  if (!text) return text;
  return text.replace(/\s*{render:.*/,"");
};

export const addContentMarker = (text, contentModeId) => (
  `${text}
  {render:"${contentModeId === "wysiwyg" ? "md" : contentModeId}"}`
);
