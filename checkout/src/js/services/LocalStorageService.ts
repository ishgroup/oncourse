import {Logger, Level, LogMessage} from "./Logger";

function getItem(key: string): string | null {
  try {
    return localStorage.getItem(key);
  } catch (e) {
    Logger.log(new LogMessage(Level.ERROR, `Error while getting item '${key}' from localStorage`, [e]));
    return null;
  }
}

export class LocalStorageService {
  static get<T>(key: string): T | null {
    try {
      const result = getItem(key);
      if (result) {
        return JSON.parse(result) as T;
      } else {
        return null;
      }
    } catch (e) {
      Logger.log(new LogMessage(Level.ERROR, `Parsing JSON item '${key}' from localStorage`, [e]));
      return null;
    }
  }

  static set(key: string, value: any) {
    try {
      localStorage.setItem(key, JSON.stringify(value));
    } catch (e) {
      Logger.log(new LogMessage(Level.ERROR, `Can't put item '${key}' to localStorage`, [e]));
    }
  }
}
