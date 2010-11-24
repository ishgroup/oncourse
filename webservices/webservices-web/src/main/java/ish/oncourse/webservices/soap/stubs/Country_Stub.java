	package ish.oncourse.webservices.soap.stubs;

import java.util.Date;


/**
 *
 * @author marek
 */
public class Country_Stub extends WillowEntity_Stub {

	private String asccssCode;
	private Date created;
	private Long ishVersion;
	private String isoCodeAlpha2;
	private String isoCodeAlpha3;
	private Integer isoCodeNumeric;
	private Date modified;
	private String name;
	private Integer saccCode;


	public void setAsccssCode(String asccssCode) {
		this.asccssCode = asccssCode;
	}

	public String getAsccssCode() {
		return asccssCode;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public void setIshVersion(Long ishVersion) {
		this.ishVersion = ishVersion;
	}

	public Long getIshVersion() {
		return ishVersion;
	}

	public void setIsoCodeAlpha2(String isoCodeAlpha2) {
		this.isoCodeAlpha2 = isoCodeAlpha2;
	}

	public String getIsoCodeAlpha2() {
		return isoCodeAlpha2;
	}

	public void setIsoCodeAlpha3(String isoCodeAlpha3) {
		this.isoCodeAlpha3 = isoCodeAlpha3;
	}

	public String getIsoCodeAlpha3() {
		return isoCodeAlpha3;
	}

	public void setIsoCodeNumeric(Integer isoCodeNumeric) {
		this.isoCodeNumeric = isoCodeNumeric;
	}

	public Integer getIsoCodeNumeric() {
		return isoCodeNumeric;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getModified() {
		return modified;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setSaccCode(Integer saccCode) {
		this.saccCode = saccCode;
	}

	public Integer getSaccCode() {
		return saccCode;
	}
}
