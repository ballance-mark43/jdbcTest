import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scorer
{
    private MyDatabase myDatabase;
    private Map<String, Integer> scoreTable;

    public Scorer()
    {
        myDatabase = new MyDatabase();
        scoreTable = populateScoreTable();
    }

    public void run()
    {
        boolean exit = false;
        while (!exit)
        {
            // request command from user
            printUserMenu();
            Scanner scanner = new Scanner(System.in);

            String userCommand = scanner.next();
            System.out.println(userCommand);

            // scoring table
            String userFileName = "";

            // retrieve all scores for a given file
            switch (userCommand.toLowerCase())
            {
                case "exit":
                {
                    exit = true;
                    break;
                }
                case "getscores":
                {
                    System.out.println("Enter Filename: ");
                    userFileName = scanner.next();

                    for (int i = 0; i < myDatabase.getAllEntries(userFileName).size()-1; i++)
                    {
                        System.out.println("\nFile: " + myDatabase.getAllEntries(userFileName).get(i).getFileName());
                        System.out.println("Score: " + myDatabase.getAllEntries(userFileName).get(i).getScore());
                        System.out.println("Date Scored: " +
                                myDatabase.getAllEntries(userFileName).get(i).getDateScored());
                    }
                    break;
                }
                case "maxscore":
                {
                    // retrieve highest score for a given file
                    System.out.println("Enter Filename: ");
                    userFileName = scanner.next();

                    System.out.println("\nFile: " + myDatabase.getMaxScore(userFileName).get(0).getFileName());
                    System.out.println("Score: " + myDatabase.getMaxScore(userFileName).get(0).getScore());
                    System.out.println("Date Scored: " + myDatabase.getMaxScore(userFileName).get(0).getDateScored());
                    break;
                }
                case "minscore":
                {
                    // retrieve lowest score for a given file
                    System.out.println("Enter Filename: ");
                    userFileName = scanner.next();

                    System.out.println("\nFile: " + myDatabase.getMinScore(userFileName).get(0).getFileName());
                    System.out.println("Score: " + myDatabase.getMinScore(userFileName).get(0).getScore());
                    System.out.println("Date Scored: " + myDatabase.getMinScore(userFileName).get(0).getDateScored());
                    break;
                }
                case "avgscore":
                {
                    System.out.println("Enter Filename: ");
                    userFileName = scanner.next();

                    System.out.println("\nFile: " + myDatabase.getAvgScore(userFileName).get(0).getFileName());
                    System.out.println("Score: " + myDatabase.getAvgScore(userFileName).get(0).getScore());
                    System.out.println("Date Scored: " + myDatabase.getAvgScore(userFileName).get(0).getDateScored());
                    break;
                }
                case "score":
                {
                    // actually scores the HTML file
                    try
                    {
                        int score = 0;

                        // request command from user
                        System.out.println("Enter Filename: ");
                        userFileName = scanner.next();

                        score = getScoreByFile(userFileName);
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("File Not Found. File Number: " + userFileName);
                    }
                    break;
                }
                case "scoredirectory":
                {
                    // actually scores the HTML file
                    try
                    {
                        int score = 0;

                        // request command from user
                        System.out.println("Enter Directory: ");
                        String userDirectoryName = scanner.next();
                        File folder = new File("C:\\Users\\Hayden Key\\jdbcTest\\" + userDirectoryName);
                        File[] filesToProcess = folder.listFiles();

                        for (int i = 0; i < filesToProcess.length-1; i++)
                        {
                            score = getScoreByFile(filesToProcess[i].getAbsolutePath());
                        }
                    }
                    catch(FileNotFoundException e)
                    {
                        System.out.println("File Not Found. File Number: " + userFileName);
                    }
                    break;
                }
                case "dates":
                {
                    // these are here to check for valid formatting
                    String fromDate = "";
                    String toDate = "";
                    SimpleDateFormat dateFormatChecker = new SimpleDateFormat("yyyy-mm-dd");

                    System.out.println("Select all entries between two dates. (yyyy-mm-dd)");

                    try
                    {
                        // user enters from date
                        System.out.println("From: ");
                        fromDate = scanner.next();

                        dateFormatChecker.parse(fromDate);

                        // user enters to date
                        System.out.println("To: ");
                        toDate = scanner.next();

                        dateFormatChecker.parse(toDate);

                        // talks to database
                        System.out.println(myDatabase.getEntriesBetweenDates(fromDate, toDate));
                    }
                    catch (ParseException e)
                    {
                        System.out.println("Date formatted incorrectly.");
                    }
                    break;
                }
            }
        }
        System.out.println("done.");
    }

    private int getScoreByFile(String userFileName) throws FileNotFoundException
    {
        int score = 0;
        Scanner fileScanner = new Scanner(new File(userFileName));
        String fileContents = fileScanner.useDelimiter("\\A").next().toLowerCase();
        fileScanner.close(); // Put this call in a finally block

        // Scoring
        Iterator<String> scoreKey = scoreTable.keySet().iterator();

        while (scoreKey.hasNext()) {
            String tag = scoreKey.next();

            Pattern pattern = Pattern.compile("<" + tag);
            Matcher matcher = pattern.matcher(fileContents);

            while (matcher.find()) {
                score += scoreTable.get(tag);
            }
        }
        System.out.printf("Total score for %s: %d\n%n", userFileName, score);

        // stores entry into SQL database
        myDatabase.inputEntry(userFileName, score);
        System.out.println("For " + userFileName + " score was " + score);
        return score;
    }

    private static Map<String, Integer> populateScoreTable()
    {
        Map<String, Integer> scoreTable = new HashMap<String, Integer>();
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
        return scoreTable;
    }

    private static void printUserMenu()
    {
        System.out.println("Available Actions:");
        System.out.println("\tscore - scores a given file");
        System.out.println("\tscoreDirectory - scores a given directory of files");
        System.out.println("\tgetScores - retrieve all scores for a given file");
        System.out.println("\tmaxScores - retrieve highest score for a given file");
        System.out.println("\tminScores - retrieve lowest score for a given file");
        System.out.println("\tgetScores - retrieve the average score for each unique file name");
        System.out.println("\tdates - retrieve all scores run in the system for a custom date range");
        System.out.println("\tquit - exit the program");
        System.out.println("\nChose Action: ");
    }
}
