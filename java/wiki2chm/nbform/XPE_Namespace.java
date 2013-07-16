package nbform;

/**
 * Объект содержит сведения об некотором пространстве имен
 */
public class XPE_Namespace implements Comparable {
    /**
     * префикс пространства имен
     */
    public String prefix;
    /**
     * URI в который разрешается данный префикс
     */
    public String uri;

    public XPE_Namespace(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public boolean equals(Object o) {
        return ((XPE_Namespace) o).prefix.equals(prefix)
                && ((XPE_Namespace) o).uri.equals(uri);
    }

    public int compareTo(Object o) {
        XPE_Namespace other = (XPE_Namespace) o;
        int x = prefix.compareTo(other.prefix);
        if (x == 0) {
            x = uri.compareTo(other.uri);
        }
        return x;
    }

    public String toString() {
        return "XPE.Namespace[prefix=" + prefix + ",uri=" + uri + "]";
    }
}
