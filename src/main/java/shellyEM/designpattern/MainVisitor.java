package shellyEM.designpattern;
// Creazione dell'interfaccia Visitor
interface Visitor {
    void visit(Book book);
    void visit(Magazine magazine);
}

// Creazione delle classi visitabili
abstract class Publication {
    private String title;

    public Publication(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract void accept(Visitor visitor);
}

class Book extends Publication {
    private int numPages;

    public Book(String title, int numPages) {
        super(title);
        this.numPages = numPages;
    }

    public int getNumPages() {
        return numPages;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

class Magazine extends Publication {
    private String publisher;

    public Magazine(String title, String publisher) {
        super(title);
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

// Creazione delle classi Visitor concrete
class PrintVisitor implements Visitor {
    public void visit(Book book) {
        System.out.println("Stampo il libro " + book.getTitle() + " con " + book.getNumPages() + " pagine.");
    }

    public void visit(Magazine magazine) {
        System.out.println("Stampo la rivista " + magazine.getTitle() + " pubblicata da " + magazine.getPublisher() + ".");
    }
}

class SaveVisitor implements Visitor {
    public void visit(Book book) {
        System.out.println("Salvo il libro " + book.getTitle() + " con " + book.getNumPages() + " pagine su disco.");
    }

    public void visit(Magazine magazine) {
        System.out.println("Salvo la rivista " + magazine.getTitle() + " pubblicata da " + magazine.getPublisher() + " su disco.");
    }
}

// Esempio di utilizzo del pattern Visitor
public class MainVisitor {
    public static void main(String[] args) {
        Publication[] publications = new Publication[] {
            new Book("Il signore degli anelli", 1000),
            new Magazine("Wired", "Condé Nast")
        };

        Visitor printVisitor = new PrintVisitor();
        Visitor saveVisitor = new SaveVisitor();

        for (Publication pub : publications) {
            pub.accept(printVisitor);
            pub.accept(saveVisitor);
        }
    }
}
