public class DatePersonDriver {
    public static void main()
    {
        Date myBirthday = new Date(22, 2, 2005);
        Person myself = new Person("Christian", myBirthday);

        myself.introduce();
    }    
}
