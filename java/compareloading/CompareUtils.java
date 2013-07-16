package compareloading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this is utility class which suited for parsing & analizying trace results
 * from jre launch with option -verbose:class
 */
public class CompareUtils {
    /**
     * value object class holds pair of clazz name & jar file from which class was loaded
     */
    public static class ComparePair {
        /**
         * class name
         */
        String clazz;
        /**
         * jar file name
         */
        String file;

        ComparePair(String clazz, String file) {
            this.clazz = clazz;
            this.file = file;
        }

        /**
         * it's required to override this 2 methods for properly function collection operations
         *
         * @return
         */
        public int hashCode() {
            return clazz.hashCode() * 7 + file.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj == null)
                if (!(obj instanceof ComparePair)) return false;
            ComparePair cobj = (ComparePair) obj;
            return cobj.clazz.equals(clazz) && cobj.file.equals(file);
        }
    }

    /**
     * holds information when single class was loaded from two different locations
     */
    public static class CompareTroika {
        /**
         * class name
         */
        String clazz;
        /**
         * first jar file name
         */
        String file1;

        /**
         * first jar file name
         */
        String file2;

        public CompareTroika(String clazz, String file1, String file2) {
            this.clazz = clazz;
            this.file1 = file1;
            this.file2 = file2;
        }
    }


    /**
     * helper inner class holds information about parsing results
     */
    public static class CompareContainer {
        /**
         * pairs of clazz & file derived from analuzing first application log
         */
        public List<ComparePair> pairs1;
        /**
         * pairs of clazz & file derived from analuzing second application log
         */
        public List<ComparePair> pairs2;
        /**
         * list of pairs common for both first & second log
         */
        public List<ComparePair> pairs_common;
        /**
         * list of pairs which are exist in first no second log
         */
        public List<ComparePair> in_1_not_in_2;
        /**
         * list of pairs which are exist in second no first log
         */
        public List<ComparePair> in_2_not_in_1;
        /**
         * list of classes which were loaded from different places
         */
        public List<CompareTroika> different_files_for_one_clazz;

        public CompareContainer(List<ComparePair> pairs1, List<ComparePair> pairs2, List<ComparePair> pairs_common, List<ComparePair> in_1_not_in_2, List<ComparePair> in_2_not_in_1, List<CompareTroika> different_files_for_one_clazz) {
            this.pairs1 = pairs1;
            this.pairs2 = pairs2;
            this.pairs_common = pairs_common;
            this.in_1_not_in_2 = in_1_not_in_2;
            this.in_2_not_in_1 = in_2_not_in_1;
            this.different_files_for_one_clazz = different_files_for_one_clazz;
        }
    }

    /**
     * main method takes as input args 2 strings (log for first & second application )
     * @param s1 log for first application
     * @param s2 log for second application
     * @return container with comparission results
     */
    public static CompareContainer compare(String s1, String s2) {
        // parse both logs
        List<ComparePair> pairs1 = parseString(s1);
        List<ComparePair> pairs2 = parseString(s2);

        // execute 'finder' methods for different aspects of data

        // at first i look for commons (e.g. classes loaded from same resources)
        List<ComparePair> pairs_common = findCommon(pairs1, pairs2);
        // now i look for classes loaded first application not by second
        List<ComparePair> in_1_not_in_2 = findLost(pairs1, pairs2);
        // reversed situation
        List<ComparePair> in_2_not_in_1 = findLost(pairs2, pairs1);
        // looks for classes loaded from different resources
        List<CompareTroika> different_files_for_one_clazz = findDifferentFiles(pairs1, pairs2);

        return new CompareContainer(pairs1, pairs2, pairs_common, in_1_not_in_2, in_2_not_in_1, different_files_for_one_clazz);
    }

    /**
     * looks for classes loaded in first & second times from different locations
     * @param pairs1 list of pairs for first application run
     * @param pairs2 list of pairs for second application run
     * @return
     */
    private static List<CompareTroika> findDifferentFiles(List<ComparePair> pairs1, List<ComparePair> pairs2) {
        List<CompareTroika> rezz = new ArrayList<CompareTroika>();
        for (ComparePair comparePair : pairs1) {
            String clazz = comparePair.clazz;
            String file = comparePair.file;
            for (ComparePair pair : pairs2) {
                if (clazz.equals(pair.clazz) && (!file.equals(pair.file))) {
                    rezz.add(new CompareTroika(pair.clazz, file, pair.file));
                }
            }
        }
        return rezz;
    }

    /** looks for pairs exists in first but not in second set
     * @param maxSet biggest set (where to find)
     * @param minSet minimum set (what to find)
     * @return
     */
    private static List<ComparePair> findLost(List<ComparePair> maxSet, List<ComparePair> minSet) {
        List<ComparePair> rezz = new ArrayList<ComparePair>();
        for (ComparePair comparePair : maxSet) {
            if (!minSet.contains(comparePair)) {
                rezz.add(comparePair);
            }
        }
        return rezz;
    }

    /**
     * looks for common pairs
     * @param pairs1 list of pairs for first application run
     * @param pairs2 list of pairs for second application run
     * @return
     */
    private static List<ComparePair> findCommon(List<ComparePair> pairs1, List<ComparePair> pairs2) {
        List<ComparePair> rezz = new ArrayList<ComparePair>();
        for (ComparePair comparePair : pairs1) {
            if (pairs2.contains(comparePair)) {
                rezz.add(comparePair);
            }
        }
        return rezz;
    }


    /**
     * parses string for application log to list of pairs (clazz & file name)
     * @param str file content
     * @return
     */
    private static List<ComparePair> parseString(String str) {
        List<ComparePair> rezz = new ArrayList<ComparePair>();
        String[] ss = str.split("\n");
        Pattern p = Pattern.compile("\\[Loaded (.+) from (.+)\\]");
        for (String s : ss) {
            // all strings in log look like
            //[Loaded java.lang.Object from D:\Program_Files_2\jdk1.5.0\jre\lib\rt.jar]
            Matcher matcher = p.matcher(s);
            if (!matcher.matches()) continue;
            String clazz = matcher.group(1);
            String file = matcher.group(2);

            rezz.add(new ComparePair(clazz, file));
        }

        // sort load info by clazz name
        Collections.sort(rezz, new Comparator<ComparePair>() {
            public int compare(ComparePair o1, ComparePair o2) {
                return o1.clazz.compareTo(o2.clazz);
            }
        });
        return rezz;
    }
}
