package ish.oncourse.webservices.services.builders;

import ish.oncourse.webservices.v4.stubs.reference.SoapReferenceStub;

import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.Persistent;

@SuppressWarnings("rawtypes")
public class StubBuilderComposite implements IStubBuilder {

	private Map<String, IStubBuilder> builders = new HashMap<String, IStubBuilder>();

	public StubBuilderComposite() {
		builders.put("Country", new CountryStubBuilder());
		builders.put("Language", new LanguageStubBuilder());
		builders.put("Module", new ModuleStubBuilder());
		builders.put("Qualification", new QualificationStubBuilder());
		builders.put("TrainingPackage", new TrainingPackageStubBuilder());
	}

	@SuppressWarnings("unchecked")
	@Override
	public SoapReferenceStub convert(Persistent record) {
		String key = record.getObjectId().getEntityName();
		IStubBuilder builder = builders.get(key);
		return builder.convert(record);
	}
}
