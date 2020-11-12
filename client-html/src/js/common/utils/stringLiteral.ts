// Helper function for creating types from string arrays
export function stringLiterals<T extends string>(...args: T[]): T[] {
  return args;
}
