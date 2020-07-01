/**
 * This file contains willow-api specific constants
 */

/**
 * CONTEXT for willow-api requests
 */

export const CONTEXT: string = process.env.RELEASE_VERSION ===  "development" ?  "http://10.29.67.16:8301/a" : "/a/";
