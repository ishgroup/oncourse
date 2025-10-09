/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataRow } from '@api/model';
import { format as formatDateTime } from 'date-fns';
import { FormErrors } from 'redux-form';
import history from '../../../constants/History';

export const updateHistory = (params, url) => {
  const urlParams = new URLSearchParams(params);
  const search = decodeURIComponent(urlParams.toString());
  const newUrl = window.location.origin + url + search;

  if (newUrl !== window.location.href) {
    history.push({
      pathname: url,
      search
    });
  }
};

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

export function getInvalidValueOdjects<D extends {}, V extends any>(errors: FormErrors<D>, values: V, invalid = []) {
  for (const key in errors) {
    if (errors[key]) {
      switch (typeof errors[key]) {
        case "string":
          invalid.push(values);
          break;
        case "object":
          getInvalidValueOdjects(errors[key], values[key], invalid);
      }
    }
  }
  return invalid;
}

export const getCustomColumnsMap = (columns: string): (dataRow: DataRow) => any => {
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

export const createAndDownloadFile = (data: any, type: string, name: string, skipDate: boolean = false) => {
  const url = window.URL.createObjectURL(type === "json"
    ? new Blob([JSON.stringify(data, null, 2)])
    : new Blob([data]));
  const link = document.createElement("a");
  const fileName = name + (skipDate ? "" : "-" + formatDateTime(new Date(), "yyyMMddkkmmss"));

  link.href = url;
  link.setAttribute("download", fileName + `.${type}`);
  link.setAttribute("type", `application/${type}`);

  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};

export const uploadAndGetFile = (): Promise<Blob> => new Promise((resolve => {
  const input = document.createElement("input");
  input.setAttribute("type", `file`);
  input.style.height = "0";
  document.body.appendChild(input);

  const prevFocus = document.body.onfocus;

  document.body.onfocus = () => {
    document.body.removeChild(input);
    document.body.onfocus = prevFocus;
  };

  input.onchange = () => {
    resolve(input.files[0]);
  };

  input.click();
}));

export const createAndDownloadBase64Image = (data: any, name: string, type = "png") => {
  const link = document.createElement("a");
  link.href = "data:image/png;base64," + data;
  link.setAttribute("download", name + `.${type}`);
  link.setAttribute("type", `application/${type}`);

  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

export const createAndDownloadBase64File = (data: any, name: string, type: string) => {
  const link = document.createElement("a");
  link.href = "data:;base64," + atob(data);
  link.setAttribute("download", name + `.${type}`);
  link.setAttribute("type", `application/${type}`);

  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

export const getArrayFieldMeta = name => {
  const match = name.match(/\[(\d)]\.([^.]+)$/);
  return { field: match[2], index: Number(match[1]) };
};

export const attachScript = url => {
  if (document.querySelector(`script[src="${url}"]`)) return;
  const script = document.createElement('script');
  script.type = 'text/javascript';
  script.src = url;
  document.head.appendChild(script);
};

export const attachScriptHTML = (html: string) => {
  const script = document.createElement('script');
  script.type = 'text/javascript';
  script.innerHTML = html;
  document.head.appendChild(script);
};

export const getWindowWidth = () => window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth || 1920;

export const getWindowHeight = () => window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight || 1080;