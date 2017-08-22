import {isNil} from "lodash";

/**
 * This file contains string to typed value converters, and used to convert properties values which we get from html
 * to typescript typed values.
 * @see HtmlDataService
 */

export const Converters = {
  boolean: (value: string): boolean => {
    if (["true", "false"].indexOf(value) === -1) {
      throw new Error(`Cannot convert ${value} to boolean`);
    }
    return value === "true";
  },

  number: (value: string): number => {
    const v = Number(value);

    if (isNaN(v)) {
      throw new Error(`Cannot convert ${value} to number`);
    }

    return v;
  },

  string: (value: string): string => {
    if (typeof value !== "string") {
      throw new Error(`Cannot convert ${value} to string`);
    }
    return value;
  },

  array: (value: string): Array<any> => {
    const v = JSON.parse(value);

    if (!(v instanceof Array)) {
      throw new Error(`Cannot convert ${value} to array`);
    }
    return v;
  },

  object: (value: string): Object => {
    let v = JSON.parse(value);
    if (typeof v !== "object" || v instanceof Array) {
      throw new Error(`Cannot convert ${value} to object`);
    }
    return v;
  }
};

const stringToTypedValue = (type: string, value: string): any => {
  if (!(type in Converters)) {
    throw new Error(`${type} is not supported`);
  }
  return isNil(value) ? null : Converters[type](value);
};

export default stringToTypedValue;