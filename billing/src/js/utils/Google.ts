/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GAWebProperty, GoogleState, GTMContainer } from '../models/Google';

export const getTokenString = (state: GoogleState) => `Bearer ${state.token?.access_token}`;

export const renderContainerLabel = (con: GTMContainer) => (con.publicId === 'new' ? con.name : `${con.name} (${con.publicId})`);

export const renderWebPropertyLabel = (prop: GAWebProperty) => (prop.id === 'new' ? prop.name : `${prop.name} (${prop.id})`);
