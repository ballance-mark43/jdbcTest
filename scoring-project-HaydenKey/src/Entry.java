public class Entry {
    private int id;
    private int score;
    private String fileName;
    private String dateScored;

    // dateScored getter and setter
    public String getDateScored() {
        return dateScored;
    }

    public void setDateScored(String dateScored) {
        this.dateScored = dateScored;
    }

    // dateScored getter and setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // getter and setter for fileName
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    // getter and setter for score
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
