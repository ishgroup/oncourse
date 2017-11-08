import * as Lodash from "lodash";
import {ATTR_DATA_PROP_PREFIX, HTMLMarker} from "../services/HTMLMarker";
import StringToTypedValue from "./StringToTypedValue";

const CAMEL_CASE_SPLITTER: RegExp = /(.)([A-Z]+)/g;

/**
 * This function read attributes with prefix 'data-prop-' from this html container and covert them to typed values
 */
export const parse = (container: HTMLElement, marker: HTMLMarker): { [key: string]: any } => {
  const result: { [key: string]: any } = {};
  Object.keys(marker.props).forEach((key) => {
    const value = container.getAttribute(`${ATTR_DATA_PROP_PREFIX}${camelCase2DashCase(key)}`);
    result[key] = StringToTypedValue(marker.props[key], value);
  });

  if (container.childElementCount === 1) {
    result['children'] = container.innerHTML;
  }

  return result;
};

export const dashCase2CamelCase = (str: string): string => {
  return Lodash.camelCase(str);
};


export const camelCase2DashCase = (str: string): string => {
  return str.replace(CAMEL_CASE_SPLITTER, function (m, previous, uppers) {
    return previous + '-' + uppers.toLowerCase();
  });
};


export const stopPropagation = (e): void => {
  e.stopPropagation();
  e.nativeEvent.stopImmediatePropagation();
};


export function plural(count, values) {
  if (count === 1) {
    return values[0];
  } else {
    return values[1];
  }
}
