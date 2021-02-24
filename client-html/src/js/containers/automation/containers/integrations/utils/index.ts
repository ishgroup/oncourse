/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Integration } from "@api/model";
import { IntegrationSchema } from "../../../../../model/automation/integrations/IntegrationSchema";

export const parseIntegrations = (integrations: Integration[]): IntegrationSchema[] => {
  if (!integrations) {
    return null;
  }

  const result: IntegrationSchema[] = [];

  integrations.forEach(integration => {
    const schema: IntegrationSchema = {} as IntegrationSchema;
    schema.id = integration.id;
    schema.name = integration.name;
    schema.type = integration.type;
    schema.verificationCode = integration.verificationCode;
    schema.created = integration.created;
    schema.modified = integration.modified;

    const fields: any = {};

    integration.props.forEach(prop => {
      if (prop) fields[prop.key] = prop.value;
    });

    schema.fields = fields;
    result.push(schema);
  });

  return result;
};

export const getByType = (type: number, source: any) => {
  switch (Number(type)) {
    case 1: {
      return source.moodle;
    }
    case 2: {
      return source.mailchimp;
    }
    case 3: {
      return source.surveymonkey;
    }
    case 4: {
      return source.surveygizmo;
    }
    case 5: {
      return source.xero;
    }
    case 6: {
      return source.myob;
    }
    case 7: {
      return source.cloudassess;
    }
    case 8: {
      return source.canvas;
    }
    case 9: {
      return source.micropower;
    }
    case 10: {
      return source.usi;
    }
    case 11: {
      return source.tsci;
    }
    case 12: {
      return source.googleclassroom;
    }
    case 13: {
      return source.coassemble;
    }
    case 14: {
      return source.talentlms;
    }
    case 15: {
      return source.learndash;
    }
    case 16: {
      return source.amazons3;
    }
    case 17: {
      return source.azure;
    }
  }
  return null;
};
