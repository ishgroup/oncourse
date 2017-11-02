class Url {
  link: string;
  isBase: boolean;
  isDefault: boolean;
}

export class Page {
  id: number;
  title: string;
  theme: string;
  layout: string;
  html: string;
  urls: Url[];
  visible: boolean;
}

