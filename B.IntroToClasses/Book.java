public class Book
{
    String title;
    String author;

    public Book(String title, String author)
    {
        this.title = title;
        this.author = author;
    }

    public void introduce(){
        System.out.println("Hello, I am " + title + " and I was written by " + author + ".");
    }
}
