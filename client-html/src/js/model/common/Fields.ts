export interface FieldClasses {
  input?: string;
  text?: string;
  underline?: string;
  label?: string;
  selectMenu?: string;
  placeholder?: string;
  loading?: string;
}

export interface SelectItemRendererProps<E = any> {
  content: string;
  data: E;
  parentProps: any;
  search?: string
}
