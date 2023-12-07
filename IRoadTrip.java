import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.io.IOException;
// This is my take on the project, I had a diffcult time on this and tried doing it, I  wasn't too successful. 
//I am submitting what I tried but kept getting errors
// Didn't get enough time to finish this up
public class IRoadTrip {
  // Data structures to store information about countries, borders, and distances
    private HashMap<String, Map<String, Integer>> bordersDatafile;
    private HashMap<String, Integer> capDistdatafile;
    private HashMap<String, String> stateNameDatafile;
// Constructor to initialize the data structures using input files
    public IRoadTrip(String[] args) {
        
        bordersDatafile = readBordersFiletxt(args[0]);
        capDistdatafile = readCapDistFile(args[1]);
        stateNameDatafile = readStateNameFile(args[2]);
       
        //testing to see if the files were being read 
        //System.out.println(capDistdatafile.keySet());
        //System.out.println("Reading borders file from: " + bordersDatafile);
       //System.out.println("capDistdatafile: " + capDistdatafile);
      //System.out.println("stateNameDatafile: " + stateNameDatafile);

    }
    // Reads and parses the borders file to populate the bordersDatafile
    private HashMap<String, Map<String, Integer>> readBordersFiletxt(String bordersFile) {
        HashMap<String, Map<String, Integer>> bordersData = new HashMap<>();
        System.out.println("Reading borders file from: " + bordersFile);
        try (BufferedReader br = new BufferedReader(new FileReader(bordersFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Parse and populate bordersData
                String[] parts = line.split("=");
                String country1 = parts[0].trim();
    
                // Check if the country has alias names
                String[] countriesAndDistances = parts[1].split(";");
                for (String countryAndDistance : countriesAndDistances) {
                    String[] countryDistancePair = countryAndDistance.trim().split(" km")[0].split(" ");  // Split by one or more whitespaces
                    if (countryDistancePair.length == 2) {
                        String country2 = countryDistancePair[0].trim();
                        int distance = Integer.parseInt(countryDistancePair[1].replaceAll(",", "").trim());
    
                    // This is the bordersData 
                        bordersData.computeIfAbsent(country1, k -> new HashMap<>()).put(country2, distance);
                        bordersData.computeIfAbsent(country2, k -> new HashMap<>()).put(country1, distance);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    
        return bordersData;
    }
    
    
    //Hashmap for reading the capDistFile
    private HashMap<String, Integer> readCapDistFile(String capDistFile) {
        HashMap<String, Integer> capDistData = new HashMap<>();
     // Reads and parses the capital distance file to populate the capDistdatafile
        try (BufferedReader br = new BufferedReader(new FileReader(capDistFile))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                // Parse and populate capDistData
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String countryA = parts[1].trim();
                    String countryB = parts[3].trim();
                    int distance = Integer.parseInt(parts[4].trim());
                    String key = countryA + "-" + countryB;
    
                    // Update capDistData
                    capDistData.put(key, distance);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    
        return capDistData;
    }
    
    //hashmap for reading the state_name.tsv
    private HashMap<String, String> readStateNameFile(String stateNameFile) {
        HashMap<String, String> stateNameData = new HashMap<>();
    
        try (BufferedReader br = new BufferedReader(new FileReader(stateNameFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Parse and populate stateNameData
                String[] parts = line.split("\t");
                if (parts.length == 5) {
                    String countryID = parts[1].trim();
                    String countryName = parts[2].trim();
    
                    // Update stateNameData
                    stateNameData.put(countryName, countryID);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return stateNameData;
    }
    public int getDistance(String country1, String country2) {
        // Check if both countries exist in the data
         if (!bordersDatafile.containsKey(country1) || !bordersDatafile.containsKey(country2)) {
            System.out.println("One or both countries are not being able to be found.");
        }

        // Check if the countries share a land border
        if (!bordersDatafile.get(country1).containsKey(country2)) {
            System.out.println("The two don't share a land border");
        }

        // Get the distance between capitals of country1 and country2
        String key = country1 + "-" + country2;
             if (!capDistdatafile.containsKey(key)) {
            System.out.println("The data is not being able to be processed");
            }

        return capDistdatafile.get(key);
    }
    //My attempt at using the dijkrstra algortihm
    public List<String> findPath(String country1, String country2) {
        // Check if both countries exist in the data
        if (!bordersDatafile.containsKey(country1) || !bordersDatafile.containsKey(country2)) {
            System.out.println("One or both countries do not exist.");
            return Collections.emptyList();
        }
    
        
        Set<String> visited = new HashSet<>();
        List<String> path = new ArrayList<>();
        dfs(country1, country2, visited, path);
    
        return path;
    }
    //trying to mark the countries as mapped 
    private boolean dfs(String currentCountry, String targetCountry, Set<String> visited, List<String> path) {
        
        visited.add(currentCountry);
        path.add(currentCountry);
    
        
        if (currentCountry.equals(targetCountry)) {
            return true;
        }
    
        // Exploring neighbors 
        if (bordersDatafile.containsKey(currentCountry)) {
            for (Map.Entry<String, Integer> neighbor : bordersDatafile.get(currentCountry).entrySet()) {
                String nextCountry = neighbor.getKey();
                if (!visited.contains(nextCountry)) {
                    // trying to recursively explore the next country
                    if (dfs(nextCountry, targetCountry, visited, path)) {
                        return true;
                    }
                }
            }
        }
    
        //trying backtrack method here 
        visited.remove(currentCountry);
        path.remove(path.size() - 1);
    
        return false;
    }
        // Print the path between two countries
    private void printPath(String country1, String country2, List<String> path) {
        // Print the path from country1 to country2
        for (String country : path) {
            System.out.print(country);
            if (!country.equals(country2)) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }
    
    // Accept user input to find paths between countries
         public void acceptUserInput() {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter the name of the first country (type EXIT to quit): ");
                String country1 = scanner.nextLine().trim();
                if (country1.equalsIgnoreCase("EXIT")) {
                    break;
                }
    
                System.out.print("Enter the name of the second country (type EXIT to quit): ");
                String country2 = scanner.nextLine().trim();
    
                if (country2.equalsIgnoreCase("EXIT")) {
                    break;
                }
    
                List<String> path = findPath(country1, country2);
    
                if (path.isEmpty()) {
                    System.out.println("No valid path found between " + country1 + " and " + country2);
                } else {
                    System.out.println("Route from " + country1 + " to " + country2 + ":");
                    printPath(country1, country2, path);
                }
            }
    
            scanner.close();
        }
    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);
        a3.acceptUserInput();
        
    }

}