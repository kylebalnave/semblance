/*
 * Copyright (C) 2014 kyleb2
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package semblance.results;

/**
 *
 * @author kyleb2
 */
public class ErrorResult extends Result {

    public ErrorResult(String uri, boolean hasPassed) {
        super(uri, hasPassed);
    }

    public ErrorResult(String uri, boolean hasPassed, String message) {
        super(uri, hasPassed, message);
    }

    public ErrorResult(String uri, boolean hasPassed, String message, String reason) {
        super(uri, hasPassed, message, reason);
    }

    public ErrorResult(String uri, boolean hasPassed, String message, String reason, int line, int paragraph) {
        super(uri, hasPassed, message, reason, line, paragraph);
    }

    public ErrorResult(String uri, boolean hasPassed, String message, String reason, int line, int paragraph, long executionTimeMs) {
        super(uri, hasPassed, message, reason, line, paragraph, executionTimeMs);
    }

    public ErrorResult(String uri, boolean hasPassed, String message, String reason, String source, int line, int paragraph, long executionTimeMs) {
        super(uri, hasPassed, message, reason, source, line, paragraph, executionTimeMs);
    }
}
