import * as _ from "lodash";

const isColor = value => {
  const s = new Option().style;
  s.color = value;
  return s.color !== '';
};

const getFieldType = value => {
  let field: any = {
    type: "text",
  };

  if (isColor(value)) {
    field = {
      type: "colorPicker",
      validate: value => isColor(value) ? undefined : "Please enter valid color string",
    };
  } else if (['"on"', '"off"'].includes(value)) {
    field = {
      type: "select",
      items: [{value: "\"on\"", label: "on"}, {value: "\"off\"", label: "off"}],
    };
  } else if (["true", "false"].includes(value)) {
    field = {
      type: "checkbox",
      value: value === "true",
    };
  }

  return field;
};

export const formatSectionField = fileText => {
  const sections = [];
  // const settingsList = [];
  const code = fileText ? fileText.replace(/\t/gm, "") : "";

  if (fileText) {
    const docVars = ["label", "section", "type"];
    const matchedVars = {
      label: [],
      section: [],
      type: [],
    };

    // const variables = code.split(";").map(c => (c.includes('$') ? c.replace(";", "") : null)).filter(c => !!c).map(c => {
    const variables = code.split("\n").map(c => (c.includes('$') && !c.includes('//') ? c.replace(";", "") : null)).filter(c => !!c).map(c => {
      docVars.forEach(docValue => {
        const regex = new RegExp(`@${docValue}\\s[\\S]*[\\t]*[^*]+$`, 'gm');
        const matched = c.match(regex);
        matchedVars[docValue] = matched && matched.length ? matched[0] : null;
      });

      const variablesRegex = new RegExp('\\$[^*]+:\\s[\\S]*[\\t]*[^*]*$', 'gm');
      const variablesMatched = c.match(variablesRegex);

      return {setting: variablesMatched ? `${variablesMatched[0]};` : null, ...matchedVars};
    });

    variables.forEach((variable, index) => {
      const settingValueParts = variable.setting.split(":").map(t => t.trim());

      const sectionTitle = variable.section ? variable.section.replace('@section ', '') : "common";
      let labelText = variable.label ? variable.label.replace('@label ', '') : null;
      const typeText = variable.type ? variable.type.replace('@type ', '') : null;

      let defaultField;

      const fieldName = settingValueParts[0].replace("$", "");
      const settingValue = settingValueParts[1].replace(";", "");

      if (!labelText) {
        labelText = _.upperFirst(_.toLower(_.startCase(fieldName)));
      }

      if (!typeText) {
        defaultField = getFieldType(settingValue);
      } else {
        defaultField = {
          type: typeText,
        };
      }

      // settingsList.push({
      //   key: fieldName,
      //   value: settingValue,
      // });

      const field = {
        name: fieldName,
        label: labelText,
        setting: variable.setting,
        value: settingValue,
        ...defaultField,
      };

      const getSection = sections.find(s => s.title === sectionTitle);

      if (getSection !== undefined) {
        getSection.items.push(field);
        getSection.items.sort((a, b) => a.type > b.type ? 1 : -1);
      } else {
        sections.push({title: sectionTitle, items: [field]});
      }
    });

    sections.sort((a, b) => a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1);
  }

  return {
    sections,
    // settingsList,
    code,
  };
};
