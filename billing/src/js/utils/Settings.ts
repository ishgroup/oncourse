/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { BillingPlan } from '@api/model';

export const renderPlanLabel = (plan: BillingPlan) => {
  switch (plan) {
    case 'basic-21':
      return 'Basic';
    case 'premium-21':
      return 'Premium';
    case 'starter-21':
      return 'Starter';
  }
};
