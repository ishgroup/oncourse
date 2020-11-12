import { createMemoryHistory, createBrowserHistory } from "history";

export default (process.env.USE_MEMORY_ROUTER ? createMemoryHistory() : createBrowserHistory());
