export type StringKeyObject<V = any> = { [key: string]: V };

export type StringKeyAndValueObject = StringKeyObject<string>;

// Helper type for creating types from string arrays
export type StringValueType<T extends ReadonlyArray<unknown>> = T extends ReadonlyArray<infer ElementType>
  ? ElementType
  : never;
