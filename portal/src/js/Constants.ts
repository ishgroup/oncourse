/**
 * This file contains willow-api specific constants
 */

/**
 * CONTEXT for willow-api requests
 */

export const CONTEXT: string = process.env.RELEASE_VERSION ===  "development" ?  "http://127.0.0.1:8283/a" : "/a/";
