/**
 * Execute provided function, when application is ready.
 *
 * @param body Function to execute.
 */

export function whenReady(body) {
  if (document.readyState == 'loading') {
    document.addEventListener('DOMContentLoaded', body);
  } else {
    body();
  }
}

