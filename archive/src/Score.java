import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Score {
    public static void main(String[] args) {
        Map<String, Integer> scoreTable = new HashMap<String, Integer>();
        MyDatabase myDatabase = new MyDatabase();
        int argsCounter = 0;

        scoreTable.put("div", 3);
        scoreTable.put("p", 1);
        scoreTable.put("h1", 3);
        scoreTable.put("h2", 2);
        scoreTable.put("html", 5);
        scoreTable.put("body", 5);
        scoreTable.put("header", 10);
        scoreTable.put("footer", 10);
        scoreTable.put("font", -1);
        scoreTable.put("center", -2);
        scoreTable.put("big", -2);
        scoreTable.put("strike", -1);
        scoreTable.put("tt", -2);
        scoreTable.put("frameset", -5);
        scoreTable.put("frame", -5);

        // retrieve lowest score for a given file
        if (args[argsCounter].equals("-getScores")) {
            argsCounter++; // move to next arg

            for (int i = argsCounter; i < args.length; i++) {
                if (args[i].startsWith("-")) {
                    i++;
                } else {
                    myDatabase.getAllEntries(args[i]);
                }
            }
        }

        // retrieve highest score for a given file
        if (args[argsCounter].equals("-maxScore")) {
            argsCounter++; // move to next arg

            for (int i = argsCounter; i < args.length; i++) {
                if (args[i].startsWith("-")) {
                    i++;
                } else {
                    myDatabase.getMaxScore(args[i]);
                }
            }
        }

        // retrieve lowest score for a given file
        if (args[argsCounter].equals("-minScore")) {
            argsCounter++; // move to next arg

            for (int i = argsCounter; i < args.length; i++) {
                if (args[i].startsWith("-")) {
                    i++;
                } else {
                    myDatabase.getMinScore(args[i]);
                }
            }
        }

        // retrieve the average score for each unique file name
        if (args[argsCounter].equals("-avgScore")) {
            argsCounter++; // move to next arg

            for (int i = argsCounter; i < args.length; i++) {
                if (args[i].startsWith("-")) {
                    i++;
                } else {
                    myDatabase.getAvgScore(args[i]);
                }
            }
        }

        // actually scores the HTML file
        if (args[argsCounter].equals("-score")) {
            argsCounter++;

            for (;argsCounter < args.length; argsCounter++) {
                try {
                    //loads entire file into ArrayList of Strings
                    FileReader fr = new FileReader(args[argsCounter]);
                    BufferedReader br = new BufferedReader(fr);
                    int score = 0;

                    String str;
                    ArrayList<String> file = new ArrayList<>();
                    while ((str = br.readLine()) != null) {
                        file.add(str.toLowerCase());
                    }


                    // Scoring
                    Iterator<String> scoreKey;

                    for (int j = 0; j < (file.size()); j++) {
                        // refreshes iterator
                        scoreKey = scoreTable.keySet().iterator();

                        // checks each line for specific element
                        while (scoreKey.hasNext()) {
                            String elem = scoreKey.next();

                            int nubOccurrences = findOccurrences(file.get(j), "/" + elem);

                            score += (scoreTable.get(elem) * nubOccurrences);
                        }
                    }

                    System.out.println("Total for " + args[argsCounter] + ": " + score + "\n");


                    // stores entry into SQL database
                    myDatabase.inputEntry(score, args[argsCounter]);

                } catch (FileNotFoundException e) {
                    System.out.println("File Not Found. File Number: " + (argsCounter + 1));
                } catch (IOException e) {
                    System.out.println("IOException.  File Number: " + (argsCounter + 1));
                }
            }
        }

        // TODO fix dates
        // retrieve all scores run in the system for a custom date range
        if (args[0].equals("-dates")) {
            //these are here to check for valid formatting
            SimpleDateFormat sdf0 = new SimpleDateFormat("mm-dd-yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("mm-dd-yyyy");
            boolean formattingConfirmation0 = false;
            boolean formattingConfirmation1 = false;

            try {
                sdf0.parse(args[1]);
                formattingConfirmation0 = true;
            } catch(ParseException e) {
                System.out.println("Invalid Date Formatting: " + args[1]);
            }

            try {
                sdf1.parse(args[2]);
                formattingConfirmation1 = true;
            } catch(ParseException e) {
                System.out.println("Invalid Date Formatting: " + args[2]);
            }

            if (formattingConfirmation0 && formattingConfirmation1) {
                myDatabase.getEntriesBetweenDates(args[1], args[2]);
            }
        }
    }

    // A method to find the frequency of a String in another String
    private static int findOccurrences(String source, String target) {
        int freq = 0;

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(source);
        while (m.find()) {
            freq++;
        }

        return freq;
    }
}
