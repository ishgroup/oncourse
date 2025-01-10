/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.scripting;

import ish.oncourse.server.cayenne.Script;
import ish.scripting.ScriptResult;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.*;

public class ScriptRun {
    private static final Logger logger = LogManager.getLogger(ScriptRun.class);

    private static final String CONTEXT_ARG = "context";
    private static final String GROOVY_SCRIPT_ENGINE = "groovy";

    static final String DEFAULT_IMPORTS =
            "import ish.common.types.*\n" +
                    "import ish.messaging.*\n" +
                    "import ish.oncourse.server.cayenne.*\n" +
                    "import org.apache.cayenne.query.*\n" +
                    "import java.text.*\n" +
                    "import ish.util.*\n" +
                    "import ish.math.Money\n" +
                    "ish.oncourse.server.imports.CsvParser\n" +
                    "import org.apache.logging.log4j.*\n";

    private static final String PREPARE_API =
            "Contact.metaClass.replacementEmail = null\n" +
                    "\n" +
                    "Contact.metaClass.rightShift = { String email -> \n" +
                    "\tdelegate.replacementEmail = email\n" +
                    "\treturn delegate\n" +
                    "}\n";

    private Script script;
    private ScriptParameters parameters;
    private ObjectContext context;
    private Bindings bindings;

    private ScriptRun() {}

    public static ScriptRun valueOf(Script script, Bindings bindings, ScriptParameters parameters, ObjectContext context){
        var run = new ScriptRun();
        run.script = script;
        run.parameters = parameters;
        run.context = context;
        run.bindings = bindings;

        return run;
    }

    public ScriptResult run(){
        logger.info("Running script {}", script.getName());
        if (script == null) {
            throw new IllegalArgumentException("Script cannot be null.");
        }

        var engine = new ScriptEngineManager().getEngineByName(GROOVY_SCRIPT_ENGINE);

        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        try {
            engine.eval(DEFAULT_IMPORTS + PREPARE_API +
                    String.format(GroovyScriptService.PREPARE_LOGGER, script.getName()) +
                    script.getScript());
        } catch (ScriptException e) {
            logger.error("Compilation failed for '{}'.", script.getName(), e);

            return ScriptResult.failure(e.getMessage());
        }

        // add context to the script's parameters list
        parameters.add(CONTEXT_ARG, context);

        var inv = (Invocable) engine;
        Object[] params = { parameters.asMap() };

        try {
            var result = inv.invokeFunction("run", params);

            return ScriptResult.success(result);
        } catch (ScriptException e) {
            logger.error("Execution failed for '{}'.", script.getName(), e);
            return ScriptResult.failure(e.getMessage());
        } catch (NoSuchMethodException e) {
            logger.error("No 'run' method found in '{}' script.", script.getName(), e);
            return ScriptResult.failure(e.getMessage());
        }
    }
}
