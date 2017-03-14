package ish.oncourse.model;

import ish.oncourse.model.auto._Oncourse;

public class Oncourse extends _Oncourse {

    private static Oncourse instance;

    private Oncourse() {}

    public static Oncourse getInstance() {
        if(instance == null) {
            instance = new Oncourse();
        }

        return instance;
    }
}
