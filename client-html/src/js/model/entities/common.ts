export interface SelectItemDefault {
  value?: any;
  label?: string;
}

export interface FindEntityAql {
  AQL: string;
  id : string;
  action: string;
}

export interface FindEntityState {
  data?: FindEntityAql[];
}
