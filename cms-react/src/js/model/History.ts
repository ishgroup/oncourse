export class Version {
  id: number;
  published: boolean;
  author: string;
  changes?: number;
  date?: any;
}

export class History {
  versions: Version[];
}

