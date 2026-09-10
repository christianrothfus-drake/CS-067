public class CountingCharacters
{
    public static void main(String[] args)
    {
        int count = 0;
        count = countChars("banana", 'a');

        System.out.println("There were " + count + " a's in banana");
    }

    // This function takes a word and a character we want to count the appearances of in the word.
    public static int countChars(String word, char characterCounting)
    {
        int wordLength = word.length();
        int charCount = 0;

        for (int charIdx=0; charIdx<wordLength; charIdx++)
        {
            if (word.charAt(charIdx) == characterCounting)
            {
                charCount++;
            }
        }

        return charCount;
    }
}