import uniqid from "uniqid";
import {
  ScriptComponent,
  ScriptEmailComponent,
  ScriptQueryComponent,
  ScriptMessageComponent,
  ScriptReportComponent,
} from "../../../../../model/scripts";

export const SCRIPT_EDIT_VIEW_FORM_NAME = "ScriptsForm";

export const closureRegexp = new RegExp(
  /\n?\/\/\s*[a-zA-Z]+\s*closure\s*start([^\//])+\/\/\s*[a-zA-Z]+\s*closure\s*end[\n]?/,
  "g"
);

export const closureNameRegexp = /\/\/\s*([a-zA-Z]+)\s*closure/;

export const emailClosureRegexp = new RegExp(
  /email\s*{\s*([!+=%*?a-zA-Z0-9.,:"'()\\\s@\-/_]|['"][!+=%*?a-zA-Z0-9.,:"'()\\\s@\-/_${}]+['"])+}/,
  "g"
);

export const getScriptComponent = (content): ScriptComponent => ({
    type: "Script",
    id: uniqid(),
    content
  });

export const getQueryTemplate = (entity: string, query: string, queryClosureReturnValue: string) =>
  `\n// Query closure start 
  ${queryClosureReturnValue} = query {
    entity "${entity}"
    query "${query.replace(/\\"/g, '"').replace(/"/g, '\\"')}"
  }      
  // Query closure end\n`;

export const getQueryComponent = (body: string): ScriptQueryComponent => {
  const queryClosureReturnValueMatch = body.match(/\s+(.+)\s+=\s+query/);
  const entityMatch = body.match(/entity\s+"(.+)"\s+query/);
  const queryMatch = body.match(/query\s+"(.+)"\s+(context)?/);

  return {
    type: "Query",
    id: uniqid(),
    // queryClosureReturnValue: queryClosureReturnValueMatch && queryClosureReturnValueMatch[1],
    queryClosureReturnValue: "records",
    entity: entityMatch && entityMatch[1],
    query: queryMatch && queryMatch[1].replace(/\\"/g, '"')
  };
};

export const getMessageTemplate = (component) => {
  const entries = Object.entries(component);
  const parsedString = entries.reduce((result, e) => (
    e[0] === 'id' ? '' : `${result} ${e[0]} "${e[1]}"\n`), '');

  return `\n// Message closure start 
  message {
    ${parsedString}
    record records
    attachment file
  }      
  // Message closure end\n`;
};

export const getMessageComponent = (body: string): ScriptMessageComponent => {
  const result: ScriptMessageComponent = {
    type: "Message",
    id: uniqid()
  };

  if (!body) return result;

  const entries = body.match(/\n?(.*")\n/gi);

  entries.forEach(e => {
    const matchedKey = e.match(/(.*?)\"/);
    const key = matchedKey ? matchedKey[1].trim() : "";

    if (!key) return;

    const matchedValue = e.replace(key, '').trim();
    const value = matchedValue.slice(1, matchedValue.length-1);
    result[key] = value === "false" ? false : value;
  });

  return result
};

export const getReportTemplate = (component) => {
  const entries = Object.entries(component);
  const parsedString = entries.reduce((result, e) => (
    e[0] === 'id' ? '' : `${result} ${e[0]} "${e[1]}"\n`), '');

  return `\n// Report closure start 
  file = report {
    ${parsedString}
    record records
  }      
  // Report closure end\n`;
};

export const getReportComponent = (body: string): ScriptReportComponent => {
  const result: ScriptReportComponent = {
    type: "Report",
    id: uniqid()
  };

  if (!body) return result;

  const entries = body.match(/\n?(.*")\n/gi);

  entries.forEach(e => {
    const matchedKey = e.match(/(.*?)\"/);
    const key = matchedKey ? matchedKey[1].trim() : "";

    if (!key) return;

    const matchedValue = e.replace(key, '').trim();
    const value = matchedValue.slice(1, matchedValue.length-1);
    result[key] = value === "false" ? false : value;
  });

  return result
};

const getClosurePropRegex = (prop: string) => `${prop}\\s+([^\\n]+)\\n`;

const emailComponentProps = [
  "template",
  "bindings",
  "subject",
  "content",
  "from",
  "to",
  "cc",
  "bcc",
  "key",
  "keyCollision"
];

const emptyEmailRegex = /email\s*{\s*}/;

export const getEmailComponent = (body: string): ScriptEmailComponent => {
  const matchProps = {};
  const bindingsValues = [];
  let parsedBody = body;

  emailComponentProps.forEach(p => {
    const regex = new RegExp(getClosurePropRegex(p));
    const match = parsedBody.match(regex);

    if (match) {
      if (p === "bindings") {
        const dataItems = match[1].split(",");

        dataItems.forEach(d => {
          const values = d.split(":");

          bindingsValues.push({ variable: values[0], data: values[1] });
        });
      } else {
        matchProps[p] = match[1];
      }

      parsedBody = parsedBody.replace(regex, "");
    }
  });

  if (bindingsValues.length) {
    matchProps["bindings"] = bindingsValues;
  }

  return emptyEmailRegex.test(parsedBody)
    ? {
        type: "Email",
        id: uniqid(),
        ...matchProps
      }
    : { type: "Script",
        id: uniqid(),
        content: body
      };
};

export const getEmailTemplate = (props: any) => {
  let bodyString = "";
  const propsArray = Object.keys(props);

  propsArray.forEach((p, index) => {
    const stringEnd = index + 1 === propsArray.length ? "" : "\t";

    if (p === "bindings") {
      bodyString += `${p} ${props[p].map(b => `${b.variable}: ${b.data}`)}\n${stringEnd}`;
      return;
    }

    bodyString += `${p} ${props[p]}\n${stringEnd}`;
  });

  return `email {\n\t${bodyString}}`;
};

export const importsRegexp = /import(\s+static)?\s+([\w]+)([.]?[\w*]+)+/g;
