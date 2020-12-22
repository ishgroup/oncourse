/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {Script} from "@api/model";
import {
  closureNameRegexp,
  closureRegexp,
  emailClosureRegexp,
  getEmailComponent,
  getQueryComponent,
  getQueryTemplate,
  getScriptComponent,
  getMessageTemplate,
  getMessageComponent,
  getReportComponent,
  getReportTemplate,
  importsRegexp
} from "../constants/index";

const getClosureComponent = (body: string) => {
  const customClosureMatch = body.match(closureNameRegexp);
  const emailClosureMatch = body.match(emailClosureRegexp);

  if (customClosureMatch) {
    const type = customClosureMatch[1];

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
  }

  if (emailClosureMatch) {
    return getEmailComponent(body);
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

export const ParseScriptBody = (scriptItem: Script) => {
  let { content } = scriptItem;
  let imports = content.match(importsRegexp);

  if (imports) {
    imports = imports.map(i => i.replace(/\n/, "").replace("import ", ""));
    content = content.replace(importsRegexp, "").trim();
  }

  const components = [];

  try {
    const customClosures = content.match(closureRegexp);
    const emailClosures = [];
    // = content.match(emailClosureRegexp);

    const matchComponents = content
      .replace(closureRegexp, "CLOSURE")
      // .replace(emailClosureRegexp, "CLOSURE")
      .split("CLOSURE");

    const closures = [...(customClosures || []), ...(emailClosures || [])];

    matchComponents.forEach((c, index) => {
      if (c.trim()) {
        checkDuplicateScriptParts(components, getScriptComponent(c));
      }
      if (closures[index]) {
        checkDuplicateScriptParts(components, getClosureComponent(closures[index]));
      }
    });
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
    // case "Email": {
    //   delete component.type;
    //   return getEmailTemplate(component);
    // }
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

export const appendComponents = (value: any): Script => {
  let content = "";

  if (value.components && value.components.length) {
    value.components.forEach(c => {
      content += getComponentBody(c);
    });
  }

  if (value.imports) {
    content = value.imports
        .filter(i => Boolean(i.trim()))
        .map(i => "import " + i)
        .join("\n") + `
        \n${content}`;
  }

  delete value.components;
  delete value.imports;
  delete value.body;

  return { ...value, content };
};

export const getType = (type) => {
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
      return type.toLowerCase()
  }
}
