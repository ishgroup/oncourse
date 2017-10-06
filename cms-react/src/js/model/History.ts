export class Version {
  id: number;
  published: boolean;
  author: string;
  changes: number;
}

export class History {
  versions: Version[];
}

