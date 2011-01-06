package ish.oncourse.webservices.soap.stubs.replication;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "replicationRequest")
public class ReplicationRequest {
	@XmlElementRefs({ @XmlElementRef(type = Course_Stub.class),
			@XmlElementRef(type = CourseClass_Stub.class),
			@XmlElementRef(type = CourseModule_Stub.class),
			@XmlElementRef(type = Session_Stub.class),
			@XmlElementRef(type = SessionTutor_Stub.class) })
	List<Soap_Stub> records;
}
