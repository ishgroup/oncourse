package ish.oncourse.ui.dynamic;

import com.howardlewisship.tapx.core.dynamic.DynamicDelegate;

public abstract class DynamicDelegatePart implements DynamicDelegate,
		Comparable<DynamicDelegatePart> {

	private int priority;

	public DynamicDelegatePart(int priority) {
		this.priority = priority;
	}

	public int compareTo(DynamicDelegatePart o) {
		return o.priority - this.priority;
	}
}
