package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 * The structure of pages for nyromodal popups.
 * @author ksenia
 *
 */
public class DialogStructure extends ISHCommon {
	/**
	 * Page(and popup) title.
	 */
	@Parameter
	@Property
	private String title;
}
