/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package ish;

import groovy.transform.CompileStatic;
import io.bootique.BQRuntime;
import io.bootique.command.CommandOutcome;
import io.bootique.test.junit.BQTestRuntimeBuilder;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.ArrayList;
import java.util.Collection;

@CompileStatic
public class BootiqueTestFactory implements AfterEachCallback, BeforeEachCallback {

    private Collection<BQRuntime> runtimes;
    private boolean autoLoadModules;

    @Override
    public void afterEach(ExtensionContext context) {
        Collection<BQRuntime> localRuntimes = this.runtimes;

        if (localRuntimes != null) {
            localRuntimes.forEach(runtime -> {
                try {
                    runtime.shutdown();
                } catch (Exception e) {
                    // ignore...
                }
            });
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        this.runtimes = new ArrayList<>();
    }

    /**
     * Sets the default policy for this factory to auto-load modules for each app.
     *
     * @return this factory instance.
     * @since 0.25
     */
    public BootiqueTestFactory autoLoadModules() {
        this.autoLoadModules = true;
        return this;
    }

    /**
     * @param args a String vararg emulating shell arguments passed to a real app.
     * @return a new instance of builder for the test runtime stack.
     * @since 0.20
     */
    public Builder app(String... args) {
        Builder builder = new Builder(runtimes, args);

        if (autoLoadModules) {
            builder.autoLoadModules();
        }

        return builder;
    }

    public static class Builder extends BQTestRuntimeBuilder<Builder> {

        private Collection<BQRuntime> runtimes;

        private Builder(Collection<BQRuntime> runtimes, String[] args) {
            super(args);
            this.runtimes = runtimes;
        }

        /**
         * The main build method that creates and returns a {@link BQRuntime}.
         *
         * @return a new instance of {@link BQRuntime} configured in this builder.
         */
        public BQRuntime createRuntime() {
            BQRuntime runtime = bootique.createRuntime();
            runtimes.add(runtime);
            return runtime;
        }

        /**
         * A convenience shortcut for the tests that are interested in command outcome, not in the runtime state. This
         * code is equivalent to <code>createRuntime().run()</code>.
         *
         * @return an outcome of the application command.
         * @since 0.25
         */
        public CommandOutcome run() {
            return createRuntime().run();
        }
    }
}
