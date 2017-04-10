export interface ReactRouter {
  push: (string) => void;
}

export interface ReactRouterContext {
  router?: ReactRouter;
}
