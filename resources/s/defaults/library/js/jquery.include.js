/*
 * by Petko D. Petkov; pdp (architect)
 * http://www.gnucitizen.org
 * http://www.gnucitizen.org/projects/jquery-include/
 */
jQuery.extend({
	/*
	 * included scripts
	 */
	includedScripts: {},

	/*
	 * include timer
	 */
	includeTimer: null,

	/*
	 * include
	 */
	include: function (url, onload) {
		if (jQuery.includedScripts[url] != undefined) {
			return;
		}

		jQuery.isReady = false;

		if (jQuery.readyList == null) {
			jQuery.readyList = [];
		}

		var script = document.createElement('script');

		script.type = 'text/javascript';
		script.onload = function () {
			jQuery.includedScripts[url] = true;

			if (typeof onload == 'function') {
				onload.apply(jQuery(script), arguments);
			}
		};
		script.onreadystatechange = function () {
			if (script.readyState == 'complete') {
				jQuery.includedScripts[url] = true;

				if (typeof onload == 'function') {
					onload.apply(jQuery(script), arguments);
				}
			}
		};
		script.src = url;

		jQuery.includedScripts[url] = false;
		document.getElementsByTagName('head')[0].appendChild(script);

		if (!jQuery.includeTimer) {
			jQuery.includeTimer = window.setInterval(function () {
				jQuery.ready();
			}, 10);
		}
	}
});

/*
 * replacement of jQuery.ready
 */
jQuery.extend({
	/*
	 * hijack jQuery.ready
	 */
	_ready: jQuery.ready,

	/*
	 * jQuery.ready replacement
	 */
	ready: function () {
		isReady = true;

		for (var script in jQuery.includedScripts) {
			if (jQuery.includedScripts[script] == false) {
				isReady = false;
				break;
			}
		}

		if (isReady) {
			window.clearInterval(jQuery.includeTimer);
			jQuery._ready.apply(jQuery, arguments);
		}
	}
});
