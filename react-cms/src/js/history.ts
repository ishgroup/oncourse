/**
*
 * Set history instance as global
*
* */

let history = null;

export const getHistoryInstance = () => (history);
export const setHistoryInstance = historyInstance => (history = historyInstance);
