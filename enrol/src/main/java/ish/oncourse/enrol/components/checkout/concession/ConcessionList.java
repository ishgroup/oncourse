package ish.oncourse.enrol.components.checkout.concession;

import ish.oncourse.model.StudentConcession;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.ArrayList;
import java.util.List;

public class ConcessionList {

	@Parameter(required = true)
	private List<StudentConcession> studentConcessions;

	@Property
	private String name;


    public List<String> getNames()
    {
        List<String> names = new ArrayList<>();
	    for (StudentConcession studentConcession : studentConcessions) {
		    String name = studentConcession.getConcessionType().getName();
		    if (!names.contains(name))
			    names.add(name);
	    }
        return names;
    }
}
