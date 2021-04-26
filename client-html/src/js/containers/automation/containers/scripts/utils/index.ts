/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Script } from "@api/model";
import {
  getQueryComponent,
  getQueryTemplate,
  getScriptComponent,
  getMessageTemplate,
  getMessageComponent,
  getReportTemplate,
  importsRegexp,
  queryClosureRegexp,
  messageClosureRegexp, getReportComponent, reportClosureRegexp, closureSplitRegexp,
} from "../constants";
import { ScriptComponentType, ScriptExtended, ScriptViewMode } from "../../../../../model/scripts";

const getClosureComponent = async (body: string, type: ScriptComponentType) => {
  switch (type) {
    case "Query": {
      return getQueryComponent(body);
    }
    case "Message": {
      return getMessageComponent(body);
    }
    case "Report": {
      return getReportComponent(body);
    }
  }
  return null;
};

const checkDuplicateScriptParts = (components, parsedComponent) => {
  const prevIndex = components.length - 1;

  if (parsedComponent.type === "Script" && components[prevIndex] && components[prevIndex].type === "Script") {
    components[prevIndex].confirmMessage += parsedComponent.content;
  } else {
    components.push(parsedComponent);
  }
};

const messageFilter = body => {
  let pass = true;
  if (/template\s+/.test(body) && !/record\s+records/.test(body)) {
    pass = false;
  }
  if (/attachment\s+/.test(body) && !/attachment\s+file\s/.test(body)) {
    pass = false;
  }
  return pass;
};

const reportFilter = body => {
  let pass = true;
  if (!/record\s+records/.test(body)) {
    pass = false;
  }
  return pass;
};

export const ParseScriptBody = async (scriptItem: Script) => {
  let { content } = scriptItem;
  let imports = content.match(importsRegexp);

  if (imports) {
    imports = imports.map(i => i.replace(/\n/, "").replace("import ", ""));
    content = content.replace(importsRegexp, "").trim();
  }

  const components = [];

  try {
    const closures = {
      Query: [],
      Message: [],
      Report: []
    };

    const parsedContent = content
    .replace(queryClosureRegexp, body => {
      closures.Query.push(body);
      return `CLOSURE-Query-${closures.Query.length - 1}`;
    })
    .replace(messageClosureRegexp, body => {
      if (messageFilter(body)) {
        closures.Message.push(body);
        return `CLOSURE-Message-${closures.Message.length - 1}`;
      }
      return body;
    })
    .replace(reportClosureRegexp, body => {
      if (reportFilter(body)) {
        closures.Report.push(body);
        return `CLOSURE-Report-${closures.Report.length - 1}`;
      }
      return body;
    });

    const matchComponents = parsedContent.split(closureSplitRegexp);
    const splitMarkesrs = parsedContent.match(closureSplitRegexp);

    for (const [index, c] of matchComponents.entries()) {
      if (c.trim()) {
        checkDuplicateScriptParts(components, getScriptComponent(c));
      }
      if (splitMarkesrs && splitMarkesrs[index]) {
        const [,type, cIndex] = splitMarkesrs[index].split("-");
        checkDuplicateScriptParts(components, await getClosureComponent(closures[type][cIndex], type as ScriptComponentType));
      }
    }
  } catch (e) {
    console.error(e);
  }

  return { ...scriptItem, components, imports };
};

const getComponentBody = (component: any) => {
  switch (component.type) {
    case "Query": {
      return getQueryTemplate(component.entity, component.query, component.queryClosureReturnValue);
    }
    case "Script": {
      return component.content;
    }
    case "Message": {
      return getMessageTemplate(component);
    }
    case "Report": {
      return getReportTemplate(component);
    }
    default:
      return null;
  }
};

export const appendComponents = (value: ScriptExtended, viewMode: ScriptViewMode): Script => {
  const result = { ...value };

  if (viewMode === "Cards") {
    result.content = "";

    if (value.components && value.components.length) {
      value.components.forEach(c => {
        result.content += getComponentBody(c);
      });
    }

    if (value.imports) {
      result.content = value.imports
        .filter(i => Boolean(i.trim()))
        .map(i => "import " + i)
        .join("\n") + `
      \n${result.content}`;
    }
  }

  delete result.components;
  delete result.imports;
  delete result.body;

  return result;
};

export const getType = type => {
  switch (type.toLowerCase()) {
    case "date time":
      return "dateTime";
    case "search select":
      return "searchSelect";
    case "remote data search select":
      return "remoteDataSearchSelect";
    case "header text":
      return "headerText";
    case "multiline text":
      return "multilineText";
    default:
      return type.toLowerCase();
  }
};
