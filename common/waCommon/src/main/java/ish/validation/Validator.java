package ish.validation;

import java.util.Map;

public interface Validator <T> {

    Map<String, T> validate();
}
