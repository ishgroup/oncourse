export const NewCustomerSteps = ['Site name', 'Templates', 'Contact', 'Organisation', 'All done!'] as const;

export type Step = typeof NewCustomerSteps[number];
