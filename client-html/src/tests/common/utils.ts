
export const dashedCase = data => {
  const dataCopy = JSON.parse(JSON.stringify(data));
  const formattedObj = {};
  Object.keys(dataCopy).forEach(key => {
    const formattedProp = key.replace(/\./g, "-");
    formattedObj[formattedProp] = dataCopy[key];
  });
  return formattedObj;
};
