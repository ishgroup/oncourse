package ish.oncourse.ui.dynamic;

import ish.oncourse.ui.utils.EmptyRenderable;

import java.util.Set;
import java.util.TreeSet;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.internal.util.RenderableAsBlock;

import com.howardlewisship.tapx.core.dynamic.DynamicDelegate;

public class ContentDelegateComposite implements DynamicDelegate {

	private Set<ContentDelegate> delegates = new TreeSet<ContentDelegate>();
	private ComponentResources resources;

	public ContentDelegateComposite(ComponentResources resources) {
		this.resources = resources;
	}

	public ComponentResources getComponentResources() {
		return resources;
	}

	public Block getBlock(String regionKey) {

		for (DynamicDelegate delegate : delegates) {
			Block block = delegate.getBlock(regionKey);
			if (block != null) {
				return block;
			}
		}

		return new RenderableAsBlock(new EmptyRenderable());
	}

	public void addContentDelegate(ContentDelegate part) {
		delegates.add(part);
	}

	public void clearDelegates() {
		delegates.clear();
	}
}
