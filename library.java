import java.io.Serializable;
import java.io.*;
import java.util.*;
class Book implements Serializable {
    private String isbn;
    private String title;
    private String author;
    private int publicationYear;

    public Book(String isbn, String title, String author, int publicationYear) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publicationYear=" + publicationYear +
                '}';
    }
}
 class InsufficientInformationException extends Exception {
    public InsufficientInformationException(String message) {
        super(message);
    }
}


 class Library {
    public void processBookData(String sourceFilePath) {
        List<Book> books = new ArrayList<>();

        // Reading book data from the CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(sourceFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    if (data.length < 4) {
                        throw new InsufficientInformationException("Insufficient information for book: " + line);
                    }
                    String isbn = data[0].trim();
                    String title = data[1].trim();
                    String author = data[2].trim();
                    int publicationYear = Integer.parseInt(data[3].trim());

                    books.add(new Book(isbn, title, author, publicationYear));
                } catch (InsufficientInformationException e) {
                    System.err.println(e.getMessage());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid publication year for book: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("The file " + sourceFilePath + " was not found.");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (books.isEmpty()) {
            System.out.println("No valid book data found.");
            return;
        }

        // Sorting books by publication year
        books.sort(Comparator.comparingInt(Book::getPublicationYear));
        System.out.println("Books sorted by publication year:");
        books.forEach(System.out::println);

        // Serializing sorted books by publication year
        serializeBooks(books, "sorted_books_yearwise.dat");

        // Sorting books by ISBN
        books.sort(Comparator.comparing(Book::getIsbn));
        System.out.println("Books sorted by ISBN:");
        books.forEach(System.out::println);

        // Serializing sorted books by ISBN
        serializeBooks(books, "sorted_books_ISBN.dat");
    }

    private void serializeBooks(List<Book> books, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(books);
            System.out.println("Books serialized to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Library service = new Library();
        service.processBookData("C:\\Users\\srial\\OneDrive\\Documents\\desktopfolders\\Threading\\src\\books.csv");
    }
}


