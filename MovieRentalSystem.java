import java.io.*;
import java.util.*;

//─── Movie Entity ──────
class Movie {
    private String id, title, genre, director;
    private int year;
    private boolean available;

    public Movie(String id, String title, String genre, String director, int year, boolean available) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.director = director;
        this.year = year;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getDirector() {
        return director;
    }

    public int getYear() {
        return year;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean v) {
        available = v;
    }

    public String toCsv() {
        return id + "," + title + "," + genre + "," + director + "," + year + "," + available;
    }

    public String toString() {
        return id + " | " + title + " | " + genre + " | " + director + " | " + year + " | "
                + (available ? "Available" : "Rented");
    }
}

// ─── Rental Transaction Entity ─────
class Rental {
    private String txnId, user, movieId, movieTitle, rentDate, returnDate, status;

    public Rental(String txnId, String user, String movieId, String movieTitle, String rentDate) {
        this.txnId = txnId;
        this.user = user;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.rentDate = rentDate;
        this.returnDate = "---";
        this.status = "RENTED";
    }

    public Rental(String txnId, String user, String movieId, String movieTitle,
            String rentDate, String returnDate, String status) {
        this.txnId = txnId;
        this.user = user;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public String getTxnId() {
        return txnId;
    }

    public String getUser() {
        return user;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getRentDate() {
        return rentDate;
    }

    public String getStatus() {
        return status;
    }

    public String setReturnDate(String d) {
        return returnDate = d;
    }

    public void setStatus(String s) {
        status = s;
    }

    public String toCsv() {
        return txnId + "," + user + "," + movieId + "," + movieTitle + "," + rentDate + "," + returnDate + "," + status;
    }

    public String toString() {
        return txnId + " | " + user + " | " + movieId + " | "
                + movieTitle + " | " + rentDate + " | "
                + returnDate + " | " + status;
    }
}

// ─── Main Application ──────
public class MovieRentalSystem {

    static final String MOVIES_FILE = "movies.csv";
    static final String RENTALS_FILE = "rentals.csv";

    static HashMap<String, Movie> movies = new HashMap<>();
    static ArrayList<Rental> rentals = new ArrayList<>();
    static HashSet<String> rented = new HashSet<>();

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        initDataFiles();
        loadData();

        System.out.println("\n  ----------- MOVIE RENTAL SYSTEM -----------\n");

        while (true) {
            System.out.println("\n1. View Movies   2. Search   3. Rent   4. Return   5. History   0. Exit");
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    viewMovies();
                    break;
                case "2":
                    searchMovies();
                    break;
                case "3":
                    rentMovie();
                    break;
                case "4":
                    returnMovie();
                    break;
                case "5":
                    viewHistory();
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("[!] Invalid choice.");
            }
        }
    }

    static void viewMovies() {
        System.out.println("\n--- ALL MOVIES ---");
        int availableCount = 0;

        for (Movie m : movies.values()) {
            System.out.println(m);
            if (m.isAvailable())
                availableCount++;
        }

        System.out.println("-------------------");
        System.out.println("Total Movies: " + movies.size());
        System.out.println("Available Movies: " + availableCount);
    }

    static void searchMovies() {
        System.out.print("Search by (1) Title (2) Genre (3) Director: ");
        String type = sc.nextLine();

        System.out.print("Enter keyword: ");
        String key = sc.nextLine().toLowerCase();

        boolean found = false;
        System.out.println("\n--- SEARCH RESULTS ---");

        for (Movie m : movies.values()) {
            if (type.equals("1") && m.getTitle().toLowerCase().contains(key)) {
                System.out.println(m);
                found = true;
            } else if (type.equals("2") && m.getGenre().toLowerCase().contains(key)) {
                System.out.println(m);
                found = true;
            } else if (type.equals("3") && m.getDirector().toLowerCase().contains(key)) {
                System.out.println(m);
                found = true;
            }
        }

        if (!found)
            System.out.println("No results found");
    }

    static void rentMovie() {
        System.out.println("\n--- AVAILABLE MOVIES ---");

        for (Movie m : movies.values()) {
            if (m.isAvailable()) {
                System.out.println(m.getId() + " | " + m.getTitle());
            }
        }

        System.out.print("Enter your name: ");
        String user = sc.nextLine();

        System.out.print("Enter movie ID: ");
        String id = sc.nextLine();

        if (!movies.containsKey(id)) {
            System.out.println("Movie not found");
            return;
        }

        Movie m = movies.get(id);

        if (!m.isAvailable()) {
            System.out.println("Movie already rented");
            return;
        }

        String txnId = "T" + System.currentTimeMillis();
        Rental r = new Rental(txnId, user, id, m.getTitle(), today());

        m.setAvailable(false);
        rentals.add(r);

        saveData();
        System.out.println("Movie rented successfully");
    }

    static void returnMovie() {
        System.out.print("Enter your name: ");
        String user = sc.nextLine();

        System.out.println("\nYour rented movies:");
        boolean found = false;

        for (Rental r : rentals) {
            if (r.getUser().equalsIgnoreCase(user) && r.getStatus().equals("RENTED")) {
                System.out
                        .println("TxnID: " + r.getTxnId() + " | " + r.getMovieTitle() + " | Date: " + r.getRentDate());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No rented movies found");
            return;
        }

        System.out.print("Enter Transaction ID: ");
        String id = sc.nextLine();

        for (Rental r : rentals) {
            if (r.getTxnId().equals(id) && r.getStatus().equals("RENTED")) {
                r.setStatus("RETURNED");
                r.setReturnDate(today());

                Movie m = movies.get(r.getMovieId());
                if (m != null)
                    m.setAvailable(true);

                saveData();
                System.out.println("Movie returned successfully");
                return;
            }
        }

        System.out.println("Transaction not found");
    }

    static void viewHistory() {
        System.out.print("Enter name to filter (or press Enter for all): ");
        String name = sc.nextLine();

        boolean found = false;
        System.out.println("\n--- RENTAL HISTORY ---");

        for (Rental r : rentals) {
            if (name.equals("") || r.getUser().toLowerCase().contains(name.toLowerCase())) {
                System.out.println(r);
                found = true;
            }
        }

        if (!found)
            System.out.println("No records found");
    }

    // ── File I/O ──────────────

    static void initDataFiles() {
        File mf = new File(MOVIES_FILE);
        if (!mf.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(mf))) {
                bw.write("ID,Title,Genre,Director,Year,Available\n");
                bw.write("M001,The Dark Knight,Action,Christopher Nolan,2008,true\n");
                bw.write("M002,Inception,Sci-Fi,Christopher Nolan,2010,true\n");
                bw.write("M003,The Godfather,Crime,Francis Ford Coppola,1972,true\n");
                bw.write("M004,Interstellar,Sci-Fi,Christopher Nolan,2014,true\n");
                bw.write("M005,Pulp Fiction,Crime,Quentin Tarantino,1994,true\n");
                bw.write("M006,The Shawshank Redemption,Drama,Frank Darabont,1994,true\n");
                bw.write("M007,Forrest Gump,Drama,Robert Zemeckis,1994,true\n");
                bw.write("M008,The Matrix,Sci-Fi,Lana Wachowski,1999,true\n");
                bw.write("M009,Goodfellas,Crime,Martin Scorsese,1990,true\n");
                bw.write("M010,Avengers Endgame,Action,Anthony Russo,2019,true\n");
                System.out.println("✔ movies.csv created with sample data.");
            } catch (IOException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }

        File rf = new File(RENTALS_FILE);
        if (!rf.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(rf))) {
                bw.write("TxnID,User,MovieID,MovieTitle,RentDate,ReturnDate,Status\n");
            } catch (IOException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    static void loadData() {
        // Load movies
        try (BufferedReader br = new BufferedReader(new FileReader(MOVIES_FILE))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] p = line.split(",");
                if (p.length == 6)
                    movies.put(p[0], new Movie(p[0], p[1], p[2], p[3],
                            Integer.parseInt(p[4]), Boolean.parseBoolean(p[5])));
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Loading movies: " + e.getMessage());
        }

        // Load rentals
        try (BufferedReader br = new BufferedReader(new FileReader(RENTALS_FILE))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] p = line.split(",");
                if (p.length == 7) {
                    Rental r = new Rental(p[0], p[1], p[2], p[3], p[4], p[5], p[6]);
                    rentals.add(r);
                    if (p[6].equals("RENTED"))
                        rented.add(p[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Loading rentals: " + e.getMessage());
        }

        System.out.println("✔ Loaded " + movies.size() + " movies, " + rentals.size() + " rentals.");
    }

    static void saveData() {
        // Save movies
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MOVIES_FILE))) {
            bw.write("ID,Title,Genre,Director,Year,Available\n");
            for (Movie m : movies.values()) {
                bw.write(m.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Saving movies: " + e.getMessage());
        }

        // Save rentals
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RENTALS_FILE))) {
            bw.write("TxnID,User,MovieID,MovieTitle,RentDate,ReturnDate,Status\n");
            for (Rental r : rentals) {
                bw.write(r.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Saving rentals: " + e.getMessage());
        }
    }

    // Returns today's date as dd-MM-yyyy
    static String today() {
        Calendar c = Calendar.getInstance();

        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);

        return day + "-" + month + "-" + year;
    }
}
