export interface LatestActivityItem {
  name: string;
  date: string;
  id: number | string;
}

export interface LatestActivityDataGroup {
  entity: string;
  items: LatestActivityItem[];
}

export interface LatestActivityState {
  started?: string;
  data?: LatestActivityDataGroup[];
}

export interface DashboardItem {
  url?: string;
  category?: string;
  name?: string;
  id?: number;
}
