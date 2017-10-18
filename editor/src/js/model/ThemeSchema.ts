interface BlockItem {
  id: number;
  position: number;
}

export class ThemeSchema {
  top: BlockItem[];
  middle1: BlockItem[];
  middle2: BlockItem[];
  middle3: BlockItem[];
  footer: BlockItem[];
}
