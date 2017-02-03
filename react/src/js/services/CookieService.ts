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
     */
    static set(name: string, value: string): void {
        const cookies = CookieService.getCookieObject(document.cookie);
        cookies[name] = value;

        const newCookies = Object.keys(cookies).map(key => `${key}=${cookies[key]}`).join("; ");

        Logger.log(new LogMessage(Level.INFO, "New Cookies set", [newCookies]));

        document.cookie = newCookies;
    }

    private static getCookieObject(cookie: string) {
        cookie.split('; ') // split to 'key=value' string
            .map(keyValue => keyValue.split("=")) // split each 'key=value' string to [key, value]
            .map(keyValue => ({key: keyValue[0], value: keyValue[1]}))
            .reduce((prev, current) => ({...prev, current}), {})
    }
}