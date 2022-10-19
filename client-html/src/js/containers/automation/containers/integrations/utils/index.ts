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
    schema.id = Number(integration.id);
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
