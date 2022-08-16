/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

export type ContentMode = "textile" | "html" | "md";

export const CONTENT_MODES: {
  id: ContentMode,
  title: string
}[] = [
  { id: "md", title: "rich text" },
  { id: "textile", title: "legacy" },
  { id: "html", title: "advanced (html)" }
];

export const getContentMarker = text => {
  if (!text) return CONTENT_MODES[0].id;
  const match = text.match(/\s*{render:"(.*)"}/, "");
  return match && match[1] ? match[1] : CONTENT_MODES[1].id;
};

export const removeContentMarker = text => {
  if (!text) return text;
  return text.replace(/\s*{render:.*/g, "");
};

export const addContentMarker = (text, contentMode) => (
  `${text}
  {render:"${contentMode}"}`
);

export const getEditorModeLabel = (mode: ContentMode) => {
  switch (mode) {
    case "md":
      return "RICH";
    case "html":
      return "HTML";
    case "textile":
      return "LEGACY";
  }
};
