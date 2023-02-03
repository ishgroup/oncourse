/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.api.v1.model.DatesIntervalDTO
import ish.oncourse.server.api.v1.model.LogFileDTO
import ish.oncourse.server.api.v1.service.LogsApi
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender
import org.apache.logging.log4j.core.appender.FileManager
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class LogsApiImpl implements LogsApi {

    @Override
    List<LogFileDTO> getLogs(DatesIntervalDTO intervalDTO) {
        LoggerContext logContext = (LoggerContext) LogManager.getContext(true);
        def configuration = logContext.getConfiguration()
        def appenders = configuration.getAppenders().values()

        def fileAppenders = appenders.findAll { it instanceof AbstractOutputStreamAppender }
                .collect { it as AbstractOutputStreamAppender }
                .findAll { it.manager instanceof FileManager }
                .collect { it.manager as FileManager }

        def startInstant = toInstant(intervalDTO.from)
        def endInstant = intervalDTO.to ? toInstant(intervalDTO.to) : Instant.now()

        def logFiles = new ArrayList<File>()

        for (def fileManager : fileAppenders) {
            def fileName = fileManager.getFileName()
            if (fileManager instanceof RollingFileManager) {
                def pattern = (fileManager as RollingFileManager).getPatternProcessor().getPattern()
                def directory = pattern.substring(0, pattern.lastIndexOf('/'))
                def patternExtension = extensionOf(pattern)

                logFiles.addAll(new File(directory).listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if (patternExtension != null) {
                            def fileExtension = extensionOf(name)
                            if (!patternExtension.equals(fileExtension))
                                return false
                        }

                        return fileWasModifiedInInterval(startInstant, endInstant, dir.getAbsolutePath())
                    }
                }))
            }

            if(fileWasModifiedInInterval(startInstant, endInstant, fileName)){
                logFiles.add(new File(fileName))
            }

        }

        return logFiles.collect { file ->
            new LogFileDTO().with { it ->
                it.content = ResourcesUtil.fileToByteArray(file)
                it.fileName = file.name
                it
            }
        }
    }

    private static Instant toInstant(LocalDateTime dateTime){
        return dateTime.toInstant(ZoneId.systemDefault().offset)
    }

    private static String extensionOf(String fileName){
        return fileName.substring(fileName.lastIndexOf('.') + 1)
    }

    private static boolean fileWasModifiedInInterval(Instant start, Instant end, String pathToFile){
        def attributes = Files.readAttributes(Paths.get(pathToFile), BasicFileAttributes.class)
        def lastModified = attributes.lastModifiedTime().toInstant()
        return lastModified.isAfter(start) && lastModified.isBefore(end)
    }
}
