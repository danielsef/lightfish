/*
 Copyright 2012 Adam Bien, adam-bien.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.lightfish.presentation.publication;

import java.io.IOException;
import java.util.logging.Logger;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.lightfish.business.servermonitoring.boundary.MonitoringController;

/**
 *
 * @author Adam Bien, blog.adam-bien.com
 */
@WebServlet(name = "TunguskaGate", urlPatterns = {"/live/*"}, asyncSupported = true)
public class TunguskaGate extends HttpServlet {

    @Inject
    Event<BrowserWindow> events;
    private final static Logger LOG = Logger.getLogger(TunguskaGate.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AsyncContext startAsync = request.startAsync();
        String channel = extractChannel(request.getRequestURI());

        LOG.info("Browser is requesting " + channel);

        if (channel == null || channel.trim().isEmpty()) {
            channel = MonitoringController.COMBINED_SNAPSHOT_NAME;
        }
        BrowserWindow browser = new BrowserWindow(startAsync, channel);
        LOG.info("Registering browser window(" + browser.hashCode() + ") for channel " + channel);

        events.fire(browser);
        LOG.fine("Event sent");
    }

    String extractChannel(String uri) {
        if (uri.endsWith("live")) {
            return "";
        }
        int lastIndexOf = uri.lastIndexOf("/");
        return uri.substring(lastIndexOf + 1);
    }
}
