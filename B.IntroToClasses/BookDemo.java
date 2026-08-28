public class BookDemo
{
    public static void main(String[] args)
    {
        Book book1 = new Book("Empire of Storms", "Sarah J. Maas");
        Book book2 = new Book("Tower of Dawn", "Sarah J. Maas");
        Book book3 = new Book("Kingdom of Ash", "Sarah J. Maas");

        book1.introduce();
        book2.introduce();
        book3.introduce();
    }
}
