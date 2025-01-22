/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * This file contains api specific constants
 */

import { EnvironmentConstants } from "../../constants/EnvironmentConstants";

/**
 * CONTEXT for api requests
 */

export const CONTEXT: string = process.env.NODE_ENV === EnvironmentConstants.development ? "https://127.0.0.1:8182/a/" : "/a/";