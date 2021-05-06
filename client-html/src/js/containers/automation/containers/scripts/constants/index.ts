
import { Binding } from "@api/model";
import {
  ScriptComponent
} from "../../../../../model/scripts";
import uniqid from "../../../../../common/utils/uniqid";
import EmailTemplateService from "../../email-templates/services/EmailTemplateService";
import EntityService from "../../../../../common/services/EntityService";

export const SCRIPT_EDIT_VIEW_FORM_NAME = "ScriptsForm";

export const getScriptComponent = (content): ScriptComponent => ({
  type: "Script",
  id: uniqid(),
  content,
});

export const queryClosureRegexp = new RegExp(
  "\\n?(def\\s+)?\\w+\\s+=\\s+query\\s+{(\\n+)?(\\s+)?entity\\s+[\"']?\\w+[\"']?(\\s+)?query\\s+[\"'].+[\"'](\\s+)(\\n+)?}(\\s+)?\\n{0,2}",
  "g",
);

export const messageClosureRegexp = new RegExp(
  "\\n?\\s*message\\s+{(\\s+\\w+\\s?[\"']?.+[\"']?\\n)+\\s*}\\s*\\n{0,2}",
  "g",
);

export const reportClosureRegexp = new RegExp(
  "\\n?((\\s+)?file\\s+=\\s+)?report\\s+{[^}]+record records[^}]+}(\\s+)?\\n{0,2}",
  "g",
);

export const getQueryTemplate = (entity: string, query: string, queryClosureReturnValue: string) =>
  `\n${queryClosureReturnValue} = query {
    entity "${entity}"
    query "${query.replace(/\\"/g, '"').replace(/"/g, '\\"')}"
  }\n\n`;

export const getQueryComponent = (body: string): ScriptComponent => {
  const queryClosureReturnValueMatch = body.match(/\s+(.+)\s+=\s+query/);
  const entityMatch = body.match(/entity\s+"(.+)"\s+query/);
  const queryMatch = body.match(/query\s+"(.+)"\s+(context)?/);

  return {
    type: "Query",
    id: uniqid(),
    queryClosureReturnValue: (queryClosureReturnValueMatch && queryClosureReturnValueMatch[1]) || "records",
    entity: entityMatch && entityMatch[1],
    query: queryMatch && queryMatch[1].replace(/\\"/g, '"'),
  };
};

export const getMessageTemplate = component => {
  const variables: Binding[] = component?.templateEntity?.variables || [];
  const entries = Object.entries(component);
  const parsedString = entries.reduce((result, e, index) => (["id", "record", "attachment", "type", "templateEntity"].includes(e[0])
    ? result
    : `${result}${e[0]} ${variables.some(v => v.name === e[0] && v.type === "Object") ? e[1] : `"${e[1]}"`}${index === (entries.length - 1) ? "" : "\n\t\t"}`), "");

  return `\nmessage {
    ${parsedString}
    record records
    attachment file
  }\n\n`;
};

export const getMessageComponent = async (body: string): Promise<ScriptComponent> => {
  const result: ScriptComponent = {
    type: "Message",
    id: uniqid(),
  };

  if (!body) {
    result.template = null;
    return result;
  }

  const entries = body.match(/{(\s+\w+\s?["']?.+["']?\n)+\s*}/)[0]?.match(/(\w+\s?["']?.+["']?\n)+/g);

  for (const e of entries) {
    const key = e.match(/\w+/)[0];
    const value = e.replace(/\w+/, "").trim();
    if (!value) break;

    if (key === "template") {
      result.templateEntity = await EntityService.getPlainRecords("EmailTemplate", "id", `keyCode is ${value}`)
      .then(r => {
        if (r.rows[0].id) {
          return EmailTemplateService.get(Number(r.rows[0].id));
        }
        return null;
      })
      .catch(er => console.error(er));
    }

    result[key] = value.replace(/['"]/g, "");
  }

  return result;
};

export const getReportTemplate = component => {
  const entries = Object.entries(component);
  const parsedString = entries.reduce((result, e) => (
    e[0] === 'id' ? '' : `${result} ${e[0]} "${e[1]}"\n\t`), '');

  return `\nfile = report {
    ${parsedString}
    record records
  }\n\n`;
};

export const getReportComponent = (body: string): ScriptComponent => {
  const result: ScriptComponent = {
    type: "Report",
    id: uniqid(),
  };

  if (!body) return result;

  const entries = body.match(/\n?(.*")\n/gi);

  entries.forEach(e => {
    const matchedKey = e.match(/(.*?)\"/);
    const key = matchedKey ? matchedKey[1].trim() : "";

    if (!key) return;

    const matchedValue = e.replace(key, '').trim();
    const value = matchedValue.slice(1, matchedValue.length - 1);
    result[key] = value === "false" ? false : value;
  });

  return result;
};

export const importsRegexp = /import(\s+static)?\s+([\w]+)([.]?[\w*]+)+/g;
