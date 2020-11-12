/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  EmailTemplate,
  EmailTemplateApi,
} from "@api/model";
import { DefaultHttpService } from "./HttpService";

class EmailTemplateService {
  readonly emailTemplateApi = new EmailTemplateApi(new DefaultHttpService());

  public getEmailTemplatesWithKeyCode(entityName: string): Promise<EmailTemplate[]> {
    return this.emailTemplateApi.getTemplatesWithKeyCode(entityName);
  }
}

export default new EmailTemplateService();
