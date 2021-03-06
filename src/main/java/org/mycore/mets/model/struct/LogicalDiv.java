package org.mycore.mets.model.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import org.jdom2.Element;

/**
 * @author Matthias Eichner
 */
public class LogicalDiv extends AbstractDiv<LogicalDiv> {

    protected HashMap<String, LogicalDiv> subDivContainer;

    protected List<Fptr> fptrList;

    private LogicalDiv parent;

    private Mptr mptr;

    /**
     * @param id
     *            the id of the div
     * @param type
     *            the type attribute
     * @param label
     *            the label of the div
     */
    public LogicalDiv(String id, String type, String label) {
        this.subDivContainer = new LinkedHashMap<>();
        this.fptrList = new ArrayList<>();
        this.setId(id);
        this.setType(type);
        this.setLabel(label);
        this.setOrder(null);
    }

    public LogicalDiv(String id, String type, String label, String admId, String dmdId) {
        this(id, type, label);
        this.setAdmId(admId);
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
        return new Vector<>(subDivContainer.values());
    }

    protected void setParent(LogicalDiv parentToSet) {
        this.parent = parentToSet;
    }

    /**
     * Returns the index position of this div in its parent.
     * 
     * @return the index position
     */
    public Optional<Integer> getPositionInParent() {
        if (this.parent == null) {
            return Optional.empty();
        }
        return Optional.of(this.parent.getChildren().indexOf(this));
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
     * @param identifier ID attribute to find  
     * @return a {@link LogicalDiv} with the given id or null
     */
    public LogicalDiv getLogicalSubDiv(String identifier) {
        for (LogicalDiv child : subDivContainer.values()) {
            if (child.getId().equals(identifier)) {
                return child;
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
     * Returns a list of all descendant div's. Be aware that there
     * is no specific order.
     * 
     * @return list of descendants
     */
    public List<LogicalDiv> getDescendants() {
        List<LogicalDiv> descendants = new ArrayList<>();
        for (LogicalDiv subDiv : subDivContainer.values()) {
            descendants.add(subDiv);
            descendants.addAll(subDiv.getDescendants());
        }
        return descendants;
    }

    @Override
    public String toString() {
        return this.id;
    }

    public void setMptr(Mptr mptr) {
        this.mptr = mptr;
    }

    public Mptr getMptr() {
        return this.mptr;
    }

    @Override
    public Element asElement() {
        Element div = super.asElement();
        if (this.mptr != null) {
            div.addContent(this.mptr.asElement());
        }
        for (LogicalDiv logicalDiv : this.subDivContainer.values()) {
            div.addContent(logicalDiv.asElement());
        }
        for (Fptr fptr : getFptrList()) {
            div.addContent(fptr.asElement());
        }
        return div;
    }

}
