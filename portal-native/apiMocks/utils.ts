// Resolve function with logger
export function promiseResolve<D = any>(config, data: D = {} as any, headers = {}) {
  console.log("%c ----------------", "color: black");
  console.log(`%c Api request to: ${config.url}`, "color: #bada55");
  console.log(`%c Api request method: ${config.method}`, "color: #bada55");
  console.log(`%c request params:`, "color: #bada55");
  if (config.method === "get") {
    console.log(config.params);
  } else {
    console.log(config.data && [parseJson(config.data)]);
  }
  console.log(`%c response params:`, "color: #bada55");
  console.log([data]);
  console.log("%c ----------------", "color: black");

  return [200, data, headers];
}

// Reject function with logger
export const promiseReject = (config, data = {}, headers = {}) => {
  console.log(`%c Api request ${config.method} to: ${config.url}`, "color: red");
  console.log(`%c request params:`, "color: #bada55");
  if (config.method === "get") {
    console.log(config.params);
  } else {
    console.log(config.data && [parseJson(config.data)]);
  }
  console.log(`%c request params:`, "color: #bada55");
  return [400, data, headers];
}

const parseJson = (data: string) => {
  let json;

  try {
    json = JSON.parse(data);
  } catch (e) {
    json = data;
  }

  return json;
}
