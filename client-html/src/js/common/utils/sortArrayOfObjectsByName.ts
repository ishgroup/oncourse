export const compareByName = (firstValue, secondValue) => {
  const firstName = firstValue.name.toUpperCase();
  const secondName = secondValue.name.toUpperCase();

  let comparison = 0;
  if (firstName > secondName) {
    comparison = 1;
  } else if (firstName < secondName) {
    comparison = -1;
  }
  return comparison;
};