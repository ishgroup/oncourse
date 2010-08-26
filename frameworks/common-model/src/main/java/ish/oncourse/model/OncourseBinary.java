package ish.oncourse.model;

import ish.oncourse.model.auto._OncourseBinary;

public class OncourseBinary extends _OncourseBinary {

    private static OncourseBinary instance;

    private OncourseBinary() {}

    public static OncourseBinary getInstance() {
        if(instance == null) {
            instance = new OncourseBinary();
        }

        return instance;
    }
}
