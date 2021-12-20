/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export const LINK_REGEX = /[^-a-zA-Z0-9_/]/i;

export const validateLink = (value) => (!value || !LINK_REGEX.test(value) ? undefined : 'URL has invalid format');


export const REDIRECT_REGEX = /[^-a-zA-Z0-9_?/]/i;

export const validateRedirect = (value) => (!value || !REDIRECT_REGEX.test(value) ? undefined : 'URL has invalid format');
