package org.mycore.mets.model.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.jdom2.Element;

/**
 * @author Matthias Eichner
 */
public class LogicalDiv extends AbstractDiv<LogicalDiv> {

    public final static String XML_AMDID = "ADMID";

    public final static String XML_DMDID = "DMDID";

    public final static String XML_ORDER = "ORDER";

    public final static String XML_LABEL = "LABEL";

    protected String label;

    protected HashMap<String, LogicalDiv> subDivContainer;

    protected List<Fptr> fptrList;

    protected Integer order;

    private LogicalDiv parent;

    protected String dmdId, amdId;

    private Mtpr mtpr;

    /**
     * @param id
     *            the id of the div
     * @param type
     *            the type attribute
     * @param label
     *            the label of the div
     * @param order
     *            the order of the div
     */
    public LogicalDiv(String id, String type, String label, Integer order) {
        this.subDivContainer = new LinkedHashMap<String, LogicalDiv>();
        this.fptrList = new ArrayList<Fptr>();
        this.setId(id);
        this.setType(type);
        this.setLabel(label);
        this.setOrder(order);
    }

    public LogicalDiv(String id, String type, String label) {
        this(id, type, label, -1, null, null);
    }

    public LogicalDiv(String id, String type, String label, Integer order, String amdId, String dmdId) {
        this(id, type, label, order);
        this.setAmdId(amdId);
        this.setDmdId(dmdId);
    }

    @Override
    public void add(LogicalDiv child) {
        if (child == null) {
            return;
        }
        child.setParent(this);
        this.subDivContainer.put(child.getId(), child);
    }

    @Override
    public void remove(LogicalDiv divToDelete) {
        for (LogicalDiv lsd : subDivContainer.values().toArray(new LogicalDiv[0])) {
            if (lsd == divToDelete) {
                this.subDivContainer.remove(lsd.getId());
                lsd.setParent(null);
                return;
            } else {
                removeFromChildren(lsd.getChildren(), divToDelete);
            }
        }
    }

    private void removeFromChildren(List<LogicalDiv> children, LogicalDiv divToDelete) {
        for (LogicalDiv child : children) {
            if (child == divToDelete) {
                child.getParent().remove(divToDelete.getId());
                child.setParent(null);
                return;
            } else {
                removeFromChildren(child.getChildren(), divToDelete);
            }
        }
    }

    /**
     * Removes the div from this logical div
     * 
     * @param identifier
     *            the identifier
     */
    public void remove(String identifier) {
        if (identifier == null) {
            return;
        }
        subDivContainer.remove(identifier);
    }

    public List<LogicalDiv> getChildren() {
        return new Vector<LogicalDiv>(subDivContainer.values());
    }

    protected void setParent(LogicalDiv parentToSet) {
        this.parent = parentToSet;
    }

    /**
     * @return the parent of this div
     */
    public LogicalDiv getParent() {
        return this.parent;
    }

    /**
     * Returns a modifiable list of file pointers.
     * 
     * @return list of file pointers
     */
    public List<Fptr> getFptrList() {
        return fptrList;
    }

    /**
     * Returns a {@link LogicalDiv} with the given id. This checks
     * all descendants.
     * 
     * @param identifier @ID attribute to find  
     * @return a {@link LogicalDiv} with the given id or null
     */
    public LogicalDiv getLogicalSubDiv(String identifier) {
        for (LogicalDiv child : subDivContainer.values()) {
            if (child.getId().equals(identifier)) {
                return  child;
            } else {
                LogicalDiv logicalSubDiv = lookupChildren(child.getChildren(), identifier);
                if (logicalSubDiv != null) {
                    return logicalSubDiv;
                }
            }
        }
        return null;
    }

    private LogicalDiv lookupChildren(List<LogicalDiv> children, String identifier) {
        for (LogicalDiv child : children) {
            if (child.getId().equals(identifier)) {
                return child;
            } else {
                lookupChildren(child.getChildren(), identifier);
            }
        }
        return null;
    }

    /**
     * Sets the label attribute.
     * 
     * @param label label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the order attribute.
     * 
     * @param order order to set
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * @return the value of the order attribute
     */
    public Integer getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return this.id;
    }

    public void setAmdId(String amdId) {
        this.amdId = amdId;
    }

    public void setDmdId(String dmdId) {
        this.dmdId = dmdId;
    }

    public String getAmdId() {
        return amdId;
    }

    public String getDmdId() {
        return dmdId;
    }

    public void setMtpr(Mtpr mtpr) {
        this.mtpr = mtpr;
    }

    public Mtpr getMtpr() {
        return this.mtpr;
    }

    @Override
    public Element asElement() {
        Element div = super.asElement();
        if (this.getLabel() != null && !this.getLabel().equals("")) {
            div.setAttribute(XML_LABEL, this.getLabel());
        }
        if (this.getOrder() != null && this.getOrder() != -1) {
            div.setAttribute(XML_ORDER, String.valueOf(this.getOrder()));
        }
        Iterator<LogicalDiv> sbDivIterator = this.subDivContainer.values().iterator();
        while (sbDivIterator.hasNext()) {
            div.addContent(sbDivIterator.next().asElement());
        }
        for (Fptr fptr : getFptrList()) {
            div.addContent(fptr.asElement());
        }
        if (this.getAmdId() != null && !this.getAmdId().equals("")) {
            div.setAttribute(XML_AMDID, this.getAmdId());
        }
        if (this.getDmdId() != null && !this.getDmdId().equals("")) {
            div.setAttribute(XML_DMDID, this.getDmdId());
        }
        if (mtpr != null) {
            div.addContent(mtpr.asElement());
        }
        return div;
    }
}
