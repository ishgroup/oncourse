package ish.oncourse.services.search;

import ish.oncourse.model.Tag;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SearchParams {
    private String s;
    private Double price;
    private String day;
    private String time;
    private Tag subject;
    private Date after;
    private Date before;
    private List<Suburb> suburbs = new ArrayList<>();
    private Long siteId;
    private Boolean debugQuery = Boolean.FALSE;
    private TimeZone clientTimezone;
	private List<Tag> tags = new ArrayList<>();

	public List<Tag> getTags() {
		return tags;
	}

	protected void addTag(Tag tag) {
		tags.add(tag);
	}

	public TimeZone getClientTimezone() {
		return clientTimezone;
	}

	void setClientTimezone(TimeZone clientTimezone) {
		this.clientTimezone = clientTimezone;
	}

	public Boolean getDebugQuery() {
		return Boolean.TRUE.equals(debugQuery);
	}

	public void setDebugQuery(Boolean debugQuery) {
		this.debugQuery = debugQuery;
	}

	public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Tag getSubject() {
        return subject;
    }

    public void setSubject(Tag subject) {
        this.subject = subject;
    }

    public Date getAfter() {
        return after;
    }

    public void setAfter(Date after) {
        this.after = after;
    }

    public Date getBefore() {
        return before;
    }

    public void setBefore(Date before) {
        this.before = before;
    }

    public List<Suburb> getSuburbs() {
        return suburbs;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public void addSuburb(Suburb suburb) {
        this.suburbs.add(suburb);
    }
}
