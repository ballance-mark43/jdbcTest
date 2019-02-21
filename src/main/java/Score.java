import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Score {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
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

        // request command from user
        System.out.println("Chose Action: ");
        Scanner scanner = new Scanner(System.in);

        String userCommand = scanner.next();
        System.out.println(userCommand);

        
        // retrieve lowest score for a given file
        if (args.length > 0 && args[argsCounter] != null && args[argsCounter].equals("-getScores")) {
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
        if (args.length > 0 && args[argsCounter] != null && args[argsCounter].equals("-maxScore")) {
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
        if (args.length > 0 && args[argsCounter] != null && args[argsCounter].equals("-minScore")) {
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
        if (args.length > 0 && args[argsCounter] != null && args[argsCounter].equals("-avgScore")) {
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
        if (userCommand.equals("score")) {
            try {
                // request command from user
                System.out.println("Enter Filename: ");

                String userFileName = scanner.next();

                //loads entire file into ArrayList of Strings
                FileReader fr = new FileReader(userFileName);
                BufferedReader br = new BufferedReader(fr);
                int score = 0;

                Scanner fileScanner = new Scanner( new File(userFileName) );
                String fileContents = fileScanner.useDelimiter("\\A").next();
                fileScanner.close(); // Put this call in a finally block
                System.out.println(fileContents);

                // Scoring
                Iterator<String> scoreKey;

                String tag = "html";

                Pattern pattern = Pattern.compile("<" + tag);
                Matcher matcher = pattern.matcher(fileContents);
                int count = 0;
                while (matcher.find()) count++;
                System.out.println(count);

                System.out.printf("Total html tags for %s: %d\n%n", userFileName, count);


                // stores entry into SQL database
                myDatabase.inputEntry(userFileName, count);

            } catch (FileNotFoundException e) {
                System.out.println("File Not Found. File Number: " + (argsCounter + 1));
            } catch (IOException e) {
                System.out.println("IOException.  File Number: " + (argsCounter + 1));
            }
        }

        // TODO fix dates
        // retrieve all scores run in the system for a custom date range
        if (args.length > 0 && args[argsCounter] != null && args[0].equals("-dates")) {
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

        System.out.println("done.");
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
