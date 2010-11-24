package ish.oncourse.webservices.soap.stubs;

import java.util.Date;


/**
 *
 * @author Marek Wawrzyczny
 */
public class Module_Stub extends WillowEntity_Stub {

	private Date created;
	private String disciplineCode;
	private String fieldOfEducation;
	private Long ishVersion;
	private Boolean isModule;
	private Date modified;
	private String nationalCode;
	private String title;
	private Long trainingPackage;

	
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getDisciplineCode() {
		return disciplineCode;
	}

	public void setDisciplineCode(String disciplineCode) {
		this.disciplineCode = disciplineCode;
	}

	public String getFieldOfEducation() {
		return fieldOfEducation;
	}

	public void setFieldOfEducation(String fieldOfEducation) {
		this.fieldOfEducation = fieldOfEducation;
	}

	public Long getIshVersion() {
		return ishVersion;
	}

	public void setIshVersion(Long ishVersion) {
		this.ishVersion = ishVersion;
	}

	public Boolean getIsModule() {
		return isModule;
	}

	public void setIsModule(Boolean isModule) {
		this.isModule = isModule;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getNationalCode() {
		return nationalCode;
	}

	public void setNationalCode(String nationalCode) {
		this.nationalCode = nationalCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getTrainingPackageId() {
		return trainingPackage;
	}

	public void setTrainingPackageId(Long trainingPackage) {
		this.trainingPackage = trainingPackage;
	}

}
