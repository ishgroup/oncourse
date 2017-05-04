export enum Level {
  ERROR,
  INFO,
  DEBUG
}

export class LogMessage {
  constructor(public level: Level,
              public message: string,
              public data?: any[]) {
  }
}

export class Logger {
  static log(message: LogMessage) {
    if (message.level === Level.DEBUG) {
      (console.debug || console.log)(message.message, message.data || "");
    } else if (message.level === Level.INFO) {
      console.info(message.message, message.data || "");
    } else if (message.level === Level.ERROR) {
      console.error(message.message, message.data || "");
    }
  }

  static logObject(message:any) {
    (console.debug || console.log)(message);
  }
}
