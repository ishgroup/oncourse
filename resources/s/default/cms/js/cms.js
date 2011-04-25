/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

jQuery(document).ready(
		function() {
			jQuery(".cms_editor_tabs").tabs();

		});

function customForms() {
    jQuery('.cms-onoff').iphoneStyle();
    jQuery("select, input:checkbox, input:radio, input:file").uniform();

    if(isChrome() != true) {
        jQuery('#scrollbar1').tinyscrollbar();
    }    

}