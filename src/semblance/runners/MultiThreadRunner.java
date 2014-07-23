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
package semblance.runners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.results.IResult;

/**
 *
 * @author kyleb2
 */
public abstract class MultiThreadRunner extends Runner {
    
    public static final String KEY_THREADS = "threads";

    public MultiThreadRunner(Map config) {
        super(config);
    }

    public MultiThreadRunner(String configUrlOrFilePath) {
        super(configUrlOrFilePath);
    }

    @Override
    public List<IResult> call() throws Exception, Error {
        results = new ArrayList<IResult>();
        int threadCount = ((Number) getConfigValue(KEY_THREADS, 5)).intValue();
        ExecutorService execSvc = Executors.newFixedThreadPool(threadCount);
        List<Runner> queue = getRunnerCollection();
        Logger.getLogger(getClass().getName()).log(Level.INFO, String.format("Start thread pool of size %s with %s threads", queue.size(), threadCount));
        try {
            List<Future<List<IResult>>> futureResults = execSvc.invokeAll(queue);
            for (Future<List<IResult>> res : futureResults) {
                if (res.get() != null) {
                    results.addAll(res.get());
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception in thread", ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception in thread", ex);
        } finally {
            if (!execSvc.isShutdown()) {
                Logger.getLogger(getClass().getName()).log(Level.INFO, "Shutdown thread pool!");
                execSvc.shutdown();
            }
        }
        return results;
    }

    /**
     * Creates a collection of Runners
     *
     * @return
     */
    protected abstract List<Runner> getRunnerCollection();

}
