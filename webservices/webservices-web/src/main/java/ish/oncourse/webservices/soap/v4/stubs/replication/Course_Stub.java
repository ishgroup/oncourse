package ish.oncourse.webservices.soap.v4.stubs.replication;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="course")
public class Course_Stub extends Soap_Stub {
	@XmlElement(required=true)
	private String name;
	
	@XmlElement(required=true)
	private String detail;
}
