import { createBrowserHistory, createMemoryHistory } from "history";

export default (process.env.USE_MEMORY_ROUTER ? createMemoryHistory() : createBrowserHistory());
