import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ParcelSorting {
    
    public static void main(String[] args) {
        try {
            ArrayList<ArrayList<String>> parcels = readFileToArray("Parcels.txt", "\\|");

            // add column with Google maps link
            // modify header row appropriately
            parcels.get(0).add("GOOGLE_MAPS_LINK");
            int addrLoc = parcels.get(0).indexOf("ADDRESS");
            for (int i = 1; i < parcels.size(); i++) {
                ArrayList<String> line = parcels.get(i);
                String url = "https://www.google.com/maps/place/";
                url += line.get(addrLoc).replace(" ", "+");
                url += ",+Mazama,+WA";
                line.add(url);
            }
            
            System.out.println("First sort: street name & number");
            // leave the header row in place when sorting
            parcels.subList(1, parcels.size()).sort((ArrayList<String> o1, ArrayList<String> o2) -> {
                String street1 = o1.get(addrLoc).substring(o1.get(addrLoc).indexOf(" ") + 1);
                // adjust for subdivided lots (# A, # B, etc.)
                // note that this would incorrectly sort a building number # on a street E 5th Ave (East 5th Ave),
                //   for example.  We don't have a way to tell this case apart from building number # E on 5th Ave.
                //   It would similarly incorrectly sort a building number # on a street with a single-character name,
                //   such as F St.
                String sub1 = "";
                if (street1.indexOf(" ") == 1) {
                    sub1 = street1.substring(0, 1);
                    street1 = street1.substring(2);
                }
                String street2 = o2.get(addrLoc).substring(o2.get(addrLoc).indexOf(" ") + 1);
                String sub2 = "";
                if (street2.indexOf(" ") == 1) {
                    sub2 = street2.substring(0, 1);
                    street2 = street2.substring(2);
                }
                int cmp = street1.compareTo(street2);
                if (cmp == 0) {
                    Integer addr1 = Integer.decode(o1.get(addrLoc).substring(0, o1.get(addrLoc).indexOf(" ")));
                    Integer addr2 = Integer.decode(o2.get(addrLoc).substring(0, o2.get(addrLoc).indexOf(" ")));
                    cmp = addr1.compareTo(addr2);
                    if (cmp == 0) {
                        cmp = sub1.compareTo(sub2);
                    }
                }
                return cmp;
            });

            // field lengths here were manually selected for the given table, but could be dynamically determined for
            //   broader application; this would be most efficient to determine while reading in the data initially, so
            //   that you wouldn't have to traverse the table again to determine the longest field in each column
            int[] fieldLengths = {10, 30, 45, 12, 10, 10, 65, 80};
            printArray(parcels, fieldLengths);
            
            System.out.println();
            System.out.println("Second sort: first name");
            int nameLoc = parcels.get(0).indexOf("OWNER");
            // leave the header row in place when sorting
            parcels.subList(1, parcels.size()).sort((ArrayList<String> o1, ArrayList<String> o2) -> {
                String name1 = o1.get(nameLoc);
                String name2 = o2.get(nameLoc);
                // adjust to sort by first name if one exists
                int comma1 = name1.indexOf(",");
                int comma2 = name2.indexOf(",");
                if (comma1 > -1) {
                    name1 = name1.substring(comma1 + 2);
                }
                if (comma2 > -1) {
                    name2 = name2.substring(comma2 + 2);
                }
                return name1.compareTo(name2);
            });
            printArray(parcels, fieldLengths);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads in a delimited text file and builds a nested ArrayList of the contents.
     * @param filename The name of the file to be read
     * @param delim A regular expression that will recognize the file's delimiter
     * @return An ArrayList of the file's contents
     * @throws FileNotFoundException if a bad filename is given
     */
    public static ArrayList<ArrayList<String>> readFileToArray(String filename, String delim)
                    throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(new File(".\\" + filename)));
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        String s;
        try {
            while ((s = br.readLine()) != null) {
                data.add(new ArrayList<String>(Arrays.asList(s.split(delim))));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Prints a nested ArrayList of strings to the console.  Assumes that fieldLengths has at least
     * as many entries as each row in the table.
     * @param arr The array to be printed
     * @param fieldLengths The desired lengths of each field in the array
     */
    public static void printArray(ArrayList<ArrayList<String>> arr, int[] fieldLengths) {
        for (ArrayList<String> fields : arr) {
            for (int i = 0; i < fields.size(); i++) {
                System.out.printf("%-" + fieldLengths[i] + "s", fields.get(i));
                if (i < fields.size() - 1) {
                    System.out.print("|");
                }
            }
            System.out.print("\n");
        }
    }
}
