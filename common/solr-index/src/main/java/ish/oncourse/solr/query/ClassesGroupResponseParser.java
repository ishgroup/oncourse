package ish.oncourse.solr.query;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.common.SolrDocumentList;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassesGroupResponseParser {

	private List<GroupCommand> groupCommands;

	private GroupCommand currentGroup;

	private ClassesGroupResponseParser(GroupResponse groupResponse) {
		if (groupResponse.getValues() == null) {
			throw new IllegalArgumentException("GroupResponse does not contain any GroupCommand");
		}
		this.groupCommands = groupResponse.getValues();
	}

	private ClassesGroupResponseParser withGroupName(String groupName) {
		currentGroup = groupCommands.stream()
									.filter(groupCommand -> groupCommand.getName().equals(groupName))
									.findFirst()
									.orElseThrow(() -> new IllegalArgumentException("GroupName: " + groupName + " not found"));
		return this;
	}

	public static ClassesGroupResponseParser valueOf(GroupResponse groupResponse, String groupName) {
		return new ClassesGroupResponseParser(groupResponse).withGroupName(groupName);
	}

	public Integer getGroupsNumber() {
		return groupCommands.size();
	}

	public Set<String> getGroupsNames() {
		return groupCommands.stream().map(GroupCommand::getName).collect(Collectors.toSet());
	}

	public Integer getMatches() {
		return currentGroup.getMatches();
	}

	public Set<String> getGroupValues() {
		return currentGroup.getValues().stream().map(Group::getGroupValue).collect(Collectors.toSet());
	}

	public SolrDocumentList getGroupDocumentList(String groupValue) {
		return currentGroup.getValues().stream()
										.filter(group -> group.getGroupValue().equals(groupValue))
										.findFirst()
										.orElseThrow(() -> new IllegalArgumentException("GroupValue: " + groupValue + " not found"))
										.getResult();
	}

	public Map<String, SolrDocumentList> getCompactGroupsView() {
		return currentGroup.getValues().stream().collect(Collectors.toMap(Group::getGroupValue, Group::getResult));
	}
}
