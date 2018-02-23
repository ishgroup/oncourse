package ish.oncourse.willow.editor.v1.service

import ish.oncourse.willow.editor.service.AbstractEditorTest;
import ish.oncourse.willow.editor.v1.model.Layout;
import ish.oncourse.willow.editor.v1.model.Theme;
import ish.oncourse.willow.editor.v1.model.CommonError
import ish.oncourse.willow.editor.v1.service.impl.ThemeApiServiceImpl
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for ThemeApi
 */
public class ThemeApiTest extends AbstractEditorTest {

    private ThemeApi api;

    @Before
    void setup() {
        super.setup()
        api = new ThemeApiServiceImpl(cayenneService, requestService)
    }


    /**
     * Add new theme
     * Add new theme
     */
    @Test
    void addThemeTest() {
        Theme theme = api.createTheme()
        assertNotNull(theme)


    }

    /**
     * Remove theme by provided Id
     * Remove theme
     */
    @Test
    void deleteThemeTest() {
        //api.deleteTheme();

        // TODO: test validations


    }

    /**
     * Get array of layouts
     * Get array of layouts
     */
    @Test
    void getLayoutsTest() {
        //Theme response = api.getLayouts();
        //assertNotNull(response);
        // TODO: test validations


    }

    /**
     * Get array of themes
     * Get array of themes
     */
    @Test
    void getThemesTest() {
        Theme theme = api.createTheme()
        List<Theme> themes = api.getThemes()
        themes
    }

    /**
     * Theme with updated params
     * Save theme params
     */
    @Test
    void saveThemeTest() {
        Theme saveThemeRequest = null;
        //Theme response = api.saveTheme(saveThemeRequest);
        //assertNotNull(response);
        // TODO: test validations


    }
    
}
