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

package ish.validation;

import org.eclipse.jetty.http.pathmap.AbstractPathSpec;
import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.http.pathmap.PathSpecGroup;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copy of
 * @see org.eclipse.jetty.http.pathmap.UriTemplatePathSpec
 * Allows to match pathes without variables (after regex parsing use '*' instead of '+')
 */
public class AngelUriTemplatePathSpec extends AbstractPathSpec {

    private static final Logger LOG = Log.getLogger(AngelUriTemplatePathSpec.class);

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{(.*)\\}");
    /**
     * Reserved Symbols in URI Template variable
     */
    private static final String VARIABLE_RESERVED = ":/?#[]@" + // gen-delims
            "!$&'()*+,;="; // sub-delims
    /**
     * Allowed Symbols in a URI Template variable
     */
    private static final String VARIABLE_SYMBOLS = "-._";
    private static final Set<String> FORBIDDEN_SEGMENTS;

    static
    {
        FORBIDDEN_SEGMENTS = new HashSet<>();
        FORBIDDEN_SEGMENTS.add("/./");
        FORBIDDEN_SEGMENTS.add("/../");
        FORBIDDEN_SEGMENTS.add("//");
    }

    private final String _declaration;
    private final PathSpecGroup _group;
    private final int _pathDepth;
    private final int _specLength;
    private final Pattern _pattern;
    private final String[] _variables;
    /**
     * The logical (simplified) declaration
     */
    private final String _logicalDeclaration;

    public AngelUriTemplatePathSpec(String rawSpec)
    {
        Objects.requireNonNull(rawSpec, "Path Param Spec cannot be null");

        if ("".equals(rawSpec) || "/".equals(rawSpec))
        {
            _declaration = "/";
            _group = PathSpecGroup.EXACT;
            _pathDepth = 1;
            _specLength = 1;
            _pattern = Pattern.compile("^/$");
            _variables = new String[0];
            _logicalDeclaration = "/";
            return;
        }

        if (rawSpec.charAt(0) != '/')
        {
            // path specs must start with '/'
            throw new IllegalArgumentException("Syntax Error: path spec \"" + rawSpec + "\" must start with '/'");
        }

        for (String forbidden : FORBIDDEN_SEGMENTS)
        {
            if (rawSpec.contains(forbidden))
                throw new IllegalArgumentException("Syntax Error: segment " + forbidden + " is forbidden in path spec: " + rawSpec);
        }

        String declaration = rawSpec;
        StringBuilder regex = new StringBuilder();
        regex.append('^');

        List<String> varNames = new ArrayList<>();
        // split up into path segments (ignoring the first slash that will always be empty)
        String[] segments = rawSpec.substring(1).split("/");
        char[] segmentSignature = new char[segments.length];
        StringBuilder logicalSignature = new StringBuilder();
        int pathDepth = segments.length;
        for (int i = 0; i < segments.length; i++)
        {
            String segment = segments[i];
            Matcher mat = VARIABLE_PATTERN.matcher(segment);

            if (mat.matches())
            {
                // entire path segment is a variable.
                String variable = mat.group(1);
                if (varNames.contains(variable))
                {
                    // duplicate variable names
                    throw new IllegalArgumentException("Syntax Error: variable " + variable + " is duplicated in path spec: " + rawSpec);
                }

                assertIsValidVariableLiteral(variable, declaration);

                segmentSignature[i] = 'v'; // variable
                logicalSignature.append("/*");
                // valid variable name
                varNames.add(variable);
                // build regex
                regex.append("/([^/]*)");
            }
            else if (mat.find(0))
            {
                // variable exists as partial segment
                throw new IllegalArgumentException("Syntax Error: variable " + mat.group() + " must exist as entire path segment: " + rawSpec);
            }
            else if ((segment.indexOf('{') >= 0) || (segment.indexOf('}') >= 0))
            {
                // variable is split with a path separator
                throw new IllegalArgumentException("Syntax Error: invalid path segment /" + segment + "/ variable declaration incomplete: " + rawSpec);
            }
            else if (segment.indexOf('*') >= 0)
            {
                // glob segment
                throw new IllegalArgumentException("Syntax Error: path segment /" + segment + "/ contains a wildcard symbol (not supported by this uri-template implementation): " + rawSpec);
            }
            else
            {
                // valid path segment
                segmentSignature[i] = 'e'; // exact
                logicalSignature.append('/').append(segment);
                // build regex
                regex.append('/');
                // escape regex special characters
                for (int j = 0; j < segment.length(); j++)
                {
                    char c = segment.charAt(j);
                    if ((c == '.') || (c == '[') || (c == ']') || (c == '\\'))
                        regex.append('\\');
                    regex.append(c);
                }
            }
        }

        // Handle trailing slash (which is not picked up during split)
        if (rawSpec.charAt(rawSpec.length() - 1) == '/')
        {
            regex.append('/');
            logicalSignature.append('/');
        }

        regex.append('$');

        Pattern pattern = Pattern.compile(regex.toString());

        int varcount = varNames.size();
        String[] variables = varNames.toArray(new String[varcount]);

        // Convert signature to group
        String sig = String.valueOf(segmentSignature);

        PathSpecGroup group;
        if (Pattern.matches("^e*$", sig))
            group = PathSpecGroup.EXACT;
        else if (Pattern.matches("^e*v+", sig))
            group = PathSpecGroup.PREFIX_GLOB;
        else if (Pattern.matches("^v+e+", sig))
            group = PathSpecGroup.SUFFIX_GLOB;
        else
            group = PathSpecGroup.MIDDLE_GLOB;

        _declaration = declaration;
        _group = group;
        _pathDepth = pathDepth;
        _specLength = declaration.length();
        _pattern = pattern;
        _variables = variables;
        _logicalDeclaration = logicalSignature.toString();
    }

    /**
     * Validate variable literal name, per RFC6570, Section 2.1 Literals
     */
    private static void assertIsValidVariableLiteral(String variable, String declaration)
    {
        int len = variable.length();

        int i = 0;
        int codepoint;
        boolean valid = (len > 0); // must not be zero length

        while (valid && i < len)
        {
            codepoint = variable.codePointAt(i);
            i += Character.charCount(codepoint);

            // basic letters, digits, or symbols
            if (isValidBasicLiteralCodepoint(codepoint, declaration))
                continue;

            // The ucschar and iprivate pieces
            if (Character.isSupplementaryCodePoint(codepoint))
                continue;

            // pct-encoded
            if (codepoint == '%')
            {
                if (i + 2 > len)
                {
                    // invalid percent encoding, missing extra 2 chars
                    valid = false;
                    continue;
                }
                codepoint = TypeUtil.convertHexDigit(variable.codePointAt(i++)) << 4;
                codepoint |= TypeUtil.convertHexDigit(variable.codePointAt(i++));

                // validate basic literal
                if (isValidBasicLiteralCodepoint(codepoint, declaration))
                    continue;
            }

            valid = false;
        }

        if (!valid)
        {
            // invalid variable name
            throw new IllegalArgumentException("Syntax Error: variable {" + variable + "} an invalid variable name: " + declaration);
        }
    }

    private static boolean isValidBasicLiteralCodepoint(int codepoint, String declaration)
    {
        // basic letters or digits
        if ((codepoint >= 'a' && codepoint <= 'z') ||
                (codepoint >= 'A' && codepoint <= 'Z') ||
                (codepoint >= '0' && codepoint <= '9'))
            return true;

        // basic allowed symbols
        if (VARIABLE_SYMBOLS.indexOf(codepoint) >= 0)
            return true; // valid simple value

        // basic reserved symbols
        if (VARIABLE_RESERVED.indexOf(codepoint) >= 0)
        {
            LOG.warn("Detected URI Template reserved symbol [{}] in path spec \"{}\"", (char)codepoint, declaration);
            return false; // valid simple value
        }

        return false;
    }

    @Override
    public int compareTo(PathSpec other)
    {
        if (other instanceof AngelUriTemplatePathSpec)
        {
            AngelUriTemplatePathSpec otherUriPathSpec = (AngelUriTemplatePathSpec)other;
            return otherUriPathSpec._logicalDeclaration.compareTo(this._logicalDeclaration);
        }
        else
        {
            return super.compareTo(other);
        }
    }

    public Map<String, String> getPathParams(String path)
    {
        Matcher matcher = getMatcher(path);
        if (matcher.matches())
        {
            if (_group == PathSpecGroup.EXACT)
                return Collections.emptyMap();
            Map<String, String> ret = new HashMap<>();
            int groupCount = matcher.groupCount();
            for (int i = 1; i <= groupCount; i++)
                ret.put(_variables[i - 1], matcher.group(i));
            return ret;
        }
        return null;
    }

    protected Matcher getMatcher(String path)
    {
        return _pattern.matcher(path);
    }

    @Override
    public int getSpecLength()
    {
        return _specLength;
    }

    @Override
    public PathSpecGroup getGroup()
    {
        return _group;
    }

    @Override
    public int getPathDepth()
    {
        return _pathDepth;
    }

    @Override
    public String getPathInfo(String path)
    {
        // Path Info only valid for PREFIX_GLOB types
        if (_group == PathSpecGroup.PREFIX_GLOB)
        {
            Matcher matcher = getMatcher(path);
            if (matcher.matches())
            {
                if (matcher.groupCount() >= 1)
                {
                    String pathInfo = matcher.group(1);
                    if ("".equals(pathInfo))
                        return "/";
                    else
                        return pathInfo;
                }
            }
        }
        return null;
    }

    @Override
    public String getPathMatch(String path)
    {
        Matcher matcher = getMatcher(path);
        if (matcher.matches())
        {
            if (matcher.groupCount() >= 1)
            {
                int idx = matcher.start(1);
                if (idx > 0)
                {
                    if (path.charAt(idx - 1) == '/')
                        idx--;
                    return path.substring(0, idx);
                }
            }
            return path;
        }
        return null;
    }

    @Override
    public String getDeclaration()
    {
        return _declaration;
    }

    @Override
    public String getPrefix()
    {
        return null;
    }

    @Override
    public String getSuffix()
    {
        return null;
    }

    public Pattern getPattern()
    {
        return _pattern;
    }

    @Override
    public boolean matches(final String path)
    {
        int idx = path.indexOf('?');
        if (idx >= 0)
        {
            // match only non-query part
            return getMatcher(path.substring(0, idx)).matches();
        }
        else
        {
            // match entire path
            return getMatcher(path).matches();
        }
    }

    public int getVariableCount()
    {
        return _variables.length;
    }

    public String[] getVariables()
    {
        return _variables;
    }
}
