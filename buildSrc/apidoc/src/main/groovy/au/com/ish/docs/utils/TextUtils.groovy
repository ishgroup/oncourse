/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.utils

import org.apache.commons.lang3.StringUtils

import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.IntStream

class TextUtils {

    /**
     * Updates markdown links within a text by computing relative paths based on file locations.
     *
     * This method scans a given markdown text for inline links (formatted as `[label](path)`)
     * and updates each link's path by computing the relative path from the specified `template`
     * file location to the target `rootTemplate` file location, and then further adjusts each
     * link using the relative path in {@code link}. The output contains updated links that
     * are correctly adjusted to be accessible from the template file’s location.
     *
     * @param text the original text containing markdown links
     * @param template the file representing the starting location for relative path computation
     * @param rootTemplate the root directory from which all links should be relative
     * @return the modified text with updated link paths to be relative from {@code template} to {@code rootTemplate}.
     */
    static String updateLinksWithRelativePaths(String text, File template, File rootTemplate) {
        String linkRegex = "\\[(.*?)\\]\\((.*?)\\)"
        Matcher matcher = Pattern.compile(linkRegex).matcher(text)

        String relativePathToRoot = computeRelativePath(template.getAbsolutePath(), rootTemplate.getAbsolutePath())
        StringBuilder sb = new StringBuilder()
        while (matcher.find()) {
            String link = matcher.group(2)
            String updatedPath = concatenateRelativePaths(relativePathToRoot, link)
            matcher.appendReplacement(sb, "[${matcher.group(1)}](${updatedPath})")
        }

        matcher.appendTail(sb)
        return sb.toString()
    }

    /**
     * Computes the relative path required to navigate from {@code to} to {@code from}.
     *
     * This method determines the relative path needed to traverse from one directory
     * structure (represented by {@code from}) to another target directory (represented by {@code to}).
     * The output path will contain "../" steps as needed to reach a common ancestor directory,
     * followed by any necessary subdirectory names from {@code to} to complete the relative path.
     *
     * @param from the initial directory path to navigate from
     * @param to the target directory path to navigate to
     * @return a string representing the relative path from {@code from} to {@code to}.
     *         If both paths are the same, returns "./".
     */
    static String computeRelativePath(String from, String to) {
        String prefix = ""
        String[] toPath = StringUtils.isNotEmpty(to) ? to.split("/") : []
        String[] fromPath = StringUtils.isNotEmpty(from) ? from.split("/") : []

        int differences = Math.max(toPath.length - fromPath.length, 0)
        int common = 0
        for (int i = 0; i < Math.min(toPath.length, fromPath.length) - 1; i++) {
            if (toPath[i].equals(fromPath[i])) {
                common++
            } else {
                differences++
            }
        }

        IntStream.range(0, differences).forEach { i -> prefix += "../" }
        IntStream.range(common, fromPath.length - 1).forEach { i -> prefix += fromPath[i] + "/" }

        return prefix.isEmpty() ? "./" : prefix
    }

    /**
     * Concatenates two relative paths into a single optimized relative path.
     *
     * This method combines {@code path1} and {@code path2} into a single relative path,
     * simplifying any unnecessary "../" or "./" segments for an efficient path.
     *
     * @param path1 the base relative path
     * @param path2 the relative path to append to {@code path1}
     * @return a string representing the optimized relative path after concatenation
     */
    private static String concatenateRelativePaths(String path1, String path2) {
        String[] fullPath = (path1 + path2).split(File.separator)
        String optimizedPath = fullPath[fullPath.length - 1]
        int goBack = 0

        for (int i = fullPath.length - 2; i >= 0; i--) {
            if (fullPath[i].equals("..")) {
                goBack++
                continue
            }
            if (goBack != 0) {
                goBack--
            } else if (!fullPath[i].equals(".")) {
                optimizedPath = fullPath[i] + File.separator + optimizedPath
            }
        }
        IntStream.range(0, goBack).forEach { i -> optimizedPath = "../" + optimizedPath }
        return optimizedPath
    }

    static String trimExtraLineSeperators(String text) {
        return text.replaceAll("\n{3,}", "\n\n")
    }

    static String trimBoundaryNewlines(String text) {
        return text.replaceFirst('(^\n+|\n+$)','')
    }
}
