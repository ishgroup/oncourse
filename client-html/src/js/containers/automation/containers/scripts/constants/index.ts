import { ScriptComponent, ScriptEmailComponent, ScriptQueryComponent } from "../../../../../model/scripts";

export const SCRIPT_EDIT_VIEW_FORM_NAME = "ScriptsForm";

export const closureRegexp = new RegExp(
  /\n?\/\/\s*[a-zA-Z]+\s*closure\s*start([^\/])+\/\/\s*[a-zA-Z]+\s*closure\s*end[\n]?/,
  "g"
);

export const closureNameRegexp = /\/\/\s*([a-zA-Z]+)\s*closure/;

export const emailClosureRegexp = new RegExp(
  /email\s*{\s*([!+=%*?a-zA-Z0-9.,:"'()\\\s@\-\/_]|['"][!+=%*?a-zA-Z0-9.,:"'()\\\s@\-\/_${}]+['"])+}/,
  "g"
);

export const getScriptComponent = (content): ScriptComponent => ({
    type: "Script",
    content
  });

export const getQueryTemplate = (entity: string, query: string, queryClosureReturnValue: string) =>
  `\n// Query closure start 
  def ${queryClosureReturnValue} = query {
    entity "${entity}"
    query "${query.replace(/\\"/g, '"').replace(/"/g, '\\"')}"
  }      
  // Query closure end\n`;

export const getQueryComponent = (body: string): ScriptQueryComponent => {
  const queryClosureReturnValueMatch = body.match(/def\s+(.+)\s+=\s+query/);
  const entityMatch = body.match(/entity\s+"(.+)"\s+query/);
  const queryMatch = body.match(/query\s+"(.+)"\s+(context)?/);

  return {
    type: "Query",
    // queryClosureReturnValue: queryClosureReturnValueMatch && queryClosureReturnValueMatch[1],
    queryClosureReturnValue: "result",
    entity: entityMatch && entityMatch[1],
    query: queryMatch && queryMatch[1].replace(/\\"/g, '"')
  };
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

        dataItems.forEach((d, i) => {
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
        ...matchProps
      }
    : { type: "Script", content: body };
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
