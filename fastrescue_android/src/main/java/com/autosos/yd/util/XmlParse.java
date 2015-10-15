package com.autosos.yd.util;

import com.autosos.yd.Constants;
import com.autosos.yd.model.Version;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Administrator on 2015/7/29.
 */
public class XmlParse {
    public static Version parseXml(InputStream inStream) throws Exception
    {
        Version version =new Version();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inStream);
        Element root = document.getDocumentElement();
        NodeList childNodes = root.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++)
        {
            Node childNode = (Node) childNodes.item(j);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                if ("version".equals(childElement.getNodeName())) {
                    version.setVersion(childElement.getFirstChild().getNodeValue());
                } else if (("versioncode".equals(childElement.getNodeName()))) {
                    version.setVerCode(Integer.parseInt(childElement.getFirstChild().getNodeValue()));
                } else if (!Constants.DEBUG && ("name".equals(childElement.getNodeName()))) {
                    version.setName(childElement.getFirstChild().getNodeValue());
                } else if (Constants.DEBUG && ("name2".equals(childElement.getNodeName()))) {
                    version.setName(childElement.getFirstChild().getNodeValue());
                } else if ("url".equals(childElement.getNodeName()) && !Constants.DEBUG) {
                    version.setUrl(childElement.getFirstChild().getNodeValue());
                } else if ("url2".equals(childElement.getNodeName()) && Constants.DEBUG) {
                    version.setUrl(childElement.getFirstChild().getNodeValue());
                }else if("update_data".equals(childElement.getNodeName())){
                    version.setUpdate_data(childElement.getFirstChild().getNodeValue());
                }else if("debug_versioncode".equals(childElement.getNodeName())){
                    version.setDebug_versioncode(Integer.parseInt(childElement.getFirstChild().getNodeValue()));
                }else if("canUpdate_version".equals(childElement.getNodeName())){
                    version.setCanUpdateVersion(Integer.parseInt(childElement.getFirstChild().getNodeValue()));
                }

            }
        }
        return version;
    }
}
