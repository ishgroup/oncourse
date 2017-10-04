export class MenuItem {
  id?: number;
  title: string;
  url: string;
  expanded?: boolean;
  errors?: {
    title?: boolean,
    url?: boolean,
  };
}

