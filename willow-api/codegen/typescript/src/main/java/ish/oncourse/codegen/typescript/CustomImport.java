package ish.oncourse.codegen.typescript;

/**
 * Data for import statement.
 *
 * @author Ibragimov Ruslan
 */
public class CustomImport {
    private final String name;
    private final String path;

    public CustomImport(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
