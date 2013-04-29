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
    private SolrDocumentList near;
    private Double price;
    private String day;
    private String time;
    private Tag subject;
    private Double km;
    private Date after;
    private Date before;
    private List<Suburb> suburbs;
    private Boolean debugQuery = Boolean.FALSE;
    private TimeZone clientTimezone;
    
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

    public SolrDocumentList getNear() {
        return near;
    }

    public void setNear(SolrDocumentList near) {
        this.near = near;
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

    public Double getKm() {
        return km;
    }

    public void setKm(Double km) {
        this.km = km;
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
        if (suburbs == null) {
            suburbs = new ArrayList<Suburb>();
            if (near != null) {
                for (SolrDocument solrDocument : near)
                    suburbs.add(Suburb.valueOf(solrDocument, getKm()));
            }
        }
        return suburbs;
    }
}
