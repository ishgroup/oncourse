/**
 * Execute provided function, when application is ready.
 *
 * @param body Function to execute.
 */
export function whenReady(body: () => void) {
    jQuery(document).ready(body);
}