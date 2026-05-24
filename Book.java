
package Lab06.Question1;
public class Book {
    private String title;
    private String author;
    private int pages;
    
    public Book(String title, String author, int pages){
        this.title = title;
        this.author = author;
        this.pages = pages;
    }
    public String getTitle(){
        return title;
    }
    public String getAuthor(){
        return author;
    }
    public int getPages(){
        return pages;
    }
    public String getBookType(){
        if (pages < 100){
            return "Short Book";
        }
        else {
            return "Regular Book";
        }
    }
}
