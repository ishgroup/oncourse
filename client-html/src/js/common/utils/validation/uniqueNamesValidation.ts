export const validateUniqueNamesInArray = (value, allValues) => {
  const matches = allValues.types.filter(item => item.name && item.name.trim() === value.trim());

  return matches.length > 1 ? "Name must be unique" : undefined;
};
