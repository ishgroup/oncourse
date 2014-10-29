package ish.oncourse.webservices.soap;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class StubPopulator<T> {
    private String packageName;
    private List<String> stubClassNames;
    private Class<T> parentStubClass;

    public StubPopulator(String packageName, List<String> stubClassNames, Class<T> parentStubClass) {
        this.packageName = packageName;
        this.stubClassNames = stubClassNames;
        this.parentStubClass = parentStubClass;
    }

    public  ArrayList<T> populate() throws Exception {
        ArrayList<T> stubs = new ArrayList<>();
        for (String stubClassName : stubClassNames) {
            Class aClass = Class.forName(String.format("%s.%s", packageName, stubClassName));
            Constructor constructor = aClass.getConstructor();
            T replicationStub = (T) constructor.newInstance();
            RandomValueFieldPopulator randomValueFieldPopulator = new RandomValueFieldPopulator();
            randomValueFieldPopulator.populate(replicationStub);
            stubs.add(replicationStub);
        }
        return stubs;
    }

}
