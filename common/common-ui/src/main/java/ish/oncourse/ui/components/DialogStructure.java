package ish.oncourse.ui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 * The structure of pages for nyromodal popups.
 * @author ksenia
 *
 */
public class DialogStructure {
	/**
	 * Page(and popup) title.
	 */
	@Parameter
	@Property
	private String title;
}
