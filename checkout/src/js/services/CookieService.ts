import {Logger, LogMessage, Level} from "./Logger";

export class CookieService {

  /**
   * Returns cookie by it's name.
   *
   * document.cookie has following format:
   *
   * clientOffset=180; clientTimezoneName=Europe/Minsk; shortlist=5127914%5128413; productShortList=50%517%518
   *
   * @param key Name of cookie to get.
   * @returns {string|null} Specified key value or null.
   */
  static get(key: string): string | null {
    const cookie = CookieService.getCookieObject(document.cookie);

    return cookie[key] || null;
  }

  /**
   * Updates cookies by provided key-value.
   *
   * @param name Name of a cookie
   * @param value New value of a cookie
   * @param path Path of cookie, "/" - if not defined
   */
  static set(name: string, value: string, path: string = "/"): void {
    const cookie = `${name}=${value}; path=${path}`;

    Logger.log(new LogMessage(Level.INFO, "New Cookies set", [cookie]));

    document.cookie = cookie;
  }

  private static getCookieObject(cookie: string) {
    return cookie.split('; ') // split to 'key=value' string
      .map(keyValue => keyValue.split("=")) // split each 'key=value' string to [key, value]
      .map(keyValue => ({[keyValue[0]]: keyValue[1]}))
      .reduce((prev, current) => Object.assign(prev, current), {}) || {};
  }
}
