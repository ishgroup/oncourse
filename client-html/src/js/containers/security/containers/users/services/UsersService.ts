/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { User, UserApi } from "@api/model";
import { DefaultHttpService } from "../../../../../common/services/HttpService";

class UserService {
  readonly http: DefaultHttpService = new DefaultHttpService();

  readonly userApi = new UserApi(this.http);

  public getUsers(): Promise<User[]> {
    return this.userApi.get();
  }

  public updateUser(user: User): Promise<User[]> {
    return this.userApi.update(user);
  }

  public disableTFA(id: number): Promise<any> {
    return this.userApi.disableTFA(id);
  }

  public resetPassword(id: number): Promise<any> {
    return this.userApi.resetPassword(id);
  }

  public requireComplexPass(): Promise<boolean> {
    return this.userApi.requireComplexPass();
  }

  public isLoggedIn(): Promise<any> {
    return this.http.GET("/");
  }
}

export default new UserService();
