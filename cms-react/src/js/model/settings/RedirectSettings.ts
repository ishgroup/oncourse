export class RedirectItem {
  id?: number;
  from?: boolean;
  to?: boolean;
}

export class RedirectSettings {
  rules: RedirectItem[];
}
