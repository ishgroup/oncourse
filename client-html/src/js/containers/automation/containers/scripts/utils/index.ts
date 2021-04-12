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
  messageClosureRegexp, getReportComponent, reportClosureRegexp,
} from "../constants";
import { ScriptExtended, ScriptViewMode } from "../../../../../model/scripts";

const getClosureComponent = async closure => {
  switch (closure.type) {
    case "query": {
      return getQueryComponent(closure.body);
    }
    case "message": {
      return getMessageComponent(closure.body);
    }
    case "report": {
      return getReportComponent(closure.body);
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

const messageFilter = body => (/template\s+/.test(body) ? /record\s+records/.test(body) : true);

const messageReplacer = match => (messageFilter(match) ? "CLOSURE" : match);

export const ParseScriptBody = async (scriptItem: Script) => {
  let { content } = scriptItem;
  let imports = content.match(importsRegexp);

  if (imports) {
    imports = imports.map(i => i.replace(/\n/, "").replace("import ", ""));
    content = content.replace(importsRegexp, "").trim();
  }

  const components = [];

  try {
    const queryClosures = (content?.match(queryClosureRegexp) || []).map(body => ({ body, type: "query" })) || [];

    const messageClosures = (content?.match(messageClosureRegexp)
      ?.filter(messageFilter) || [])
      ?.map(body => ({ body, type: "message" })) || [];

    const reportClosuress = (content?.match(reportClosureRegexp) || []).map(body => ({ body, type: "report" })) || [];

    let parsedContent = content;

    if (queryClosures.length) {
      parsedContent = parsedContent.replace(queryClosureRegexp, "CLOSURE");
    }
    if (messageClosures.length) {
      parsedContent = parsedContent.replace(messageClosureRegexp, messageReplacer);
    }
    if (reportClosuress.length) {
      parsedContent = parsedContent.replace(reportClosureRegexp, "CLOSURE");
    }

    const matchComponents = parsedContent.split("CLOSURE");

    const closures = [...queryClosures, ...messageClosures, ...reportClosuress];

    for (const [index, c] of matchComponents.entries()) {
      if (c.trim()) {
        checkDuplicateScriptParts(components, getScriptComponent(c));
      }
      if (closures[index]) {
        checkDuplicateScriptParts(components, await getClosureComponent(closures[index]));
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
