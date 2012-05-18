package ish.oncourse.portal.services.javascript;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Flow;
import org.apache.tapestry5.internal.TapestryInternalUtils;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.StylesheetLink;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class JQueryJavaScriptStack implements JavaScriptStack {

    private final boolean productionMode;

    private final SymbolSource symbolSource;

    private final AssetSource assetSource;

    private final ThreadLocale threadLocale;

    private final List<Asset> javaScriptStack, stylesheetStack;

    private static final String ROOT = "org/apache/tapestry5";

    private static final String[] CORE_JAVASCRIPT = new String[]
    {
            "context:js/calendar.js",
            "context:js/initialise.js",
            "context:js/jquery-1.6.2.min.js",
            "context:js/jquery.effects.blind.min.js",
            "context:js/jquery.effects.core.min.js",
            "context:js/jquery.effects.fade.min.js",
            "context:js/jquery.effects.slide.min.js",
            "context:js/jquery.prettyCheckboxes.js",
            "context:js/jquery.selectbox.js",
            "context:js/jquery.tara.tabs.js",
            "context:js/jquery.ui.core.min.js",
            "context:js/jquery.ui.datepicker.js",
            "context:js/main.js",
            "context:js/script.js",
            "context:js/scroller.js",
            "context:js/slider.js"
    };

    // Because of changes to the logic of how stylesheets get incorporated, the default stylesheet
    // was removed, the logic for it is now in TapestryModule.contributeMarkupRenderer().

    private static final String[] CORE_STYLESHEET = new String[]
            { "context:css/jquery.prettyCheckboxes.css",
                    "context:css/jquery.selectbox.css",
                    "context:css/screen.css",
                    "context:css/stl.css"};

    public JQueryJavaScriptStack(@Symbol(SymbolConstants.PRODUCTION_MODE)
                               boolean productionMode,
                               SymbolSource symbolSource,
                               AssetSource assetSource,
                               ThreadLocale threadLocale)
    {
        this.symbolSource = symbolSource;
        this.productionMode = productionMode;
        this.assetSource = assetSource;
        this.threadLocale = threadLocale;

        javaScriptStack = convertToAssets(CORE_JAVASCRIPT);
        stylesheetStack = convertToAssets(CORE_STYLESHEET);
    }

    public String getInitialization()
    {
        return productionMode ? null : "Tapestry.DEBUG_ENABLED = true;";
    }

    public List<String> getStacks()
    {
        return Collections.emptyList();
    }

    private List<Asset> convertToAssets(String[] paths)
    {
        List<Asset> assets = CollectionFactory.newList();

        for (String path : paths)
        {
            assets.add(expand(path, null));
        }

        return Collections.unmodifiableList(assets);
    }

    private Asset expand(String path, Locale locale)
    {
        String expanded = symbolSource.expandSymbols(path);

        return assetSource.getAsset(null, expanded, locale);
    }

    public List<Asset> getJavaScriptLibraries()
    {
        return createStack(javaScriptStack).toList();
    }

    public List<StylesheetLink> getStylesheets()
    {
        return createStack(stylesheetStack).map(TapestryInternalUtils.assetToStylesheetLink)
                .toList();
    }

    private Flow<Asset> createStack(List<Asset> stack, Asset... assets)
    {
        return F.flow(stack).append(assets);
    }
}
