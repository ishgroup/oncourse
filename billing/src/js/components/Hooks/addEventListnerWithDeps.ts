import { useEffect } from "react";

export const addEventListenerWithDeps = (deps: any[], handler: (event?:any) => void) => {
  useEffect(() => {
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler)
  }, [...deps])
}