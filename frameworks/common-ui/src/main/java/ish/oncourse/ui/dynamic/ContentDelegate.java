package ish.oncourse.ui.dynamic;

import com.howardlewisship.tapx.core.dynamic.DynamicDelegate;

public abstract class ContentDelegate implements DynamicDelegate,
		Comparable<ContentDelegate> {

	private int priority;

	public ContentDelegate(int priority) {
		this.priority = priority;
	}

	public int compareTo(ContentDelegate o) {
		return this.priority - o.priority;
	}
}
