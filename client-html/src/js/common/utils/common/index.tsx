/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { format as formatDateTime } from "date-fns";
import { SelectItemDefault } from "../../../model/entities/common";

export const getDeepValue = (source, path) => {
  if (path.match(/[.,[]/)) {
    const pathArr = path.split(/[.,[]/g).filter(p => p);

    return pathArr.reduce((acc, current) => {
      if (typeof acc === "object") {
        return current.match(/]/) ? acc[current.replace("]", "")] : acc[current];
      }

      return acc;
    }, source);
  }

  if (path.match(/\w+\.\w+/)) {
    const pathArr = path.split(/\./g);

    return pathArr.reduce((acc, current) => {
      if (typeof acc === "object") {
        return acc[current];
      }

      return acc;
    }, source);
  }

  return source[path];
};

export const sortDefaultSelectItems = (a: SelectItemDefault, b: SelectItemDefault) =>
  (a.label[0].toLowerCase() > b.label[0].toLowerCase() ? 1 : -1);

export const mapSelectItems = (i): SelectItemDefault => ({ label: i, value: i });

export const getCustomColumnsMap = columns => {
  const colArr: string[] = columns.split(",");
  const booleanArr = ["true", "false"];

  return ({ id, values }) => ({
    id: Number(id),
    ...colArr.reduce((prev, cur, i) => ({
        ...prev,
        [cur]: booleanArr.includes(values[i]) ? JSON.parse(values[i]) : values[i]
      }), {})
  });
};

export const createAndDownloadFile = (data: any, type: string, name: string) => {
  const url = window.URL.createObjectURL(new Blob([data]));
  const link = document.createElement("a");
  const fileName = name + "-" + formatDateTime(new Date(), "yyyMMddkkmmss");

  link.href = url;
  link.setAttribute("download", fileName + `.${type}`);
  link.setAttribute("type", `application/${type}`);

  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};

export const getArrayFieldMeta = name => {
  const match = name.match(/\[(\d)]\.([^.]+)$/);
  return { field: match[2], index: Number(match[1]) };
};

export const stubFunction = () => undefined;

export const stubComponent = () => <div className="invisible" />;

export const getWindowWidth = () => window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth || 1920;

export const getWindowHeight = () => window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight || 1080;

export const isInStandaloneMode = () =>
  (window.matchMedia('(display-mode: standalone)').matches)
  // @ts-ignore
  || (window.navigator.standalone)
  || document.referrer.includes('android-app://');
