package ish.oncourse.services.node;

import java.util.List;

import ish.oncourse.model.WebNode;

public interface IWebNodeService {

	/**
	 * Returns all web nodes for the current site or current college.
	 */
	List<WebNode> getNodes();
}
