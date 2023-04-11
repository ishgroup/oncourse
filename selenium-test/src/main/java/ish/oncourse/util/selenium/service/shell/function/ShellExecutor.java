/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.util.selenium.service.shell.function;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

public class ShellExecutor {

    private static String message;

    private ShellExecutor() {
    }

    public static ShellExecutor valueOf() {
        return new ShellExecutor();
    }

    public ShellExecutor executeCommand(String commandString) {
        try {
            String[] args = new String[]{System.getenv().get("SHELL"), "-c", commandString};
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            configureEnviroment(processBuilder);

            Process process = processBuilder.start();
            process.waitFor();

            if (process.exitValue() == 0) {
                message = getLocalizedtMessage(process.getInputStream());
            } else {
                message = getLocalizedtMessage(process.getErrorStream());
            }

        } catch (Exception e) {
            message = "Unable to execute command: \n" + e.getLocalizedMessage();
        }
        return this;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Gets location of builded budle and sourseMap js files to create localized React error message.
     * @param processBuilder
     * @throws FileNotFoundException
     */
    private static void configureEnviroment(ProcessBuilder processBuilder) throws FileNotFoundException {
        File rootNodeJsDirectory = new File(
                new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath() +
                        "/client-html/build/nodejs/"
        );
        if (!rootNodeJsDirectory.exists() || !rootNodeJsDirectory.isDirectory()) {
            throw new FileNotFoundException(rootNodeJsDirectory.getAbsolutePath() + " is invalid nodeJs path.");
        }

        Optional<File> versionNodeJsDirectory = Arrays.stream(Objects.requireNonNull(rootNodeJsDirectory
                        .listFiles((File current, String name) -> current.isDirectory() && name.startsWith("node-"))))
                .findFirst();
        if (versionNodeJsDirectory.isEmpty()) {
            throw new FileNotFoundException("NodeJs not found. Please load it to client-html module");
        }

        String envPath = System.getenv().get("PATH");
        String pathVariable = versionNodeJsDirectory.get().getAbsolutePath() + "/bin" + ":" + envPath;
        processBuilder.environment().put("PATH", pathVariable);
    }

    private static String getLocalizedtMessage(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(StringUtils.LF);
            }
        }

        return !sb.toString().isEmpty() ? StringUtils.LF + sb : "No message";
    }
}
