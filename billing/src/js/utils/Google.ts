/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GAWebProperty, GoogleState, GTMContainer } from '../models/Google';

export const getTokenString = (state: GoogleState) => `Bearer ${state.token?.access_token}`;

export const renderContainerLabel = (con: GTMContainer) => `${con.name} (${con.publicId})`;

export const renderWebPropertyLabel = (prop: GAWebProperty) => `${prop.name} (${prop.id})`;
