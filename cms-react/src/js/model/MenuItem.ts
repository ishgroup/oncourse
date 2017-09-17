export class MenuItem {
  id: string;
  title: string;
  url: string;
  expanded?: boolean;
  errors?: {
    title?: boolean,
    url?: boolean,
  };
}

