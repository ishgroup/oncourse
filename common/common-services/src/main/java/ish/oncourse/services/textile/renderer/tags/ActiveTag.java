package ish.oncourse.services.textile.renderer.tags;

import ish.oncourse.model.Tag;

import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ActiveTag {
	private Tag tag;
	private List<Tag> activeTags;

	private boolean active = false;
	private boolean selected = false;

	public boolean isActive() {
		return active;
	}

	public boolean isSelected() {
		return selected;
	}

	private void init() {
		if (!activeTags.isEmpty()) {
			for (Tag activeTag : activeTags) {
				if (tag.isParentOf(activeTag)) {
					selected = true;
				}
				if (activeTag.getId().equals(tag.getId())) {
					active = true;
				}
			}

		}
	}

	public static ActiveTag valueOf(Tag tag, List<Tag> activeTags) {
		ActiveTag result = new ActiveTag();
		result.tag = tag;
		result.activeTags = activeTags;
		result.init();
		return result;
	}


}
