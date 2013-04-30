package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class SearchInputs {

	@Inject
	private ITagService tagService;
	
	@Inject
	private Request request;

	@Property
	private String advKeyword;
	
	private List<Tag> subjectTagChildTags;

	@Property
	private String searchNear;

	@Property
	private String searchPrice;

	@Property
	private boolean daytime;

	@Property
	private boolean evening;

	@Property
	private boolean weekday;

	@Property
	private boolean weekend;
	@Property
	private ArrayList<String> tagNames;
	
	@Property
	private String tagName;
	
	@SetupRender
	void beforeRender() {
		subjectTagChildTags = tagService.getSubjectsTag().getWebVisibleTags();

		Collections.sort(subjectTagChildTags, new Comparator<Tag>() {
			public int compare(Tag tag1, Tag tag2) {
				return tag1.getName().compareTo(tag2.getName());
			}
		});
		
		tagNames = new ArrayList<>();
		for(Tag tag:subjectTagChildTags){
			tagNames.add(tag.getName());
		}
		
		Tag browseTag = tagService.getBrowseTag();
		
		if (browseTag != null) {
			if(tagNames.contains(browseTag.getName())){
				tagName = browseTag.getName();
			}
		}
	}

	URL onActionFromSearch2() {
		String subject="";
		subjectTagChildTags = tagService.getSubjectsTag().getWebVisibleTags();
		
		for(Tag tag:subjectTagChildTags){
			if(tagName!=null&&tag.getName().equals(tagName)){
				subject = tag.getDefaultPath();
				break;
			}
		}
		try {
			String url = "http://" + request.getServerName()
					+ "/courses?s=" + (advKeyword == null ? StringUtils.EMPTY : advKeyword)
					+ "&subject=" + subject
					+ "&near=" + (searchNear == null ? StringUtils.EMPTY : searchNear)
					+ "&price=" + (searchPrice == null ? StringUtils.EMPTY : searchPrice)
					+ "&time=" + (daytime ? "daytime" : (evening ? "evening" : StringUtils.EMPTY))
					+ "&day=" + (weekday ? "weekday" : (weekend ? "weekend" : StringUtils.EMPTY));
			return new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
