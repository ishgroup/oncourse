import $ from 'jquery';

/**
 * Execute provided function, when application is ready.
 *
 * @param body Function to execute.
 */

// legacy, provide jquery to global scope
window["jQuery"] = $;

export function whenReady(body) {
    $(document).ready(body);
}
