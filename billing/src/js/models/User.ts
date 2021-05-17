
export const NewCustomerSteps = ["Site name", "Templates", "Contact", "Organisation", "All done!"] as const;
export const ExistingCustomerSteps = ["Sites"] as const;

export type NewCustomerStep = typeof NewCustomerSteps[number];
export type ExistingCustomerStep = typeof ExistingCustomerSteps[number];
export type Step = NewCustomerStep | ExistingCustomerStep;
