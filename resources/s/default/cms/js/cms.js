/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

jQuery(document).ready(
		function() {
			jQuery(".cms_editor_tabs").tabs();

			$j(".cms-edit-area-in").each(
					function() {
						editArea = $j(this);
						editBlock = editArea.parent().parent().parent();
						editArea.css('width', editBlock.width() + "px").css(
								'height', editBlock.height() + "px");
					});
		});