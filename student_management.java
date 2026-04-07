import java.io.*;
import java.util.*;

// ─── Movie Entity ─────────────────────────────────────────────────────────────
class Movie {
    private String id, title, genre, director;
    private int year;
    private boolean available;

    public Movie(String id, String title, String genre, String director, int year, boolean available) {
        this.id = id; this.title = title; this.genre = genre;
        this.director = director; this.year = year; this.available = available;
    }

    public String getId()         { return id; }
    public String getTitle()      { return title; }
    public String getGenre()      { return genre; }
    public String getDirector()   { return director; }
    public int    getYear()       { return year; }
    public boolean isAvailable()  { return available; }
    public void setAvailable(boolean v) { available = v; }

    public String toCsv() {
        return id + "," + title + "," + genre + "," + director + "," + year + "," + available;
    }

    public String toString() {
        return String.format("| %-5s | %-30s | %-10s | %-20s | %d | %s |",
            id, title, genre, director, year, available ? "Available" : "Rented   ");
    }
}

// ─── Rental Transaction Entity ────────────────────────────────────────────────
class Rental {
    private String txnId, user, movieId, movieTitle, rentDate, returnDate, status;

    public Rental(String txnId, String user, String movieId, String movieTitle, String rentDate) {
        this.txnId = txnId; this.user = user; this.movieId = movieId;
        this.movieTitle = movieTitle; this.rentDate = rentDate;
        this.returnDate = "---"; this.status = "RENTED";
    }

    // Full constructor for loading from file
    public Rental(String txnId, String user, String movieId, String movieTitle,
                  String rentDate, String returnDate, String status) {
        this.txnId = txnId; this.user = user; this.movieId = movieId;
        this.movieTitle = movieTitle; this.rentDate = rentDate;
        this.returnDate = returnDate; this.status = status;
    }

    public String getTxnId()      { return txnId; }
    public String getUser()       { return user; }
    public String getMovieId()    { return movieId; }
    public String getMovieTitle() { return movieTitle; }
    public String getRentDate()   { return rentDate; }
    public String getStatus()     { return status; }
    public void setReturnDate(String d) { returnDate = d; }
    public void setStatus(String s)     { status = s; }

    public String toCsv() {
        return txnId + "," + user + "," + movieId + "," + movieTitle + "," + rentDate + "," + returnDate + "," + status;
    }

    public String toString() {
        return String.format("| %-8s | %-12s | %-5s | %-28s | %-12s | %-12s | %-8s |",
            txnId, user, movieId, movieTitle, rentDate, returnDate, status);
    }
}

// ─── Main Application ─────────────────────────────────────────────────────────
public class MovieRentalSystem {

    static final String MOVIES_FILE  = "movies.csv";
    static final String RENTALS_FILE = "rentals.csv";

    static HashMap<String, Movie>  movies  = new HashMap<>();   // key = movie ID
    static ArrayList<Rental>       rentals = new ArrayList<>(); // all transactions
    static HashSet<String>         rented  = new HashSet<>();   // currently rented IDs

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        initDataFiles();
        loadData();

        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║   🎬 MOVIE RENTAL SYSTEM 🎬   ║");
        System.out.println("╚══════════════════════════════╝");

        while (true) {
            System.out.println("\n1. View Movies   2. Search   3. Rent   4. Return   5. History   0. Exit");
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": viewMovies();   break;
                case "2": searchMovies(); break;
                case "3": rentMovie();    break;
                case "4": returnMovie();  break;
                case "5": viewHistory();  break;
                case "0":
                    System.out.println("Goodbye! 👋"); return;
                default:
                    System.out.println("[!] Invalid choice.");
            }
        }
    }

    // ── 1. View All Movies ────────────────────────────────────────────────────
    static void viewMovies() {
        System.out.println("\n--- ALL MOVIES ---------------------------------------------------------");
        System.out.printf("| %-5s | %-30s | %-10s | %-20s | %-4s | %-9s |%n",
            "ID", "Title", "Genre", "Director", "Year", "Status");
        System.out.println("------------------------------------------------------------------------");
        movies.values().stream()
            .sorted(Comparator.comparing(Movie::getId))
            .forEach(System.out::println);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Total: " + movies.size() + " | Available: " + movies.values().stream().filter(Movie::isAvailable).count());
    }

    // ── 2. Search Movies ──────────────────────────────────────────────────────
    static void searchMovies() {
        System.out.print("Search by (1)Title (2)Genre (3)Director: ");
        String type = sc.nextLine().trim();
        System.out.print("Enter keyword: ");
        String kw = sc.nextLine().trim().toLowerCase();

        System.out.println("\n--- SEARCH RESULTS -----------------------------------------------------");
        boolean found = false;
        for (Movie m : movies.values()) {
            boolean match = type.equals("1") && m.getTitle().toLowerCase().contains(kw)
                         || type.equals("2") && m.getGenre().toLowerCase().contains(kw)
                         || type.equals("3") && m.getDirector().toLowerCase().contains(kw);
            if (match) { System.out.println(m); found = true; }
        }
        if (!found) System.out.println("  No results found.");
        System.out.println("------------------------------------------------------------------------");
    }

    // ── 3. Rent a Movie ───────────────────────────────────────────────────────
    static void rentMovie() {
        System.out.println("\n--- AVAILABLE MOVIES ---------------------------------------------------");
        movies.values().stream()
            .filter(Movie::isAvailable)
            .sorted(Comparator.comparing(Movie::getId))
            .forEach(m -> System.out.printf("  %-5s | %s%n", m.getId(), m.getTitle()));
        System.out.println("------------------------------------------------------------------------");

        System.out.print("Your name: ");
        String user = sc.nextLine().trim();
        System.out.print("Movie ID : ");
        String id = sc.nextLine().trim().toUpperCase();

        if (!movies.containsKey(id)) {
            System.out.println("[!] Movie ID not found."); return;
        }
        Movie m = movies.get(id);
        if (!m.isAvailable()) {
            System.out.println("[!] '" + m.getTitle() + "' is already rented."); return;
        }

        String txnId = "T" + (System.currentTimeMillis() % 1_000_000);
        Rental r = new Rental(txnId, user, id, m.getTitle(), today());
        m.setAvailable(false);
        rented.add(id);
        rentals.add(r);
        saveData();

        System.out.println("✅ Rented! TxnID: " + txnId + " | Movie: " + m.getTitle() + " | Date: " + today());
    }

    // ── 4. Return a Movie ─────────────────────────────────────────────────────
    static void returnMovie() {
        System.out.print("Your name: ");
        String user = sc.nextLine().trim();

        System.out.println("\nYour active rentals:");
        boolean any = false;
        for (Rental r : rentals) {
            if (r.getUser().equalsIgnoreCase(user) && r.getStatus().equals("RENTED")) {
                System.out.printf("  TxnID: %-10s | %s (rented: %s)%n",
                    r.getTxnId(), r.getMovieTitle(), r.getRentDate());
                any = true;
            }
        }
        if (!any) { System.out.println("[!] No active rentals for " + user + "."); return; }

        System.out.print("Enter Transaction ID to return: ");
        String txnId = sc.nextLine().trim().toUpperCase();

        for (Rental r : rentals) {
            if (r.getTxnId().equals(txnId) && r.getStatus().equals("RENTED")) {
                r.setReturnDate(today());
                r.setStatus("RETURNED");
                Movie m = movies.get(r.getMovieId());
                if (m != null) m.setAvailable(true);
                rented.remove(r.getMovieId());
                saveData();
                System.out.println("✅ Returned! Movie: " + r.getMovieTitle() + " | Date: " + today());
                return;
            }
        }
        System.out.println("[!] Transaction not found or already returned.");
    }

    // ── 5. Rental History ─────────────────────────────────────────────────────
    static void viewHistory() {
        System.out.print("Filter by name (or press Enter for all): ");
        String filter = sc.nextLine().trim().toLowerCase();

        System.out.println("\n--- RENTAL HISTORY -----------------------------------------------------");
        System.out.printf("| %-8s | %-12s | %-5s | %-28s | %-12s | %-12s | %-8s |%n",
            "TxnID", "User", "ID", "Movie", "Rented", "Returned", "Status");
        System.out.println("------------------------------------------------------------------------");

        boolean found = false;
        for (Rental r : rentals) {
            if (filter.isEmpty() || r.getUser().toLowerCase().contains(filter)) {
                System.out.println(r); found = true;
            }
        }
        if (!found) System.out.println("  No records found.");
        System.out.println("------------------------------------------------------------------------");
    }

    // ── File I/O ──────────────────────────────────────────────────────────────

    // Creates data files with sample data if they don't exist yet
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
            } catch (IOException e) { System.out.println("[ERROR] " + e.getMessage()); }
        }

        File rf = new File(RENTALS_FILE);
        if (!rf.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(rf))) {
                bw.write("TxnID,User,MovieID,MovieTitle,RentDate,ReturnDate,Status\n");
            } catch (IOException e) { System.out.println("[ERROR] " + e.getMessage()); }
        }
    }

    static void loadData() {
        // Load movies
        try (BufferedReader br = new BufferedReader(new FileReader(MOVIES_FILE))) {
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                String[] p = line.split(",");
                if (p.length == 6)
                    movies.put(p[0], new Movie(p[0], p[1], p[2], p[3],
                        Integer.parseInt(p[4]), Boolean.parseBoolean(p[5])));
            }
        } catch (IOException e) { System.out.println("[ERROR] Loading movies: " + e.getMessage()); }

        // Load rentals
        try (BufferedReader br = new BufferedReader(new FileReader(RENTALS_FILE))) {
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                String[] p = line.split(",");
                if (p.length == 7) {
                    Rental r = new Rental(p[0], p[1], p[2], p[3], p[4], p[5], p[6]);
                    rentals.add(r);
                    if (p[6].equals("RENTED")) rented.add(p[2]); // rebuild HashSet
                }
            }
        } catch (IOException e) { System.out.println("[ERROR] Loading rentals: " + e.getMessage()); }

        System.out.println("✔ Loaded " + movies.size() + " movies, " + rentals.size() + " rentals.");
    }

    static void saveData() {
        // Save movies
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MOVIES_FILE))) {
            bw.write("ID,Title,Genre,Director,Year,Available\n");
            for (Movie m : movies.values()) { bw.write(m.toCsv()); bw.newLine(); }
        } catch (IOException e) { System.out.println("[ERROR] Saving movies: " + e.getMessage()); }

        // Save rentals
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RENTALS_FILE))) {
            bw.write("TxnID,User,MovieID,MovieTitle,RentDate,ReturnDate,Status\n");
            for (Rental r : rentals) { bw.write(r.toCsv()); bw.newLine(); }
        } catch (IOException e) { System.out.println("[ERROR] Saving rentals: " + e.getMessage()); }
    }

    // Returns today's date as dd-MM-yyyy
    static String today() {
        Calendar c = Calendar.getInstance();
        return String.format("%02d-%02d-%04d",
            c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
    }
}
