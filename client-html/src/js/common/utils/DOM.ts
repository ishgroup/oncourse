/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

export const countLines = (el: HTMLElement) => {
  if (!el) return null;
  const divHeight = el.offsetHeight;
  const lineHeight = parseInt(getComputedStyle(el).lineHeight);
  return Math.round(divHeight / lineHeight);
};

export const countWidth = (el: string, container: Element) => {
  const testContainer = document.createElement("span");
  const containerStyles = getComputedStyle(container);
  testContainer.style["height"] = containerStyles["height"];
  testContainer.style["fontSize"] = containerStyles["fontSize"];
  testContainer.style["fontWeight"] = containerStyles["fontWeight"];
  testContainer.style["fontFeatureSettings"] = containerStyles["fontFeatureSettings"];
  testContainer.style.position = "absolute";
  testContainer.style.zIndex = "-10000";
  testContainer.innerHTML = el;
  document.body.appendChild(testContainer);
  const width = testContainer.offsetWidth;
  document.body.removeChild(testContainer);
  return width;
};