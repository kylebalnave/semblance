
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
 */package semblance.runners;

import java.util.List;
import semblance.results.IResult;

/**
 * A Runner Interface
 * @author balnave
 */
public interface IRunner {
        
    /**
     * Calls the Runner to begin
     * @return
     * @throws Exception
     * @throws Error 
     */
    public List<IResult> run() throws Exception, Error;
   
}
