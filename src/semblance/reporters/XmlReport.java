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
package semblance.reporters;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import semblance.results.IResult;

/**
 * Handles XML reporting
 *
 * @author kyleb2
 */
public class XmlReport extends AbstractReport {

    protected final Document doc;

    public XmlReport(List<IResult> results) {
        super(results);
        doc = getDoc();
    }

    protected final Document getDoc() {
        Document _doc = null;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            _doc = docBuilder.newDocument();
        } catch (ParserConfigurationException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, String.format("Error creating XML doc : %s", ex.getMessage()));
        }
        return _doc;
    }

    @Override
    public void out() {
        new SystemLogReport(results).out();
    }

    @Override
    public boolean out(String fileOut) {
        buildResultDoc();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Failed to create XML transformer %s");
            return false;
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(fileOut);
        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Failed to transform XML %s");
            return false;
        }
        return true;
    }

    protected Document buildResultDoc() {
        Element site = doForEachResultList(results);
        for (IResult result : results) {
            if (result.hasError()) {
                site.appendChild(doForEachErrorResult(result));
            } else if (result.hasFailed()) {
                site.appendChild(doForEachFailedResult(result));
            } else {
                site.appendChild(doForEachPassedResult(result));
            }
        }
        return doc;
    }

    @Override
    protected Element doForEachResultList(List<IResult> results) {
        Element element = doc.createElement("result");
        element.setAttribute("tests", String.valueOf(this.getResultsCount()));
        element.setAttribute("failures", String.valueOf(this.getFailureCount()));
        element.setAttribute("errors", String.valueOf(this.getErrorCount()));
        doc.appendChild(element);
        return element;
    }

    @Override
    protected Element doForEachPassedResult(IResult result) {
        Element element = doc.createElement("result");
        element.setAttribute("url", result.getName());
        element.setAttribute("code", String.valueOf(result.getMessage()));
        element.setAttribute("status", "pass");
        return element;
    }

    @Override
    protected Element doForEachFailedResult(IResult result) {
        Element element = doForEachPassedResult(result);
        Element msg = doc.createElement("message");
        CDATASection cdata = doc.createCDATASection(result.getMessage());
        msg.appendChild(cdata);
        element.setAttribute("status", "fail");
        element.setAttribute("reason", result.getReason());
        element.appendChild(msg);
        return element;
    }

    @Override
    protected Element doForEachErrorResult(IResult result) {
        Element element = doForEachFailedResult(result);
        element.setAttribute("status", "error");
        return element;
    }

}
