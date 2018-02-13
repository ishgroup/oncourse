package ish.oncourse.webservices.soap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ATransportTest {

    public static <T> ArrayList<T> createStubsBy(List<String> stubClassNames, String packageName, Class<T> parentStubClass) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        ArrayList<T> stubs = new ArrayList<>();
        for (String stubClassName : stubClassNames) {
            @SuppressWarnings("rawtypes")
            Class aClass = Class.forName(String.format("%s.%s", packageName, stubClassName));
            @SuppressWarnings({"rawtypes", "unchecked"})
            Constructor constructor = aClass.getConstructor();
            @SuppressWarnings("unchecked")
            T replicationStub = (T) constructor.newInstance();
            stubs.add(replicationStub);
        }
        return stubs;
    }
}
