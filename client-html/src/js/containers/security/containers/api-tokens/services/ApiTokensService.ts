/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ApiToken, TokenApi } from "@api/model";
import { DefaultHttpService } from "../../../../../common/services/HttpService";

class ApiTokensService {
  readonly tokenApi = new TokenApi(new DefaultHttpService());

  deleteToken(tokenId: number): Promise<any> {
    return this.tokenApi._delete(tokenId);
  }

  createTokens(tokens: ApiToken[]): Promise<any> {
    return this.tokenApi.create(tokens);
  }

  getTokens(): Promise<Array<ApiToken>> {
    return this.tokenApi.get();
  }
}

export default new ApiTokensService();
