import {CONTENT_MODES, DEFAULT_CONTENT_MODE_ID} from "../constants";
import {ContentMode} from "../../../model";

export const getContentModeId = text => {
  if (!text) return CONTENT_MODES[0].id;
  const markerIndex = text.lastIndexOf("{render:");
  if (markerIndex === -1) {
    return text.trim().toLowerCase() === "sample content" ? CONTENT_MODES[0].id : DEFAULT_CONTENT_MODE_ID;
  }

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

export const getEditorModeLabel = (mode: ContentMode) => {
  switch (mode) {
    case "md":
      return "RICH";
    case "html":
      return "HTML";
    case "textile":
      return "LEGACY";

    default:
      return getEditorModeLabel(DEFAULT_CONTENT_MODE_ID);
  }
};
