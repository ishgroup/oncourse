import { Session, SessionApi } from '@api/model';
import { DefaultHttpService } from '../constants/HttpService';

class SessionService {
  private sessionApi = new SessionApi(new DefaultHttpService());

  public getUserSessions(): Promise<Session[]> {
    return this.sessionApi.getUserSessions();
  }
}

export default new SessionService();
