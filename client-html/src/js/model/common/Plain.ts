export interface PlainEntityState<T> {
  items?: T[];
  search?: string;
  loading?: boolean;
  rowsCount?: number;
}
