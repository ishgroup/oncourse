
package ish.oncourse.webservices.soap.stubs.reference;


import java.util.Date;


/**
 *
 * @author Marek Wawrzyczny
 */
public class Language_Stub extends SoapReference_Stub {

	private String absCode;
	private Date created;
	private Boolean isActive;
	private Long ishVersion;
	private Date modified;
	private String name;

	
	public String getAbsCode() {
		return absCode;
	}

	public void setAbsCode(String absCode) {
		this.absCode = absCode;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Long getIshVersion() {
		return ishVersion;
	}

	public void setIshVersion(Long ishVersion) {
		this.ishVersion = ishVersion;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
