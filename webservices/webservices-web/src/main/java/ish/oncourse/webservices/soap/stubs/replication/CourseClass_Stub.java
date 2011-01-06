package ish.oncourse.webservices.soap.stubs.replication;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="courseClass")
public class CourseClass_Stub extends Soap_Stub {
	@XmlElement
	private String code;
}
