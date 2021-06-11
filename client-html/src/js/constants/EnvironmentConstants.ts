export class EnvironmentConstants {
  static development = "development";
  static production = "production";
}

export const IS_JEST = process.env.JEST_WORKER_ID !== undefined;
