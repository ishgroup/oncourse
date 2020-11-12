const properties = [
  "direction",
  "boxSizing",
  "width",
  "height",
  "overflowX",
  "overflowY",

  "borderTopWidth",
  "borderRightWidth",
  "borderBottomWidth",
  "borderLeftWidth",
  "borderStyle",

  "paddingTop",
  "paddingRight",
  "paddingBottom",
  "paddingLeft",

  "fontStyle",
  "fontVariant",
  "fontWeight",
  "fontStretch",
  "fontSize",
  "fontSizeAdjust",
  "lineHeight",
  "fontFamily",

  "textAlign",
  "textTransform",
  "textIndent",
  "textDecoration",

  "letterSpacing",
  "wordSpacing",

  "tabSize",
  "MozTabSize"
];

const getCaretCoordinates = (element, position, options?) => {
  if (!element) return null;
  const debug = (options && options.debug) || false;
  if (debug) {
    const el = document.querySelector("#input-textarea-caret-position-mirror-div");
    if (el) el.parentNode.removeChild(el);
  }

  const div = document.createElement("div");
  div.id = "input-textarea-caret-position-mirror-div";
  document.body.appendChild(div);

  const style = div.style;
  const computed = window.getComputedStyle ? window.getComputedStyle(element) : element.currentStyle; // currentStyle for IE < 9
  const isInput = element.nodeName === "INPUT";

  style.whiteSpace = "pre-wrap";
  if (!isInput) style.wordWrap = "break-word";

  style.position = "absolute";
  if (!debug) style.visibility = "hidden";

  properties.forEach(function(prop) {
    if (isInput && prop === "lineHeight") {
      if (computed.boxSizing === "border-box") {
        const height = parseInt(computed.height);
        const outerHeight =
          parseInt(computed.paddingTop) +
          parseInt(computed.paddingBottom) +
          parseInt(computed.borderTopWidth) +
          parseInt(computed.borderBottomWidth);
        const targetHeight = outerHeight + parseInt(computed.lineHeight);
        if (height > targetHeight) {
          style.lineHeight = height - outerHeight + "px";
        } else if (height === targetHeight) {
          style.lineHeight = computed.lineHeight;
        } else {
          style.lineHeight = "0";
        }
      } else {
        style.lineHeight = computed.height;
      }
    } else {
      style[prop] = computed[prop];
    }
  });

  style.overflow = "hidden";

  div.textContent = element.value.substring(0, position);

  if (isInput) div.textContent = div.textContent.replace(/\s/g, "\u00a0");

  const span = document.createElement("span");

  span.textContent = element.value.substring(position) || ".";
  div.appendChild(span);

  const coordinates = {
    top: span.offsetTop + parseInt(computed["borderTopWidth"]),
    left: span.offsetLeft + parseInt(computed["borderLeftWidth"]),
    height: parseInt(computed["lineHeight"])
  };

  if (debug) {
    span.style.backgroundColor = "#aaa";
  } else {
    document.body.removeChild(div);
  }

  return coordinates;
};

export default getCaretCoordinates;
