/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { MessageApi, MessageType, Recipients, SearchQuery, SendMessageRequest } from '@api/model';
import { DefaultHttpService } from '../../../../common/services/HttpService';

class MessageService {
  readonly service = new DefaultHttpService();

  readonly messageApi = new MessageApi(this.service);

  public getMessage(id: number): Promise<any> {
    return this.messageApi.get(id);
  }

  public removeMessage(id: number): Promise<any> {
    return this.messageApi.remove(id);
  }

  public sendMessage(recipientsCount: number, model: SendMessageRequest, messageType: string): Promise<string> {
    return this.messageApi.sendMessage(recipientsCount, model, messageType);
  }

  public getMessagePreview(recipientsCount: number, model: SendMessageRequest, messageType: string): Promise<string> {
    return this.service.POST(`/v1/list/option/message`, model,
      {
        headers: { "X-validate-only": "true" },
        params: { messageType, recipientsCount }
      });
  }

  public getRecipients(entity: string, messageType: MessageType, search: SearchQuery, templateId: number): Promise<Recipients> {
    return this.messageApi.getRecipients(entity, messageType, search, templateId);
  }
}

export default new MessageService();
