/*
 * Copyright (C) 2014 balnave
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package semblance.results;

public class Result implements IResult {

    protected String uri = "";
    protected String source = "";
    protected String message = "";
    protected String reason = "";

    protected long executionTimeMs = 0;

    protected int line = 0;
    protected int paragraph = 0;

    protected boolean pass = false;

    public Result(String uri, boolean hasPassed) {
        this.uri = uri;
        pass = hasPassed;
    }

    public Result(String uri, boolean hasPassed, String message) {
        this(uri, hasPassed);
        this.message = message;
    }

    public Result(String uri, boolean hasPassed, String message, String reason) {
        this(uri, hasPassed, message);
        this.reason = reason;
    }

    public Result(String uri, boolean hasPassed, String message, String reason, int line, int paragraph) {
        this(uri, hasPassed, message, reason);
        this.line = line;
        this.paragraph = paragraph;
    }

    public Result(String uri, boolean hasPassed, String message, String reason, int line, int paragraph, long executionTimeMs) {
        this(uri, hasPassed, message, reason, line, paragraph);
        this.executionTimeMs = executionTimeMs;
    }

    public Result(String uri, boolean hasPassed, String message, String reason, String source, int line, int paragraph, long executionTimeMs) {
        this(uri, hasPassed, message, reason, line, paragraph);
        this.executionTimeMs = executionTimeMs;
        this.source = source;
    }

    @Override
    public String getName() {
        return uri;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getMessage() {
        return removeMultipleSpaces(message);
    }

    @Override
    public String getReason() {
        return removeMultipleSpaces(reason);
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getParagraph() {
        return paragraph;
    }

    @Override
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    protected String removeMultipleSpaces(String input) {
        if (input instanceof String) {
            return input.replaceAll("\\s+", " ");
        }
        return "";
    }

}
