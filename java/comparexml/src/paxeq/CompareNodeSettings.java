package paxeq;

/**
 * Created by IntelliJ IDEA.
 * User: Программирование
 * Date: 18.08.2005
 * Time: 9:31:22
 * To change this template use File | Settings | File Templates.
 */
public class CompareNodeSettings {
    boolean IgnoreElements = false;
    boolean IgnoreAttributes = false;
    boolean IgnoreCData = false;
    boolean IgnoreText = false;
    boolean IgnoreComments = false;
    boolean IgnoreEntities = false;
    boolean IgnorePI = false;


    public CompareNodeSettings(boolean ignoreElements, boolean ignoreAttributes, boolean ignoreCData, boolean ignoreText, boolean ignoreComments, boolean ignoreEntities, boolean ignorePI) {
        IgnoreElements = ignoreElements;
        IgnoreAttributes = ignoreAttributes;
        IgnoreCData = ignoreCData;
        IgnoreText = ignoreText;
        IgnoreComments = ignoreComments;
        IgnoreEntities = ignoreEntities;
        IgnorePI = ignorePI;
    }

    public CompareNodeSettings(boolean ignoreElements, boolean ignoreAttributes, boolean ignoreCData, boolean ignoreText, boolean ignoreComments, boolean ignoreEntities) {
        IgnoreElements = ignoreElements;
        IgnoreAttributes = ignoreAttributes;
        IgnoreCData = ignoreCData;
        IgnoreText = ignoreText;
        IgnoreComments = ignoreComments;
        IgnoreEntities = ignoreEntities;
    }

    public CompareNodeSettings(boolean ignoreElements, boolean ignoreAttributes, boolean ignoreCData, boolean ignoreText, boolean ignoreComments) {
        IgnoreElements = ignoreElements;
        IgnoreAttributes = ignoreAttributes;
        IgnoreCData = ignoreCData;
        IgnoreText = ignoreText;
        IgnoreComments = ignoreComments;
    }

    public CompareNodeSettings(boolean ignoreElements, boolean ignoreAttributes, boolean ignoreCData, boolean ignoreText) {
        IgnoreElements = ignoreElements;
        IgnoreAttributes = ignoreAttributes;
        IgnoreCData = ignoreCData;
        IgnoreText = ignoreText;
    }

    public CompareNodeSettings(boolean ignoreElements, boolean ignoreAttributes, boolean ignoreCData) {
        IgnoreElements = ignoreElements;
        IgnoreAttributes = ignoreAttributes;
        IgnoreCData = ignoreCData;
    }

    public CompareNodeSettings(boolean ignoreElements, boolean ignoreAttributes) {
        IgnoreElements = ignoreElements;
        IgnoreAttributes = ignoreAttributes;
    }

    public CompareNodeSettings(boolean ignoreElements) {
        IgnoreElements = ignoreElements;
    }

    public boolean isIgnoreElements() {
        return IgnoreElements;
    }

    public void setIgnoreElements(boolean ignoreElements) {
        IgnoreElements = ignoreElements;
    }

    public boolean isIgnoreAttributes() {
        return IgnoreAttributes;
    }

    public void setIgnoreAttributes(boolean ignoreAttributes) {
        IgnoreAttributes = ignoreAttributes;
    }

    public boolean isIgnoreCData() {
        return IgnoreCData;
    }

    public void setIgnoreCData(boolean ignoreCData) {
        IgnoreCData = ignoreCData;
    }

    public boolean isIgnoreText() {
        return IgnoreText;
    }

    public void setIgnoreText(boolean ignoreText) {
        IgnoreText = ignoreText;
    }

    public boolean isIgnoreComments() {
        return IgnoreComments;
    }

    public void setIgnoreComments(boolean ignoreComments) {
        IgnoreComments = ignoreComments;
    }

    public boolean isIgnoreEntities() {
        return IgnoreEntities;
    }

    public void setIgnoreEntities(boolean ignoreEntities) {
        IgnoreEntities = ignoreEntities;
    }

    public boolean isIgnorePI() {
        return IgnorePI;
    }

    public void setIgnorePI(boolean ignorePI) {
        IgnorePI = ignorePI;
    }
}
