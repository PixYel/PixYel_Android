package de.pixyel.dhbw.pixyel.ConnectionManager;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Josua Frank
 */
public class XML {

    private File xmlFile;//File only for this file
    private final Element e;//Element only for this XML-Type(Element)
    private Document doc;//Document only for this XML-File(test.xml)
    private static LinkedHashMap<Element, XML> LIST = new LinkedHashMap<>();
    private boolean autosave = false;//Saves the file after EVERY change
    private static final String EMPTYKEYWORD = "EMPTY";

    //DIENEN NUR DAMIT MAN SIE NICHT IN JEDER METHODE NEU DEKLARIEREN MUSS, KEINE INHALTSABLAGE
    private NodeList ch;//Children
    private Node chi;
    private NamedNodeMap a;//Attributes
    private Node p;//Parent

    /**
     * Creates a new XML without a file representation
     *
     * @param name name of the XML Node
     * @return The new XML instance
     */
    public static XML createNewXML(String name) {
        return new XML(name);
    }

    /**
     * Creates a new XML file with the specific rootname
     *
     * @param file The XML file to be opened
     * @param rootname The name of the first node
     * @return The new XML instance
     */
    public static XML createNewXML(File file, String rootname) {
        return new XML(file, rootname);
    }

    /**
     * Opens a existing XML file
     *
     * @param file The XML file to be opened
     * @return The new XML instance
     * @throws XMLException pixyel_backend.xml.XML.XMLException Raised when the XML file
     * contains errors
     */
    public static XML openXML(File file) throws XMLException {
        return new XML(file);
    }

    /**
     * Opens a existing XML by its String
     *
     * @param xml The XML String to be opened
     * @return The new XML instance
     * @throws XMLException pixyel_backend.xml.XML.XMLException Raised when the XML string
     * contains errors
     */
    public static XML openXML(String xml) throws XMLException {
        return new XML(xml, true);
    }

    private XML(String name) {
        doc = getDoc();
        if (name.equals("")) {
            name = EMPTYKEYWORD;
        }
        e = doc.createElement(name);
        LIST.put(e, this);
    }

    private XML(File file) throws XMLException {
        e = readXML(file);
        LIST.put(e, this);
    }

    private XML(String toRead, boolean useless) throws XMLException {
        e = readXML(toRead);
        LIST.put(e, this);
    }

    private XML(File file, String rootname) {
        if (rootname == null) {
            rootname = "";
        }
        e = createXML(file, rootname);
        LIST.put(e, this);
        if (e != null && autosave) {
            reloadFile();
        }
    }

    /**
     * for existing XMLs to read from
     *
     * @param file
     * @param rootname
     */
    private Element createXML(File file, String rootname) {
        xmlFile = file;
        Element element = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            xmlFile.createNewFile();
            if (doc == null) {
                doc = builder.newDocument();
            }
            if (rootname.isEmpty()) {
                String root = file.getName();
                if (root.contains(".")) {
                    element = doc.createElement(xmlFile.getName().substring(0, xmlFile.getName().lastIndexOf(".")));
                } else {
                    element = doc.createElement(root);
                }
            } else {
                element = doc.createElement(rootname);
            }
        } catch (ParserConfigurationException | IOException ex) {
            System.err.println("Fehler: " + ex);
        }
        return element;
    }

    private Element readXML(File file) throws XMLException {
        xmlFile = file;
        Document document = isValid(xmlFile);
        Element element = document.getDocumentElement();
        element.normalize();
        doc = document;
        return element;
    }

    private Element readXML(String string) throws XMLException {
        Document document = isValid(string);
        Element element = document.getDocumentElement();
        element.normalize();
        doc = document;
        return element;
    }

    private XML(Element element) {
        e = element;
        XML.LIST.put(e, this);
    }

    /**
     *
     * @param xmlFile The XML file to be parsed
     * @return null if invalid, a Document if valid
     * @throws XMLException Yes, a lot of shit can happen here
     */
    private Document isValid(File xmlFile) throws XMLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(xmlFile);
        } catch (ParserConfigurationException ex) {
            throw new XMLException("[INTERNAL] The DocumentBuilder cannot be created with with the requested configuration: " + ex);
        } catch (SAXException ex) {
            throw new XMLException("Parsing error occured: " + ex);
        } catch (IOException ex) {
            throw new XMLException("IO error occured: " + ex);
        }
    }

    /**
     *
     * @param string The String to be parsed
     * @return null if invalid, a Document if valid
     * @throws XMLException Yes, a lot of shit can happen here
     */
    private Document isValid(String string) throws XMLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(string));
            return builder.parse(is);
        } catch (ParserConfigurationException ex) {
            throw new XMLException("[INTERNAL] The DocumentBuilder cannot be created with with the requested configuration: " + ex);
        } catch (SAXException ex) {
            throw new XMLException("Parsing error occured: " + ex);
        } catch (IOException ex) {
            throw new XMLException("IO error occured: " + ex);
        }
    }

    /**
     * Checks wether this node has attributes or not
     *
     * @return true if the node has attributes, false if the node has no
     * attributes
     */
    public boolean hasAttributes() {
        return e.hasAttributes();
    }

    /**
     * Checks wether this node has text-content or not
     *
     * @return true if the node has text content, false if the content has no
     * text content
     */
    public boolean hasContent() {
        if (e.hasChildNodes()) {
            for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
                if ((chi = ch.item(i)).getNodeType() == Node.TEXT_NODE) {
                    return !chi.getTextContent().matches("[\\s]*");//not only whitespaces
                }
            }
        }
        return false;
    }

    /**
     * Checks wether this node has parent or not
     *
     * @return true, if this node has a parent, false if this node is the first
     * node in the xml tree
     */
    public boolean hasParent() {
        return !isRoot();

    }

    /**
     * Checks if this node has child nodes
     *
     * @return true, if this node has at least one child node, false if it has
     * no child nodes
     */
    public boolean hasChildren() {
        if (!e.hasChildNodes()) {
            return false;
        }
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            if (ch.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the name of the node
     *
     * @return The name of the node
     */
    public String getName() {
        return e.getTagName();
    }

    /**
     * Returns the attributes as a linked hashmap
     *
     * @return The attributes a a linked hashmap (key = attributename, value =
     * attributevalue)
     */
    public LinkedHashMap<String, String> getAttributes() {
        LinkedHashMap<String, String> r = new LinkedHashMap<>();
        for (int i = 0; i < (a = e.getAttributes()).getLength(); i++) {
            r.put(a.item(i).getNodeName(), a.item(i).getNodeValue());
        }
        return r;
    }

    /**
     * Returns the requested attribute value of this node
     *
     * @param name The name of the requested attribute
     * @return The value of the requested attribute
     */
    public String getAttribute(String name) {
        return e.getAttribute(name);
    }

    /**
     * Returns the FIRST attribute value of this node or an empty String, if
     * this node has no attributes
     *
     * @return The value of the FIRST attribute
     */
    public String getFirstAttribute() {
        for (int i = 0; i < (a = e.getAttributes()).getLength(); i++) {
            return a.item(i).getNodeValue();
        }
        return "";
    }

    /**
     * Returns the LAST attribute value of this node or an empty String, if this
     * node has no attributes
     *
     * @return The value of the LAST attribute
     */
    public String getLastAttribute() {
        for (int i = (a = e.getAttributes()).getLength(); i > 0; i--) {
            return a.item(i - 1).getNodeValue();
        }
        return "";
    }

    /**
     * Returns the text content of this node
     *
     * @return The text content of this node, if theres no content, then it
     * returns "" (empty String)
     */
    public String getContent() {
        if (e.hasChildNodes()) {
            for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
                if ((chi = ch.item(i)).getNodeType() == Node.TEXT_NODE) {
                    if (!chi.getTextContent().matches("[\\s]*")) {//Not only whitepaces
                        return chi.getTextContent();
                    }
                    return "";
                }
            }
        }
        return "";
    }

    /**
     * Returns the parent of this node
     *
     * @return The parent of this node or null if this node has no parent
     */
    public XML getParent() {
        if (!isRoot()) {
            return getXMLByElement((Element) e.getParentNode());
        }
        return null;
    }

    /**
     * Returns all children of this node
     *
     * @return All children of this node as ArrayList
     */
    public ArrayList<XML> getChildren() {
        ArrayList<XML> r = new ArrayList<>();
        boolean childDeleted = false;
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            if (childDeleted) {
                i--;
                childDeleted = false;
            }
            if ((chi = ch.item(i)).getNodeName() == null || chi.getNodeName().matches("[\\s]*")) {
                e.removeChild(chi);
                childDeleted = true;
            } else if (chi.getNodeType() == Node.ELEMENT_NODE) {
                r.add(getXMLByElement((Element) ch.item(i)));
            }
        }
        return r;
    }

    /**
     * Returns the requested child, children or null if the child doesnt exist
     *
     * @param name The name of the requested child or the requested children
     * @return A ArrayList with zero, one or more children matching this name
     */
    public ArrayList<XML> getChild(String name) {
        ArrayList<XML> r = new ArrayList<>();
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            chi = ch.item(i);
            if (chi.getNodeType() == Node.ELEMENT_NODE && chi.getNodeName().equals(name)) {
                r.add(getXMLByElement((Element) chi));
            }
        }
        return r;
    }

    /**
     * Returns the first child of this node
     *
     * @return The first child of this node as XML-object or null if there isnt
     * a first child
     */
    public XML getFirstChild() {
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            if ((chi = ch.item(i)).getNodeType() == Node.ELEMENT_NODE) {
                return getXMLByElement((Element) chi);
            }
        }
        return null;
    }

    /**
     * Returns the first child with the requested name
     *
     * @param name The requested name of the wanted child
     * @return The first child whose name matches your requested name or null if
     * there isnt a first child
     */
    public XML getFirstChild(String name) {
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            if ((chi = ch.item(i)).getNodeType() == Node.ELEMENT_NODE && chi.getNodeName().equals(name)) {
                return getXMLByElement((Element) chi);
            }
        }
        return null;
    }

    /**
     * Returns the last child of this node
     *
     * @return The last child of this node as XML-object
     */
    public XML getLastChild() {
        chi = null;
        for (int i = (ch = e.getChildNodes()).getLength(); i > 0; i--) {
            if ((chi = ch.item(i - 1)).getNodeType() == Node.ELEMENT_NODE) {
                return getXMLByElement((Element) chi);
            }
        }
        return null;
    }

    /**
     * Returns the last child with the requested name
     *
     * @param name The requested name of the wanted child
     * @return The last child whose name matches your requested name or null if
     * there isnt a first child
     */
    public XML getLastChild(String name) {
        for (int i = (ch = e.getChildNodes()).getLength(); i > 0; i--) {
            if ((chi = ch.item(i - 1)).getNodeType() == Node.ELEMENT_NODE && chi.getNodeName().equals(name)) {
                return getXMLByElement((Element) chi);
            }
        }
        return null;
    }

    /**
     * Returns the children which are having the requested attribute value
     *
     * @param attributeValue The attribute to look for
     * @return The List of Children
     */
    public ArrayList<XML> getChildrenByAttribute(String attributeValue) {
        ArrayList<XML> r = new ArrayList<>();
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            chi = ch.item(i);
            if (chi.getNodeType() == Node.ELEMENT_NODE) {
                for (int j = 0; j < (a = chi.getAttributes()).getLength(); j++) {
                    if (a.item(j).getNodeValue().equals(attributeValue)) {
                        r.add(getXMLByElement((Element) chi));
                    }
                }
            }
        }
        return r;
    }

    ArrayList<XML> alreadyAppended = new ArrayList<>();

    /**
     * Adds an attribute to this node
     *
     * @param name The name of this attribute
     * @param value The value of this attribute
     * @return This node (for convenience reasons)
     */
    public XML addAttribute(String name, String value) {
        e.setAttribute(name, value);
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Sets the content of this node
     *
     * @param content The content to be set on this node
     * @return This node (for convenience reasons)
     */
    public XML setContent(String content) {
        doc = getDoc();
        e.appendChild(doc.createTextNode(content));
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Adds child nodes to this node
     *
     * @param children zero, one or more child nodes as XML-object to add
     * @return A ArrayList of the newly added children
     */
    public ArrayList<XML> addChildren(XML... children) {
        doc = getDoc();
        ArrayList<XML> r = new ArrayList<>();
        for (XML child : children) {
            if (!alreadyAppended.contains(child)) {
                if (child.e.getOwnerDocument().equals(doc)) {
                    chi = e.appendChild(child.e);
                } else {
                    chi = e.appendChild(doc.adoptNode((Node) child.e));
                }
                alreadyAppended.add(child);
            } else//bla
                if (doc.equals(child.e.getOwnerDocument())) {
                    chi = e.appendChild(child.e.cloneNode(true));
                } else {
                    chi = e.appendChild(doc.adoptNode((Node) child.e.cloneNode(true)));
                }
            r.add(getXMLByElement((Element) chi));
        }
        if (autosave) {
            reloadFile();
        }
        return r;
    }

    /**
     * Adds a child to this node
     *
     * @param child The new child to be added
     * @return The newly added child as XML
     */
    public XML addChild(XML child) {
        doc = getDoc();
        if (!alreadyAppended.contains(child)) {
            if (child.e.getOwnerDocument().equals(doc)) {
                chi = e.appendChild(child.e);
            } else {
                chi = e.appendChild(doc.adoptNode((Node) child.e));
            }
            alreadyAppended.add(child);
        } else//bla
            if (doc.equals(child.e.getOwnerDocument())) {
                chi = e.appendChild(child.e.cloneNode(true));
            } else {
                chi = e.appendChild(doc.adoptNode((Node) child.e.cloneNode(true)));
            }
        if (autosave) {
            reloadFile();
        }
        return getXMLByElement((Element) chi);
    }

    /**
     * Adds new children to this node
     *
     * @param children The zero, one or more names of the new children
     * @return A ArrayList of the newly added children
     */
    public ArrayList<XML> addChildren(String... children) {
        doc = getDoc();
        ArrayList<XML> r = new ArrayList<>();
        XML child;
        for (String childS : children) {
            child = new XML(childS);
            if (!alreadyAppended.contains(child)) {
                if (child.e.getOwnerDocument().equals(doc)) {
                    chi = e.appendChild(child.e);
                } else {
                    chi = e.appendChild(doc.adoptNode((Node) child.e));
                }
                alreadyAppended.add(child);
            } else//
                if (doc.equals(child.e.getOwnerDocument())) {
                    chi = e.appendChild(child.e.cloneNode(true));
                } else {
                    chi = e.appendChild(doc.adoptNode((Node) child.e.cloneNode(true)));
                }
            r.add(getXMLByElement((Element) chi));
        }
        if (autosave) {
            reloadFile();
        }
        return r;
    }

    /**
     * Adds a child to this node
     *
     * @param child The name of the new child to be added
     * @return The newly added child as XML
     */
    public XML addChild(String child) {
        doc = getDoc();
        XML childXML = new XML(child);
        if (!alreadyAppended.contains(childXML)) {
            if (childXML.e.getOwnerDocument().equals(doc)) {
                chi = e.appendChild(childXML.e);
            } else {
                chi = e.appendChild(doc.adoptNode((Node) childXML.e));
            }
            alreadyAppended.add(childXML);
        } else//
            if (doc.equals(childXML.e.getOwnerDocument())) {
                chi = e.appendChild(childXML.e.cloneNode(true));
            } else {
                chi = e.appendChild(doc.adoptNode((Node) childXML.e.cloneNode(true)));
            }
        if (autosave) {
            reloadFile();
        }
        return getXMLByElement((Element) chi);
    }

    /**
     * Removes this node and with it all its children, attributes and contents
     */
    public void remove() {
        if (hasParent()) {
            (chi = e.getParentNode()).removeChild(e);
            if (autosave) {
                getXMLByElement((Element) chi).reloadFile();
            }
        }
    }

    /**
     * Removes a attribute of this node
     *
     * @param name The name of the attribute to be removed
     * @return This node (for convenience reasons)
     */
    public XML removeAttribute(String name) {
        e.removeAttribute(name);
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Removes the FIRST attribute of this node
     *
     * @return This node (for convenience reasons)
     */
    public XML removeFirstAttribute() {
        for (int i = 0; i < (a = e.getAttributes()).getLength(); i++) {
            e.removeAttribute(a.item(i).getNodeName());
            if (autosave) {
                reloadFile();
            }
            return this;
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Removes the LAST attribute of this node
     *
     * @return This node (for convenience reasons)
     */
    public XML removeLastAttribute() {
        for (int i = (a = e.getAttributes()).getLength(); i > 0; i--) {
            e.removeAttribute(a.item(i - 1).getNodeName());
            if (autosave) {
                reloadFile();
            }
            return this;
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Removes ALL attributes matching the specified value
     *
     * @param value The specified name after which the all attributes equaling
     * this value will be removed
     * @return This node (for convenience reasons)
     */
    public XML removeAttributesByValue(String value) {
        for (int i = (a = e.getAttributes()).getLength(); i > 0; i--) {
            if (a.item(i - 1).getNodeValue().equals(value)) {
                e.removeAttribute(a.item(i - 1).getNodeName());
            }
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Removes the FIRST attributes matching the specified value
     *
     * @param value The specified name after which the first attribute equaling
     * this value will be removed
     * @return This node (for convenience reasons)
     */
    public XML removeFirstAttributeByValue(String value) {
        for (int i = 0; i < (a = e.getAttributes()).getLength(); i++) {
            if (a.item(i).getNodeValue().equals(value)) {
                e.removeAttribute(a.item(i).getNodeName());
                if (autosave) {
                    reloadFile();
                }
                return this;
            }
        }
        return this;
    }

    /**
     * Removes the LAST attributes matching the specified value
     *
     * @param value The specified name after which the last attribute equaling
     * this value will be removed
     * @return This node (for convenience reasons)
     */
    public XML removeLastAttributeByValue(String value) {
        for (int i = (a = e.getAttributes()).getLength(); i > 0; i--) {
            if (a.item(i - 1).getNodeValue().equals(value)) {
                e.removeAttribute(a.item(i - 1).getNodeName());
                if (autosave) {
                    reloadFile();
                }
                return this;
            }
        }
        return this;
    }

    /**
     * Removes the content of this current node
     *
     * @return This node (for convenience reasons)
     */
    public XML removeContent() {
        if (hasContent()) {
            for (int i = (ch = e.getChildNodes()).getLength(); i > 0; i--) {
                if ((chi = ch.item(i - 1)).getNodeType() == Node.TEXT_NODE) {
                    e.removeChild(chi);
                }
            }
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Removes a child specified by its name
     *
     * @param name The name of the child to be removed
     * @return This node (for convenience reasons)
     */
    public XML removeChildren(String name) {
        for (int i = (ch = e.getChildNodes()).getLength(); i > 0; i--) {
            if (((chi = ch.item(i - 1)).getNodeName().equals(name))) {
                e.removeChild(chi);
            }
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Removes a child specified by its XML-object
     *
     * @param child The XML-object of the child to be removed
     * @return This node (for convenience reasons)
     */
    public XML removeChild(XML child) {
        e.removeChild(child.e);
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Removes the first child of this node
     *
     * @return This node (for convenience reasons)
     */
    public XML removeFirstChild() {
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            if ((chi = ch.item(i)).getNodeType() == Node.ELEMENT_NODE) {
                e.removeChild(chi);
                if (autosave) {
                    reloadFile();
                }
                return this;
            }
        }
        return this;
    }

    /**
     * Removes the last child of this node
     *
     * @return This node (for convenience reasons)
     */
    public XML removeLastChild() {
        chi = null;
        for (int i = (ch = e.getChildNodes()).getLength(); i > 0; i--) {
            if ((chi = ch.item(i - 1)).getNodeType() == Node.ELEMENT_NODE) {
                e.removeChild(chi);
                if (autosave) {
                    reloadFile();
                }
                return this;
            }
        }
        return this;
    }

    /**
     * Removes ALL attributes of this node
     *
     * @return This node (for convenience reasons)
     */
    public XML clearAttributes() {
        for (int i = (a = e.getAttributes()).getLength(); i > 0; i--) {
            e.removeAttribute(a.item(i - 1).getNodeName());
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Removes the text-content of this node
     *
     * @return This node (for convenience reasons)
     */
    public XML clearContent() {
        if (e.hasChildNodes()) {
            for (int i = (ch = e.getChildNodes()).getLength(); i > 0; i--) {
                if ((chi = ch.item(i - 1)).getNodeType() == Node.TEXT_NODE) {
                    e.removeChild(chi);
                }
            }
            if (autosave) {
                reloadFile();
            }
        }
        return this;
    }

    /**
     * Removes ALL children of this node
     *
     * @return This node (for convenience reasons)
     */
    public XML clearChildren() {
        for (int i = (ch = e.getChildNodes()).getLength(); i > 0; i--) {
            e.removeChild(ch.item(i - 1));
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Sets the file in which this XML-object is going to be stored in
     *
     * @param file The file in which this XML-object is going to be stored in
     */
    public void setFileToSaveIn(File file) {
        if (hasParent()) {
            getParent().setFileToSaveIn(file);
            return;
        }
        xmlFile = file;
    }

    /**
     * Saves this XML-object to the specified file
     */
    public void save() {
        if (hasParent()) {
            getParent().save();
            return;
        }
        if (xmlFile == null) {
            System.err.println("Fehler, keine Datei angegeben, bitte benutze saveTo(file) oder definiere die Datei mit setFileToSaveIn(file)");
            return;
        }
        saveTo(xmlFile);
    }

    /**
     * Saves this XML-object in the specified filen \nDoes NOT change the
     * standard file to save in, use @link setFileToSaveIn() for this
     *
     * @param toSaveIn The file in which this XML-object is going to be stored
     * in
     */
    public void saveTo(File toSaveIn) {
        if (hasParent()) {
            getParent().saveTo(toSaveIn);
            return;
        }
        try {
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(e);
            if (!toSaveIn.exists()) {
                toSaveIn.getParentFile().mkdir();
                toSaveIn.createNewFile();
            }
            StreamResult result = new StreamResult(toSaveIn);

            // Output to console for testing
            transformer.transform(source, result);
            System.out.println("File saved!");

        } catch (IOException | TransformerException ex) {
            System.err.println("Fehler: " + ex);
        }
    }

    /**
     * Sets the autosave setting. Autosave saves after EVERY change
     *
     * @param flag The Autosave flag
     */
    public void setAutosave(boolean flag) {
        autosave = flag;
        save();
    }

    private Document getDoc() {
        if (hasParent()) {
            return getParent().getDoc();
        } else if (doc == null) {
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException ex) {
                System.err.println("Error, could not create Document instance");
                return null;
            }
        } else {
            return doc;
        }
    }

    private void reloadFile() {
        if (hasParent()) {
            getParent().reloadFile();
            return;
        }
        if (xmlFile != null) {
            save();
        }
    }

    private XML getXMLByElement(Element e) {
        if (XML.LIST.containsKey(e)) {
            return XML.LIST.get(e);
        }
        return new XML(e);
    }

    /**
     * Returns wether this node is the last child or not
     *
     * @return true if this node is the last child of its parents, false if it
     * is not the last child of its parents
     */
    public boolean isLastChild() {
        if (!isRoot()) {
            while ((chi = e.getParentNode().getLastChild()).getNodeType() == Node.TEXT_NODE && chi.getTextContent().matches("[\\s]*")) {
                e.getParentNode().removeChild(chi);
            }
            return e.getParentNode().getLastChild().isSameNode(e);
        } else {
            return true;
        }

    }

    private boolean isRoot() {
        if (e == null) {
            return true;
        }
        p = e.getParentNode();
        short t = 0;
        if (p != null) {
            t = p.getNodeType();
        }
        return p == null || (t != Node.ELEMENT_NODE && t != Node.TEXT_NODE);
    }

    /**
     * Returns the content of the current node
     *
     * @return The content of the current node as String in XML language
     */
    public String toStringOnlyThisNode() {
        try {
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");//removes this stuff: <?xml version='1.0' ?>

            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "0");

            StreamResult streamResult = new StreamResult(new StringWriter());
            transformer.transform(new DOMSource(e), streamResult);
            String result = streamResult.getWriter().toString();
            return result;

        } catch (TransformerException ex) {
            System.err.println("Fehler: " + ex);
        }
        return "";
    }

    /**
     * Returns the content the whole XML
     *
     * @return The content of the whole XML as String in XML language
     */
    @Override
    public String toString() {
        if (hasParent()) {
            return getParent().toString();
        }
        return toStringOnlyThisNode();
    }

    /**
     * Returns a fancy string graph of this XML-object
     *
     * @return A String graph of this XML-object
     */
    public String toStringGraph() {
        ArrayList<Boolean> uncles = new ArrayList<>();
        return toStringGraph(0, uncles);
    }

    private static final int WIDTHOFTHECHART = 3;

    private String toStringGraph(int depth, ArrayList<Boolean> unclesAndGreatunclesASO) {//Geschwister meiner Eltern, deren Eltern, usw
        //Funktioniert
        String string = "";
        //string += "[Depth: " + depth + "] [Size Of TRUE/FALSE: " + unclesAndGreatunclesASO.size() + "]";
        if (depth > 0) {
            if (isLastChild()) {
                string += "└─Name: \"" + getName() + "\" ";
            } else {
                string += "├─Name: \"" + getName() + "\" ";
            }
        } else {
            string += "Name: \"" + getName() + "\" ";
        }
        //Funktioniert
        if (hasAttributes()) {
            string += "; Attributes: ";
            boolean first = true;
            for (int i = 0; i < (a = e.getAttributes()).getLength(); i++) {
                if (!first) {
                    string += ", ";
                }
                first = false;
                string += "\"" + a.item(i).getNodeName() + "\" = \"" + a.item(i).getNodeValue() + "\" ";
            }
        }
        //Funktioniert
        if (hasContent()) {
            string += "; Content: \"" + getContent().replace("\n", "[NewLine]").replace("\t", "[TAB]") + "\" ";
        }
        if (hasParent()) {
            string += "; Parent: \"" + getParent().getName() + "\" ";
        }
        String temp = "";
        for (Boolean hasUncle : unclesAndGreatunclesASO) {
            if (hasUncle) {
                temp = "│" + temp;
                for (int i = WIDTHOFTHECHART; i > 1; i--) {
                    temp = " " + temp;
                }
            } else {
                for (int i = WIDTHOFTHECHART; i > 0; i--) {
                    temp = " " + temp;
                }
            }
        }
        if (depth > 0) {
            temp = temp.substring(0, (depth - 1) * WIDTHOFTHECHART);
        }
        string = new StringBuilder(temp).reverse().toString() + string;
        if (depth != 0) {
            string = "\n" + string;
        }
        if (hasChildren()) {
            if (!isRoot()) {
                if (!isLastChild()) {
                    unclesAndGreatunclesASO.add(Boolean.TRUE);
                } else {
                    unclesAndGreatunclesASO.add(Boolean.FALSE);
                }
            }
            ArrayList<XML> childs;
            string += "; Children: " + (childs = getChildren()).size() + " ";
            for (int i = 0; i < childs.size(); i++) {
                string += childs.get(i).toStringGraph(depth + 1, unclesAndGreatunclesASO);
            }
        }
        if (isLastChild()) {
            if (unclesAndGreatunclesASO.size() > 0) {
                unclesAndGreatunclesASO.remove(unclesAndGreatunclesASO.size() - 1);
            }
        }
        return string;
    }

    public class XMLException extends Exception {

        public XMLException() {
            super();
        }

        public XMLException(String message) {
            super(message);
        }

    }

}