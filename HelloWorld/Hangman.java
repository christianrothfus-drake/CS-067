import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

//Acknowledgements/Credits
//  Runestone Academy - Used for learning how a lot of these concepts work including if-statements,
//      appending to strings, and setting up functions to accept parameters.
//  Baeldung.com - This was used for learning how to generate random numbers, string concatenation,
//      presence of an element in a list, string to character conversion, and character to string conversion.
//  AlgoCademy - This was used for learning how to slice strings to access the characters I want.
//  Google AI - Other various inputs I learned from the Google AI overview including how to get user input,
//      debugging an error with my formatting (I added an extra ';'  -_- ), and comparing booleans.
//  Programiz - Learning how to check if a string is alphabeticals

public class Hangman
    {
    public static void main(String[] args) 
    {
        // Setup for the game
        List<Character> GuessedLetters = new ArrayList<Character>();
        
        int LivesRemaining = 5;
        
        String SecretWord = GetWord();
        
        System.out.println("Welcome! Submit 5 wrong guesses and you lose!");
        DisplayGuessedLetters(SecretWord, GuessedLetters);

        Scanner scanner = new Scanner(System.in);

        // This is the loop that actually runs the game.
        while (LivesRemaining >= 0)
        {
            String UserGuess = GetGuess(scanner);

            if (UserGuess.length() > 1)
            {
                System.out.println("Invalid input. Please submit exactly one letter.\n");
                continue;
            }
            
            char GuessedLetter = UserGuess.toUpperCase().charAt(0);

            if (Character.isAlphabetic(GuessedLetter) == false)
            {
                System.out.println("Invalid input. Please submit a letter.\n");
                continue;
            }

            if (GuessedLetters.contains(GuessedLetter))
            {
                System.out.println(GuessedLetter + " has already been guessed. Please try again.\n");
                continue;
            }

            GuessedLetters.add(GuessedLetter);

            if (SecretWord.contains(Character.toString(GuessedLetter)) == true)
            {
                System.out.println(GuessedLetter + " is in the secret word!\n");

                if (IsSecretWordGuessed(SecretWord, GuessedLetters) == true)
                {
                    System.out.println("Congratulations, you guessed the word!\n");
                    DisplayGuessedLetters(SecretWord, GuessedLetters);
                    return;
                }
            }
            else
            {
                System.out.println(GuessedLetter + " is not in the secret word!\n");

                LivesRemaining -= 1;

                if (LivesRemaining == 0)
                {
                    System.out.println("Sorry, you lose!\nThe secret word was " + SecretWord);
                    return;
                }
            }

            System.out.println("Lives remaining: " + LivesRemaining);
            DisplayGuessedLetters(SecretWord, GuessedLetters);

        }

        scanner.close();
    }

    // This functions creates the list of possible 
    public static String GetWord()
    {
        List<String> ValidWords = new ArrayList<String>();

        ValidWords.add("HELLO");
        ValidWords.add("WORLD");
        ValidWords.add("PSHAW"); // A very very stupid word that wordle used over the summer
        ValidWords.add("MEHER");
        ValidWords.add("TABBY");

        Random random = new Random();

        int RandomIndex = random.nextInt(5);

        return ValidWords.get(RandomIndex);
    }

    // This function prompts the user for a letter to guess.
    public static String GetGuess(Scanner scanner)
    {
        System.out.print("Enter your letter guess: "); // Print what the user should do

        String UserGuess = scanner.nextLine(); // Prompt the user for a guess

        return UserGuess;
    }

    // This function prints out the secret word, with unguessed letters replaced with underscores.
    public static void DisplayGuessedLetters(String SecretWord, List<Character> GuessedLetters)
    {
        int WordLength = SecretWord.length();

        String RevealedWord = "";

        // This loop goes through each letter in the secret word and reveals the letters that have been guessed.
        for (int LetterIdx = 0; LetterIdx < WordLength; LetterIdx++)
        {
            char CurrentLetter = SecretWord.substring(LetterIdx, LetterIdx+1).charAt(0);

            if (GuessedLetters.contains(CurrentLetter))
            {
                RevealedWord = RevealedWord + CurrentLetter;
            }

            else
            {
                RevealedWord = RevealedWord + "_";
            }
        }

        System.out.println(RevealedWord);
    }

    // This function returns true if all of the letters in the secret word have been guessed, false otherwise.
    public static boolean IsSecretWordGuessed(String SecretWord, List<Character> GuessedLetters)
    {
        int WordLength = SecretWord.length();

        for (int LetterIdx = 0; LetterIdx < WordLength; LetterIdx++)
        {
            char CurrentLetter = SecretWord.substring(LetterIdx, LetterIdx+1).charAt(0);

            if (GuessedLetters.contains(CurrentLetter) == false)
            {
                return false;
            }
        }
        
        return true;
    }
}