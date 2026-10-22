package Assignment10.src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


// Acknowledgements
//      https://www.w3schools.com/sql/func_mysql_substring.asp - I learned how to use the SQL substring method


public class CreateDB {
    /**
    * Creates the database used for this program and runs the main loop that allows user functionality.
    * The database is recreated upon the program being run, and is therefore always fresh.
    */
    public static void main() {
    String url = "jdbc:sqlite:Assignments/Assignment10/sets.db";

    // try-with-resources automatically closes the connection and statement.
        try (   Connection connection = DriverManager.getConnection(url);
                Statement statement = connection.createStatement())
        {

            // WARNING: Deletes the existing Sets table and all its records.
            // This allows us to start with the same sample data each time.
            statement.executeUpdate("DROP TABLE IF EXISTS Sets");

            String sql =    "CREATE TABLE Sets (" +
                            "setCode TEXT PRIMARY KEY, " +
                            "name TEXT NOT NULL, " +
                            "tokenSetCode TEXT, " +
                            "releaseDate TEXT, " +       // Dates stored as YYYY/MM/DD
                            "type TEXT, " +
                            "isOnlineOnly BOOLEAN, " +
                            "parentCode TEXT, " +
                            "totalSetSize INTEGER, " +
                            "baseSetSize INTEGER, " +
                            "isPartialPreview BOOLEAN)";
            statement.executeUpdate(sql);

            insertData(statement);
            
            Scanner scanner = new Scanner(System.in);
            
            boolean acceptingResponses = true;
            ResultSet rs;

            // The main loop that prompts the user for a desired action.
            while (acceptingResponses)
            {
                printMainMenuOptions();

                int choice = scanner.nextInt();

                String searchQuery;

                switch (choice)
                {
                    case 1: // The user wants to sort the data
                        String[] sortResults = sortSets(scanner);

                        searchQuery = sortResults[0];
                        String columnUsing = sortResults[1];

                        rs = statement.executeQuery(searchQuery);

                        while (rs.next())
                        {
                            System.out.print(rs.getString("name") + ": ");
                            System.out.println(rs.getString(columnUsing));
                        }

                        break;

                    case 2: // The user wants to filter the data
                        searchQuery = filterSets(scanner);

                        rs = statement.executeQuery(searchQuery);

                        while (rs.next())
                        {
                            System.out.println(rs.getString("name"));
                        }
                        break;

                    case 3: // The user wants to count the number of sets per set type
                        searchQuery = countSetTypes();

                        rs = statement.executeQuery(searchQuery);

                        while (rs.next())
                        {
                            System.out.print(rs.getString("type") + ": ");
                            System.out.println(rs.getString("count"));
                        }

                        break;

                    case 4: // The user wants to get the average set size of all sets
                        searchQuery = averageSetSize();

                        rs = statement.executeQuery(searchQuery);

                        while (rs.next())
                        {
                            System.out.println("The average set size is: " + rs.getString("AverageSetSize"));
                        }
                        break;

                    case 5: // The user wants to get the count of all sets by year
                        searchQuery = countSetsByYear();

                        rs = statement.executeQuery(searchQuery);

                        while (rs.next())
                        {
                            System.out.print(rs.getString("year") + ": ");
                            System.out.println(rs.getString("count"));
                        }
                        continue;

                    case 6: // The user wants to exit the program
                        acceptingResponses = false;
                    default: // The user submitted a bad input, exits the program
                        acceptingResponses = false;
                }
            }

        scanner.close();

        }

        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *  Prints out the main menu options to the terminal.
    */
    public static void printMainMenuOptions()
    {
        System.out.println("Select your desired option:");
        System.out.println("1) Display all sets (with sorting options)");
        System.out.println("2) Display all sets (with filtering options)");
        System.out.println("3) Display the count of all set types");
        System.out.println("4) Display the average of total set size");
        System.out.println("5) Display the count of sets, grouped by year");
        System.out.println("6) Exit program");
    }

    /**
     * Prompts the user for inputs to get the desired query that sorts the data.
     * Takes a scanner to get user input.
     * Returns an array of strings of length 2 containing the SQL query and the column being sorted on (in that order) as strings.
    */ 
    public static String[] sortSets(Scanner scanner)
    {
        System.out.println("What would you like to sort the sets by?");
        System.out.println("1) Name");
        System.out.println("2) Set Code");
        System.out.println("3) Release Date");
        System.out.println("4) Total Set Size");
        System.out.println("5) Base Set Size");

        int sortByChoice = scanner.nextInt();

        System.out.println("Would you like the results to be sorted increasing or decreasing? Respond 0 for ascending, 1 for decreasing.");

        int sortDirectionChoice = scanner.nextInt();

        String columnSortingBy;
        String sortDirection;

        switch (sortByChoice) // Getting the column name from the selected choice.
        {
            case 1:
                columnSortingBy = "name";
                break;
            case 2:
                columnSortingBy = "setCode";
                break;
            case 3:
                columnSortingBy = "releaseDate";
                break;
            case 4:
                columnSortingBy = "totalSetSize";
                break;
            case 5:
                columnSortingBy = "baseSetSize";
                break;
            default:
                columnSortingBy = "name";
        }

        switch (sortDirectionChoice) // Getting the sort direction from the selected choice.
        {
            case 0:
                sortDirection = "ASC";
                break;
            case 1:
                sortDirection = "DESC";
                break;
            default:
                sortDirection = "";
        }

        String searchQuery = "SELECT name, " + columnSortingBy + " FROM Sets ORDER BY " + columnSortingBy + " " + sortDirection + ";";
        String columnName = columnSortingBy;

        String[] returns = new String[2];
        returns[0] = searchQuery;
        returns[1] = columnName;

        return returns;

    }

    /**
     * Prompts the user for inputs to get the desired query that filters the data.
     * Takes a scanner to get user input.
     * Returns a string containing the SQL query.
    */ 
    public static String filterSets(Scanner scanner)
    {
        System.out.println("What would you like to filter the sets by?");
        System.out.println("1) Name");
        System.out.println("2) Release Date");
        System.out.println("3) Type");
        System.out.println("4) Online status");
        System.out.println("5) Release status");

        int filterByChoice = scanner.nextInt();

        String searchQuery;

        switch (filterByChoice)
        {
            case 1:
                System.out.println("What letter or number would you like to filter on?");
                String nameFilter = scanner.next();

                searchQuery = "SELECT * FROM Sets WHERE name LIKE \"" + nameFilter + "%\";";
                break;

            case 2:
                System.out.println("What year would you like to filter on?");
                String yearFilter = scanner.next();

                searchQuery = "SELECT * FROM Sets WHERE releaseDate LIKE \"" + yearFilter + "%\";";
                break;

            case 3:
                System.out.println("What type of set you like to filter on?");
                System.out.println("1) Normal sets");
                System.out.println("2) Commander sets");
                System.out.println("3) Other (type it out)");

                String typeFilter = scanner.next();

                switch (typeFilter)
                {
                    case "1":
                        searchQuery = "SELECT * FROM Sets WHERE type LIKE \"expansion\"";
                        break;
                    case "2":
                        searchQuery = "SELECT * FROM Sets WHERE type LIKE \"commander\"";
                        break;
                    default:
                        searchQuery = "SELECT * FROM Sets WHERE type LIKE \"" + typeFilter + "\"";
                        break;
                }
                break;

            case 4:
                System.out.println("Would you like to see sets without the online only sets, or the only sets only?");
                System.out.println("1) Show online sets only");
                System.out.println("2) Exclude online only sets");

                int onlineChoice = scanner.nextInt();

                if (onlineChoice == 1)
                {
                    searchQuery = "SELECT * FROM Sets WHERE isOnlineOnly = TRUE;";
                }

                else if (onlineChoice == 2)
                {
                    searchQuery = "SELECT * FROM Sets WHERE isOnlineOnly = FALSE;";
                }

                else
                {
                    searchQuery = "";
                }

                break;
            case 5:
                System.out.println("Would you like to see realeased sets only, or unreleased sets only?");
                System.out.println("1) Show unreleased sets");
                System.out.println("2) Show released sets");

                int releaseStatus = scanner.nextInt();

                if (releaseStatus == 1)
                {
                    searchQuery = "SELECT * FROM Sets WHERE isPartialPreview = TRUE;";
                }

                else if (releaseStatus == 2)
                {
                    searchQuery = "SELECT * FROM Sets WHERE isPartialPreview IS NULL;";
                }

                else
                {
                    searchQuery = "";
                }


                break;
            default:
                searchQuery = "";
                break;
        }

        return searchQuery;

    }

    /**
     * Returns a string containing the SQL query to count the number of sets per set type.
    */ 
    public static String countSetTypes()
    {
        return "SELECT type, COUNT(*) AS count FROM Sets GROUP BY type;";
    }

    /**
     * Returns a string containing the SQL query to get the average of the total set size.
    */ 
    public static String averageSetSize()
    {
        return "SELECT AVG(totalSetSize) AS AverageSetSize FROM Sets;";
    }

    /**
     * Returns a string containing the SQL query to get the count of the number of sets by release year.
    */
    public static String countSetsByYear()
    {
        return "SELECT SUBSTRING(releaseDate, 1, 4) AS year, COUNT(*) AS count FROM Sets GROUP BY year;";
    }

    /**
     * Inserts the data for this program.
     * Takes a Statement variable that is used to call the insert statement.
    */
    public static void insertData(Statement statement)
    {
        try
        {
            statement.executeUpdate("insert into Sets values('MDD', 'Marvel: Darkhold Destiny', 'TMDD', '2027/04/09', 'expansion', false, NULL, 12, 12, true)");
            statement.executeUpdate("insert into Sets values('MDC', 'Darkhold Destiny Commander', NULL, '2027/04/09', 'commander', false, 'MDD', 1, 1, true)");
            statement.executeUpdate("insert into Sets values('NAU', 'Nauctis: The Sunken Realm', 'TNAU', '2027/02/05', 'expansion', false, NULL, 0, 0, true)");
            statement.executeUpdate("insert into Sets values('PF27', 'MagicFest 2027', NULL, '2027/01/01', 'promo', false, NULL, 1, 1, true)");
            statement.executeUpdate("insert into Sets values('TRK', 'Star Trek', 'TTRK', '2026/11/13', 'expansion', false, NULL, 135, 135, true)");
            statement.executeUpdate("insert into Sets values('SDS', 'Stardates', NULL, '2026/11/13', 'masterpiece', false, 'TRK', 1, 1, true)");
            statement.executeUpdate("insert into Sets values('TRC', 'Star Trek Commander', NULL, '2026/11/13', 'commander', false, 'TRK', 41, 41, true)");
            statement.executeUpdate("insert into Sets values('MBC', 'Mystery Booster Commander Edition', NULL, '2026/10/19', 'draft_innovation', false, NULL, 80, 80, NULL)");
            statement.executeUpdate("insert into Sets values('YFRA', 'Alchemy: Reality Fracture', NULL, '2026/10/13', 'alchemy', true, 'FRA', 31, 31, NULL)");
            statement.executeUpdate("insert into Sets values('PFRA', 'Reality Fracture Promos', NULL, '2026/10/02', 'promo', false, 'FRA', 90, 90, NULL)");
            statement.executeUpdate("insert into Sets values('AFRA', 'Reality Fracture Art Series', 'AFRA', '2026/10/02', 'memorabilia', false, 'FRA', 54, 54, NULL)");
            statement.executeUpdate("insert into Sets values('FRA', 'Reality Fracture', 'TFRA', '2026/10/02', 'expansion', false, NULL, 462, 290, NULL)");
            statement.executeUpdate("insert into Sets values('FRC', 'Reality Fracture Commander', 'TFRC', '2026/10/02', 'commander', false, 'FRA', 103, 103, NULL)");
            statement.executeUpdate("insert into Sets values('SLZ', 'The Zeta Set', NULL, '2026/09/02', 'box', false, NULL, 363, 363, NULL)");
            statement.executeUpdate("insert into Sets values('AHOB', 'The Hobbit Art Series', 'AHOB', '2026/08/14', 'memorabilia', false, 'HOB', 54, 54, NULL)");
            statement.executeUpdate("insert into Sets values('AHOC', 'The Hobbit Eternal Art Series', 'AHOC', '2026/08/14', 'memorabilia', false, 'HOC', 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('HOB', 'The Hobbit', 'THOB', '2026/08/14', 'expansion', false, NULL, 321, 193, NULL)");
            statement.executeUpdate("insert into Sets values('HOC', 'The Hobbit Eternal', NULL, '2026/08/14', 'eternal', false, 'HOB', 158, 158, NULL)");
            statement.executeUpdate("insert into Sets values('FMSC', 'Marvel Super Heroes Jumpstart Front Cards', 'FMSC', '2026/06/26', 'memorabilia', false, 'MSC', 61, 61, NULL)");
            statement.executeUpdate("insert into Sets values('AMSH', 'Marvel Super Heroes Art Series', 'AMSH', '2026/06/26', 'memorabilia', false, 'MSH', 66, 66, NULL)");
            statement.executeUpdate("insert into Sets values('MSC', 'Marvel Super Heroes Commander', 'TMSC', '2026/06/26', 'commander', false, 'MSH', 866, 866, NULL)");
            statement.executeUpdate("insert into Sets values('MSH', 'Marvel Super Heroes', 'TMSH', '2026/06/26', 'expansion', false, NULL, 453, 296, NULL)");
            statement.executeUpdate("insert into Sets values('YSOS', 'Alchemy: Secrets of Strixhaven', NULL, '2026/05/19', 'alchemy', true, 'SOS', 32, 32, NULL)");
            statement.executeUpdate("insert into Sets values('PSOS', 'Secrets of Strixhaven Promos', NULL, '2026/04/24', 'promo', false, 'SOS', 80, 80, NULL)");
            statement.executeUpdate("insert into Sets values('SOS', 'Secrets of Strixhaven', 'TSOS', '2026/04/24', 'expansion', false, NULL, 368, 281, NULL)");
            statement.executeUpdate("insert into Sets values('SOC', 'Secrets of Strixhaven Commander', 'TSOC', '2026/04/24', 'commander', false, 'SOS', 426, 426, NULL)");
            statement.executeUpdate("insert into Sets values('ASOS', 'Secrets of Strixhaven Art Series', 'ASOS', '2026/04/24', 'memorabilia', false, 'SOS', 54, 54, NULL)");
            statement.executeUpdate("insert into Sets values('SOA', 'Secrets of Strixhaven Mystical Archive', NULL, '2026/04/24', 'masterpiece', false, 'SOS', 195, 195, NULL)");
            statement.executeUpdate("insert into Sets values('PZA', 'Teenage Mutant Ninja Turtles Source Material', NULL, '2026/03/06', 'masterpiece', false, 'TMT', 20, 20, NULL)");
            statement.executeUpdate("insert into Sets values('FTMC', 'Teenage Mutant Ninja Turtles Eternal Front Cards', 'FTMC', '2026/03/06', 'memorabilia', false, 'TMC', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('TMT', 'Teenage Mutant Ninja Turtles', 'TTMT', '2026/03/06', 'expansion', false, NULL, 320, 195, NULL)");
            statement.executeUpdate("insert into Sets values('TMC', 'Teenage Mutant Ninja Turtles Eternal', 'TTMC', '2026/03/06', 'eternal', false, 'TMT', 132, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ATMT', 'Teenage Mutant Ninja Turtles Art Series', 'ATMT', '2026/03/06', 'memorabilia', false, 'TMT', 54, 54, NULL)");
            statement.executeUpdate("insert into Sets values('PL26', 'Year of the Horse 2026', 'PL26', '2026/02/17', 'promo', false, NULL, 4, 5, NULL)");
            statement.executeUpdate("insert into Sets values('YECL', 'Alchemy: Lorwyn Eclipsed', NULL, '2026/02/03', 'alchemy', true, 'ECL', 33, 33, NULL)");
            statement.executeUpdate("insert into Sets values('PECL', 'Lorwyn Eclipsed Promos', NULL, '2026/01/23', 'promo', false, 'ECL', 80, 80, NULL)");
            statement.executeUpdate("insert into Sets values('AECL', 'Lorwyn Eclipsed Art Series', 'AECL', '2026/01/23', 'memorabilia', false, 'ECL', 54, 54, NULL)");
            statement.executeUpdate("insert into Sets values('ECL', 'Lorwyn Eclipsed', 'TECL', '2026/01/23', 'expansion', false, NULL, 408, 273, NULL)");
            statement.executeUpdate("insert into Sets values('ECC', 'Lorwyn Eclipsed Commander', 'TECC', '2026/01/23', 'commander', false, 'ECL', 176, 176, NULL)");
            statement.executeUpdate("insert into Sets values('PW26', 'Wizards Play Network 2026', NULL, '2026/01/01', 'promo', false, NULL, 23, 23, NULL)");
            statement.executeUpdate("insert into Sets values('PF26', 'MagicFest 2026', NULL, '2026/01/01', 'promo', false, NULL, 17, 17, NULL)");
            statement.executeUpdate("insert into Sets values('AFIC', 'Final Fantasy Scene Box', 'AFIC', '2025/12/05', 'memorabilia', false, 'FIC', 24, 24, NULL)");
            statement.executeUpdate("insert into Sets values('PTLA', 'Avatar: The Last Airbender Promos', NULL, '2025/11/21', 'promo', false, 'TLA', 80, 88, NULL)");
            statement.executeUpdate("insert into Sets values('ATLE', 'Avatar: the Last Airbender Eternal Art Series', 'ATLE', '2025/11/21', 'memorabilia', false, 'TLE', 12, 0, NULL)");
            statement.executeUpdate("insert into Sets values('FTLA', 'Avatar: The Last Airbender Beginner Box Front Cards', 'FTLA', '2025/11/21', 'memorabilia', false, 'TLA', 10, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ATLA', 'Avatar: the Last Airbender Art Series', 'ATLA', '2025/11/21', 'memorabilia', false, 'TLA', 54, 0, NULL)");
            statement.executeUpdate("insert into Sets values('TLE', 'Avatar: The Last Airbender Eternal', 'TTLE', '2025/11/21', 'eternal', false, 'TLA', 317, 0, NULL)");
            statement.executeUpdate("insert into Sets values('JTLA', 'Avatar: The Last Airbender Jumpstart Front Cards', 'JTLA', '2025/11/21', 'memorabilia', false, 'TLA', 46, 0, NULL)");
            statement.executeUpdate("insert into Sets values('TLA', 'Avatar: The Last Airbender', 'TTLA', '2025/11/21', 'expansion', false, NULL, 394, 286, NULL)");
            statement.executeUpdate("insert into Sets values('LMAR', 'Marvel Legends Series Inserts', NULL, '2025/09/30', 'promo', false, 'MAR', 8, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ASPM', 'Marvel''s Spider-Man Art Series', 'ASPM', '2025/09/26', 'memorabilia', false, 'SPM', 54, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PSPM', 'Marvel''s Spider-Man Promos', NULL, '2025/09/26', 'promo', false, 'SPM', 68, 73, NULL)");
            statement.executeUpdate("insert into Sets values('SPM', 'Marvel''s Spider-Man', 'TSPM', '2025/09/26', 'expansion', false, NULL, 286, 198, NULL)");
            statement.executeUpdate("insert into Sets values('SPE', 'Marvel''s Spider-Man Eternal', NULL, '2025/09/26', 'eternal', false, 'SPM', 26, 20, NULL)");
            statement.executeUpdate("insert into Sets values('MAR', 'Marvel Universe', NULL, '2025/09/26', 'masterpiece', false, NULL, 100, 0, NULL)");
            statement.executeUpdate("insert into Sets values('AA4', 'Arena Anthology 4', NULL, '2025/09/23', 'box', true, NULL, 28, 28, NULL)");
            statement.executeUpdate("insert into Sets values('OM1', 'Through the Omenpaths', NULL, '2025/09/23', 'expansion', true, 'SPM', 189, 194, NULL)");
            statement.executeUpdate("insert into Sets values('AA3', 'Arena Anthology 3', NULL, '2025/09/23', 'box', true, NULL, 25, 25, NULL)");
            statement.executeUpdate("insert into Sets values('OMB', 'Through the Omenpaths Bonus Sheet', NULL, '2025/09/23', 'masterpiece', true, 'MAR', 40, 40, NULL)");
            statement.executeUpdate("insert into Sets values('PH23', '2023 Heroes of the Realm', NULL, '2025/08/24', 'funny', false, NULL, 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('AA2', 'Arena Anthology 2', NULL, '2025/08/19', 'box', true, NULL, 25, 26, NULL)");
            statement.executeUpdate("insert into Sets values('AA1', 'Arena Anthology 1', NULL, '2025/08/19', 'box', true, NULL, 26, 26, NULL)");
            statement.executeUpdate("insert into Sets values('YEOE', 'Alchemy: Edge of Eternities', NULL, '2025/08/19', 'alchemy', true, 'EOE', 41, 41, NULL)");
            statement.executeUpdate("insert into Sets values('AEOE', 'Edge of Eternities Art Series', 'AEOE', '2025/08/01', 'memorabilia', false, 'EOE', 54, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PEOE', 'Edge of Eternities Promos', NULL, '2025/08/01', 'promo', false, 'EOE', 160, 160, NULL)");
            statement.executeUpdate("insert into Sets values('PBIG', 'The Big Score Promos', NULL, '2025/08/01', 'promo', false, 'BIG', 30, 30, NULL)");
            statement.executeUpdate("insert into Sets values('EOS', 'Edge of Eternities: Stellar Sights', NULL, '2025/08/01', 'masterpiece', false, 'EOE', 180, 0, NULL)");
            statement.executeUpdate("insert into Sets values('EOE', 'Edge of Eternities', 'TEOE', '2025/08/01', 'expansion', false, NULL, 400, 276, NULL)");
            statement.executeUpdate("insert into Sets values('EOC', 'Edge of Eternities Commander', 'TEOC', '2025/08/01', 'commander', false, 'EOE', 191, 24, NULL)");
            statement.executeUpdate("insert into Sets values('RFIN', 'Final Fantasy Regional Promos', NULL, '2025/06/13', 'promo', false, 'FIN', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('FIC', 'Final Fantasy Commander', 'TFIC', '2025/06/13', 'commander', false, 'FIN', 486, 100, NULL)");
            statement.executeUpdate("insert into Sets values('PFIN', 'Final Fantasy Promos', NULL, '2025/06/13', 'promo', false, 'FIN', 94, 116, NULL)");
            statement.executeUpdate("insert into Sets values('FCA', 'Final Fantasy: Through the Ages', NULL, '2025/06/13', 'masterpiece', false, 'FIN', 65, 0, NULL)");
            statement.executeUpdate("insert into Sets values('AFIN', 'Final Fantasy Art Series', 'AFIN', '2025/06/13', 'memorabilia', false, 'FIN', 53, 0, NULL)");
            statement.executeUpdate("insert into Sets values('WFIN', 'FIN Asia WPN Promo Tokens', 'WFIN', '2025/06/13', 'token', false, 'FIN', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PSS5', 'FIN Standard Showdown', NULL, '2025/06/13', 'promo', false, 'FIN', 2, 1, NULL)");
            statement.executeUpdate("insert into Sets values('FIN', 'Final Fantasy', 'TFIN', '2025/06/13', 'expansion', false, NULL, 598, 309, NULL)");
            statement.executeUpdate("insert into Sets values('PA1', 'Pioneer Anthology 1', NULL, '2025/05/05', 'box', true, NULL, 11, 11, NULL)");
            statement.executeUpdate("insert into Sets values('YTDM', 'Alchemy: Tarkir', NULL, '2025/04/29', 'alchemy', true, 'TDM', 30, 33, NULL)");
            statement.executeUpdate("insert into Sets values('PTDM', 'Tarkir: Dragonstorm Promos', NULL, '2025/04/11', 'promo', false, 'TDM', 160, 166, NULL)");
            statement.executeUpdate("insert into Sets values('ATDM', 'Tarkir: Dragonstorm Art Series', 'ATDM', '2025/04/11', 'memorabilia', false, 'TDM', 54, 0, NULL)");
            statement.executeUpdate("insert into Sets values('TDC', 'Tarkir: Dragonstorm Commander', 'TTDC', '2025/04/11', 'commander', false, 'TDM', 413, 50, NULL)");
            statement.executeUpdate("insert into Sets values('TDM', 'Tarkir: Dragonstorm', 'TTDM', '2025/04/11', 'expansion', false, NULL, 426, 291, NULL)");
            statement.executeUpdate("insert into Sets values('PLG25', 'Love Your LGS 2025', NULL, '2025/03/26', 'promo', false, NULL, 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('YDFT', 'Alchemy: Aetherdrift', NULL, '2025/03/04', 'alchemy', true, 'DFT', 31, 31, NULL)");
            statement.executeUpdate("insert into Sets values('PSSC', 'Secret Lair Showcase Planes', NULL, '2025/02/21', 'memorabilia', false, 'SLD', 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('ADFT', 'Aetherdrift Art Series', 'ADFT', '2025/02/14', 'memorabilia', false, 'DFT', 54, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PDFT', 'Aetherdrift Promos', NULL, '2025/02/14', 'promo', false, 'DFT', 160, 160, NULL)");
            statement.executeUpdate("insert into Sets values('DFT', 'Aetherdrift', 'TDFT', '2025/02/14', 'expansion', false, NULL, 553, 291, NULL)");
            statement.executeUpdate("insert into Sets values('PL25', 'Year of the Snake 2025', 'PL25', '2025/02/14', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('DRC', 'Aetherdrift Commander', 'TDRC', '2025/02/14', 'commander', false, 'DFT', 184, 187, NULL)");
            statement.executeUpdate("insert into Sets values('PJSC', 'Japan Standard Cup', NULL, '2025/02/09', 'promo', false, NULL, 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('AINR', 'Innistrad Remastered Art Series', 'AINR', '2025/01/24', 'memorabilia', false, 'INR', 25, 0, NULL)");
            statement.executeUpdate("insert into Sets values('INR', 'Innistrad Remastered', 'TINR', '2025/01/24', 'masters', false, NULL, 495, 287, NULL)");
            statement.executeUpdate("insert into Sets values('PSPL', 'Spotlight Series', NULL, '2025/01/03', 'promo', false, NULL, 13, 13, NULL)");
            statement.executeUpdate("insert into Sets values('PW25', 'Wizards Play Network 2025', NULL, '2025/01/01', 'promo', false, NULL, 16, 1, NULL)");
            statement.executeUpdate("insert into Sets values('PF25', 'MagicFest 2025', NULL, '2025/01/01', 'promo', false, NULL, 19, 19, NULL)");
            statement.executeUpdate("insert into Sets values('PIO', 'Pioneer Masters', NULL, '2024/12/10', 'masters', true, NULL, 398, 278, NULL)");
            statement.executeUpdate("insert into Sets values('PFDN', 'Foundations Promos', NULL, '2024/11/15', 'promo', false, 'FDN', 106, 106, NULL)");
            statement.executeUpdate("insert into Sets values('AFDN', 'Foundations Art Series', 'AFDN', '2024/11/15', 'memorabilia', false, 'FDN', 55, 0, NULL)");
            statement.executeUpdate("insert into Sets values('FDN', 'Foundations', 'TFDN', '2024/11/15', 'core', false, NULL, 771, 291, NULL)");
            statement.executeUpdate("insert into Sets values('J25', 'Foundations Jumpstart', NULL, '2024/11/15', 'draft_innovation', false, 'FDN', 779, 779, NULL)");
            statement.executeUpdate("insert into Sets values('FFDN', 'Foundations Front Cards', 'FFDN', '2024/11/15', 'memorabilia', false, 'FDN', 10, 0, NULL)");
            statement.executeUpdate("insert into Sets values('FJ25', 'Foundations Jumpstart Front Cards', 'FJ25', '2024/11/15', 'memorabilia', false, 'J25', 46, 0, NULL)");
            statement.executeUpdate("insert into Sets values('FDC', 'Foundations Commander', 'TFDC', '2024/11/15', 'commander', false, 'FDN', 319, 319, NULL)");
            statement.executeUpdate("insert into Sets values('PLTC', 'Tales of Middle-earth Deluxe Commander Kit', NULL, '2024/10/16', 'promo', false, 'LTR', 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('YDSK', 'Alchemy: Duskmourn', NULL, '2024/10/15', 'alchemy', true, 'DSK', 30, 32, NULL)");
            statement.executeUpdate("insert into Sets values('PDSK', 'Duskmourn: House of Horror Promos', NULL, '2024/09/27', 'promo', false, 'DSK', 160, 180, NULL)");
            statement.executeUpdate("insert into Sets values('ADSK', 'Duskmourn: House of Horror Art Series', 'ADSK', '2024/09/27', 'memorabilia', false, 'DSK', 54, 0, NULL)");
            statement.executeUpdate("insert into Sets values('DSK', 'Duskmourn: House of Horror', 'TDSK', '2024/09/27', 'expansion', false, NULL, 417, 286, NULL)");
            statement.executeUpdate("insert into Sets values('DSC', 'Duskmourn: House of Horror Commander', 'TDSC', '2024/09/27', 'commander', false, 'DSK', 373, 40, NULL)");
            statement.executeUpdate("insert into Sets values('YBLB', 'Alchemy: Bloomburrow', NULL, '2024/08/20', 'alchemy', true, 'BLB', 30, 30, NULL)");
            statement.executeUpdate("insert into Sets values('PLG24', 'Love Your LGS 2024', 'PLG24', '2024/08/06', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PBLB', 'Bloomburrow Promos', NULL, '2024/08/02', 'promo', false, 'BLB', 160, 160, NULL)");
            statement.executeUpdate("insert into Sets values('ABLB', 'Bloomburrow Art Series', 'ABLB', '2024/08/02', 'memorabilia', false, 'BLB', 54, 0, NULL)");
            statement.executeUpdate("insert into Sets values('BLC', 'Bloomburrow Commander', 'TBLC', '2024/08/02', 'commander', false, 'BLB', 356, 40, NULL)");
            statement.executeUpdate("insert into Sets values('PCBB', 'Cowboy Bebop', NULL, '2024/08/02', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('BLB', 'Bloomburrow', 'TBLB', '2024/08/02', 'expansion', false, NULL, 397, 281, NULL)");
            statement.executeUpdate("insert into Sets values('MB2', 'Mystery Booster 2', 'MB2', '2024/08/02', 'masters', false, NULL, 384, 390, NULL)");
            statement.executeUpdate("insert into Sets values('MACR', 'Assassin''s Creed Minigames', 'MACR', '2024/07/05', 'minigame', false, 'ACR', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('AACR', 'Assassin''s Creed Art Series', 'AACR', '2024/07/05', 'memorabilia', false, 'ACR', 20, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ACR', 'Assassin''s Creed', 'TACR', '2024/07/05', 'draft_innovation', false, NULL, 309, 110, NULL)");
            statement.executeUpdate("insert into Sets values('SMH3', 'Modern Horizons 3 Substitute Cards', 'SMH3', '2024/06/14', 'token', false, 'MH3', 1, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PMH3', 'Modern Horizons 3 Promos', NULL, '2024/06/14', 'promo', false, 'MH3', 92, 90, NULL)");
            statement.executeUpdate("insert into Sets values('MH3', 'Modern Horizons 3', 'TMH3', '2024/06/14', 'draft_innovation', false, NULL, 524, 303, NULL)");
            statement.executeUpdate("insert into Sets values('AMH3', 'Modern Horizons 3 Art Series', 'AMH3', '2024/06/14', 'memorabilia', false, 'MH3', 54, 0, NULL)");
            statement.executeUpdate("insert into Sets values('M3C', 'Modern Horizons 3 Commander', 'TM3C', '2024/06/14', 'commander', false, 'MH3', 398, 16, NULL)");
            statement.executeUpdate("insert into Sets values('H2R', 'Modern Horizons 2 Timeshifts', NULL, '2024/06/14', 'draft_innovation', false, 'MH3', 16, 0, NULL)");
            statement.executeUpdate("insert into Sets values('YOTJ', 'Alchemy: Outlaws of Thunder Junction', NULL, '2024/05/07', 'alchemy', true, 'OTJ', 30, 31, NULL)");
            statement.executeUpdate("insert into Sets values('AOTJ', 'Outlaws of Thunder Junction Art Series', 'AOTJ', '2024/04/19', 'memorabilia', false, 'OTJ', 54, 0, NULL)");
            statement.executeUpdate("insert into Sets values('POTJ', 'Outlaws of Thunder Junction Promos', NULL, '2024/04/19', 'promo', false, 'OTJ', 160, 160, NULL)");
            statement.executeUpdate("insert into Sets values('OTP', 'Breaking News', 'TOTP', '2024/04/19', 'masterpiece', false, 'OTJ', 80, 0, NULL)");
            statement.executeUpdate("insert into Sets values('OTC', 'Outlaws of Thunder Junction Commander', 'TOTC', '2024/04/19', 'commander', false, 'OTJ', 342, 40, NULL)");
            statement.executeUpdate("insert into Sets values('OTJ', 'Outlaws of Thunder Junction', 'TOTJ', '2024/04/19', 'expansion', false, NULL, 374, 286, NULL)");
            statement.executeUpdate("insert into Sets values('BIG', 'The Big Score', 'TBIG', '2024/04/19', 'expansion', false, 'OTJ', 95, 30, NULL)");
            statement.executeUpdate("insert into Sets values('PIP', 'Fallout', 'TPIP', '2024/03/08', 'commander', false, NULL, 1068, 1076, NULL)");
            statement.executeUpdate("insert into Sets values('YMKM', 'Alchemy: Murders at Karlov Manor', NULL, '2024/03/05', 'alchemy', true, 'MKM', 30, 30, NULL)");
            statement.executeUpdate("insert into Sets values('PUNK', 'Black Lotus Unknown Planechase', NULL, '2024/02/24', 'funny', false, 'UNK', 52, 52, NULL)");
            statement.executeUpdate("insert into Sets values('FCLU', 'Ravnica: Clue Edition Front Cards', 'FCLU', '2024/02/23', 'memorabilia', false, 'CLU', 10, 0, NULL)");
            statement.executeUpdate("insert into Sets values('CLU', 'Ravnica: Clue Edition', NULL, '2024/02/23', 'draft_innovation', false, 'MKM', 284, 289, NULL)");
            statement.executeUpdate("insert into Sets values('PSS4', 'MKM Standard Showdown', NULL, '2024/02/10', 'promo', false, 'MKM', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('AMKM', 'Murders at Karlov Manor Art Series', 'AMKM', '2024/02/09', 'memorabilia', false, 'MKM', 54, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PMKM', 'Murders at Karlov Manor Promos', NULL, '2024/02/09', 'promo', false, 'MKM', 180, 182, NULL)");
            statement.executeUpdate("insert into Sets values('MKM', 'Murders at Karlov Manor', 'TMKM', '2024/02/09', 'expansion', false, NULL, 451, 286, NULL)");
            statement.executeUpdate("insert into Sets values('MKC', 'Murders at Karlov Manor Commander', 'TMKC', '2024/02/09', 'commander', false, 'MKM', 358, 361, NULL)");
            statement.executeUpdate("insert into Sets values('WMKM', 'MKM Japanese Promo Tokens', 'WMKM', '2024/02/09', 'token', false, 'MKM', 4, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PL24', 'Year of the Dragon 2024', 'PL24', '2024/02/08', 'promo', false, NULL, 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('RVR', 'Ravnica Remastered', 'TRVR', '2024/01/12', 'masters', false, NULL, 531, 291, NULL)");
            statement.executeUpdate("insert into Sets values('PW24', 'Wizards Play Network 2024', NULL, '2024/01/01', 'promo', false, NULL, 18, 14, NULL)");
            statement.executeUpdate("insert into Sets values('PF24', 'MagicFest 2024', NULL, '2024/01/01', 'promo', false, NULL, 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('YLCI', 'Alchemy: Ixalan', NULL, '2023/12/05', 'alchemy', true, 'LCI', 31, 31, NULL)");
            statement.executeUpdate("insert into Sets values('SPG', 'Special Guests', NULL, '2023/11/17', 'masterpiece', false, NULL, 175, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PLCI', 'The Lost Caverns of Ixalan Promos', NULL, '2023/11/17', 'promo', false, 'LCI', 136, 138, NULL)");
            statement.executeUpdate("insert into Sets values('ALCI', 'The Lost Caverns of Ixalan Art Series', 'ALCI', '2023/11/17', 'memorabilia', false, 'LCI', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('SLCI', 'The Lost Caverns of Ixalan Substitute Cards', 'SLCI', '2023/11/17', 'token', false, 'LCI', 1, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PMAT', 'March of the Machine: The Aftermath Promos', NULL, '2023/11/17', 'promo', false, 'MAT', 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('LCI', 'The Lost Caverns of Ixalan', 'TLCI', '2023/11/17', 'expansion', false, NULL, 415, 291, NULL)");
            statement.executeUpdate("insert into Sets values('LCC', 'The Lost Caverns of Ixalan Commander', 'TLCC', '2023/11/17', 'commander', false, 'LCI', 370, 16, NULL)");
            statement.executeUpdate("insert into Sets values('REX', 'Jurassic World Collection', 'TREX', '2023/11/17', 'eternal', false, 'LCI', 45, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ALTC', 'Tales of Middle-earth Scene Box', 'ALTC', '2023/11/04', 'memorabilia', false, 'LTC', 24, 0, NULL)");
            statement.executeUpdate("insert into Sets values('WHO', 'Doctor Who', 'TWHO', '2023/10/13', 'commander', false, NULL, 1178, 331, NULL)");
            statement.executeUpdate("insert into Sets values('YWOE', 'Alchemy: Wilds of Eldraine', NULL, '2023/10/10', 'alchemy', true, 'WOE', 31, 34, NULL)");
            statement.executeUpdate("insert into Sets values('PWOE', 'Wilds of Eldraine Promos', NULL, '2023/09/08', 'promo', false, 'WOE', 160, 200, NULL)");
            statement.executeUpdate("insert into Sets values('WWOE', 'WOE Japanese Promo Tokens', 'WWOE', '2023/09/08', 'token', false, 'WOE', 6, 0, NULL)");
            statement.executeUpdate("insert into Sets values('WOC', 'Wilds of Eldraine Commander', 'TWOC', '2023/09/08', 'commander', false, 'WOE', 173, 28, NULL)");
            statement.executeUpdate("insert into Sets values('WOT', 'Wilds of Eldraine: Enchanting Tales', NULL, '2023/09/08', 'masterpiece', false, 'WOE', 103, 0, NULL)");
            statement.executeUpdate("insert into Sets values('AWOE', 'Wilds of Eldraine Art Series', 'AWOE', '2023/09/08', 'memorabilia', false, 'WOE', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('WOE', 'Wilds of Eldraine', 'TWOE', '2023/09/08', 'expansion', false, NULL, 381, 276, NULL)");
            statement.executeUpdate("insert into Sets values('PMDA', 'Magic Ã— Duel Masters Promos', NULL, '2023/09/08', 'promo', false, NULL, 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('PTSR', 'Time Spiral Remastered Promos', NULL, '2023/09/08', 'promo', false, 'TSR', 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('P30T', '30th Anniversary Celebration Tokyo', NULL, '2023/09/01', 'promo', false, '30A', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('ACMM', 'Commander Masters Art Series', 'ACMM', '2023/08/04', 'memorabilia', false, 'CMM', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('CMM', 'Commander Masters', 'TCMM', '2023/08/04', 'masters', false, NULL, 1067, 451, NULL)");
            statement.executeUpdate("insert into Sets values('PH22', '2022 Heroes of the Realm', NULL, '2023/08/01', 'funny', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('HA7', 'Historic Anthology 7', NULL, '2023/07/18', 'box', true, NULL, 25, 25, NULL)");
            statement.executeUpdate("insert into Sets values('EA3', 'Explorer Anthology 3', NULL, '2023/07/18', 'box', true, NULL, 25, 25, NULL)");
            statement.executeUpdate("insert into Sets values('PF23', 'MagicFest 2023', NULL, '2023/07/01', 'promo', false, NULL, 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('PLTR', 'Tales of Middle-earth Promos', NULL, '2023/06/23', 'promo', false, 'LTR', 86, 86, NULL)");
            statement.executeUpdate("insert into Sets values('LTC', 'Tales of Middle-earth Commander', 'TLTC', '2023/06/23', 'commander', false, 'LTR', 591, 84, NULL)");
            statement.executeUpdate("insert into Sets values('FLTR', 'Tales of Middle-earth Front Cards', 'FLTR', '2023/06/23', 'memorabilia', false, 'LTR', 10, 0, NULL)");
            statement.executeUpdate("insert into Sets values('LTR', 'The Lord of the Rings: Tales of Middle-earth', 'TLTR', '2023/06/23', 'draft_innovation', false, NULL, 854, 281, NULL)");
            statement.executeUpdate("insert into Sets values('MLTR', 'The Lord of the Rings: Tales of Middle-earth Minigames', 'MLTR', '2023/06/23', 'minigame', false, 'LTR', 1, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ALTR', 'Tales of Middle-earth Art Series', 'ALTR', '2023/06/23', 'memorabilia', false, 'LTR', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('MAT', 'March of the Machine: The Aftermath', NULL, '2023/05/12', 'expansion', false, NULL, 230, 50, NULL)");
            statement.executeUpdate("insert into Sets values('FMOM', 'March of the Machine Jumpstart Front Cards', 'FMOM', '2023/04/21', 'memorabilia', false, 'MOM', 5, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PMOM', 'March of the Machine Promos', NULL, '2023/04/21', 'promo', false, 'MOM', 134, 162, NULL)");
            statement.executeUpdate("insert into Sets values('MOM', 'March of the Machine', 'TMOM', '2023/04/21', 'expansion', false, NULL, 387, 291, NULL)");
            statement.executeUpdate("insert into Sets values('WMOM', 'MOM Japanese Promo Tokens', 'WMOM', '2023/04/21', 'token', false, 'MOM', 12, 0, NULL)");
            statement.executeUpdate("insert into Sets values('AMOM', 'March of the Machine Art Series', 'AMOM', '2023/04/21', 'memorabilia', false, 'MOM', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('MOC', 'March of the Machine Commander', 'TMOC', '2023/04/21', 'commander', false, 'MOM', 450, 79, NULL)");
            statement.executeUpdate("insert into Sets values('SMOM', 'March of the Machine Substitute Cards', 'SMOM', '2023/04/21', 'token', false, 'MOM', 1, 0, NULL)");
            statement.executeUpdate("insert into Sets values('MUL', 'Multiverse Legends', 'TMUL', '2023/04/21', 'masterpiece', false, 'MOM', 260, 260, NULL)");
            statement.executeUpdate("insert into Sets values('SIS', 'Shadows of the Past', NULL, '2023/03/21', 'masters', true, 'SIR', 76, 81, NULL)");
            statement.executeUpdate("insert into Sets values('SIR', 'Shadows over Innistrad Remastered', NULL, '2023/03/21', 'masters', true, NULL, 294, 320, NULL)");
            statement.executeUpdate("insert into Sets values('YONE', 'Alchemy: Phyrexia', NULL, '2023/02/28', 'alchemy', true, 'ONE', 31, 31, NULL)");
            statement.executeUpdate("insert into Sets values('SLP', 'Secret Lair Promo', NULL, '2023/02/17', 'promo', false, 'SLD', 57, 50, NULL)");
            statement.executeUpdate("insert into Sets values('UNK', 'Unknown Event', NULL, '2023/02/15', 'funny', false, NULL, 526, 477, NULL)");
            statement.executeUpdate("insert into Sets values('ONE', 'Phyrexia: All Will Be One', 'TONE', '2023/02/10', 'expansion', false, NULL, 479, 271, NULL)");
            statement.executeUpdate("insert into Sets values('MONE', 'Phyrexia: All Will Be One Minigames', 'MONE', '2023/02/10', 'minigame', false, 'ONE', 5, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ONC', 'Phyrexia: All Will Be One Commander', 'TONC', '2023/02/10', 'commander', false, 'ONE', 174, 28, NULL)");
            statement.executeUpdate("insert into Sets values('FONE', 'Phyrexia: All Will Be One Jumpstart Front Cards', 'FONE', '2023/02/10', 'memorabilia', false, 'ONE', 5, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PL23', 'Year of the Rabbit 2023', 'PL23', '2023/02/10', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('AONE', 'Phyrexia: All Will Be One Art Series', 'AONE', '2023/02/10', 'memorabilia', false, 'ONE', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PONE', 'Phyrexia: All Will Be One Promos', NULL, '2023/02/10', 'promo', false, 'ONE', 160, 160, NULL)");
            statement.executeUpdate("insert into Sets values('WONE', 'ONE Japanese Promo Tokens', 'WONE', '2023/01/27', 'token', false, 'ONE', 6, 0, NULL)");
            statement.executeUpdate("insert into Sets values('DMR', 'Dominaria Remastered', 'TDMR', '2023/01/13', 'masters', false, NULL, 457, 261, NULL)");
            statement.executeUpdate("insert into Sets values('PR23', 'Regional Championship Qualifiers 2023', NULL, '2023/01/07', 'promo', false, NULL, 3, 4, NULL)");
            statement.executeUpdate("insert into Sets values('PW23', 'Wizards Play Network 2023', NULL, '2023/01/01', 'promo', false, NULL, 11, 11, NULL)");
            statement.executeUpdate("insert into Sets values('P23', 'Judge Gift Cards 2023', NULL, '2023/01/01', 'promo', false, NULL, 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('YBRO', 'Alchemy: The Brothers'' War', NULL, '2022/12/13', 'alchemy', true, 'BRO', 31, 31, NULL)");
            statement.executeUpdate("insert into Sets values('EA2', 'Explorer Anthology 2', NULL, '2022/12/13', 'box', true, NULL, 25, 25, NULL)");
            statement.executeUpdate("insert into Sets values('SCD', 'Starter Commander Decks', 'TSCD', '2022/12/02', 'commander', false, NULL, 352, 352, NULL)");
            statement.executeUpdate("insert into Sets values('FJ22', 'Jumpstart 2022 Front Cards', 'FJ22', '2022/12/02', 'memorabilia', false, 'J22', 46, 0, NULL)");
            statement.executeUpdate("insert into Sets values('J22', 'Jumpstart 2022', NULL, '2022/12/02', 'draft_innovation', false, NULL, 835, 834, NULL)");
            statement.executeUpdate("insert into Sets values('30A', '30th Anniversary Edition', 'T30A', '2022/11/28', 'memorabilia', false, NULL, 594, 594, NULL)");
            statement.executeUpdate("insert into Sets values('PEWK', 'Eternal Weekend', NULL, '2022/11/26', 'promo', false, NULL, 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('BRC', 'The Brothers'' War Commander', 'TBRC', '2022/11/18', 'commander', false, 'BRO', 209, 40, NULL)");
            statement.executeUpdate("insert into Sets values('BRR', 'The Brothers'' War Retro Artifacts', NULL, '2022/11/18', 'masterpiece', false, 'BRO', 189, 63, NULL)");
            statement.executeUpdate("insert into Sets values('PBRO', 'The Brothers'' War Promos', 'PBRO', '2022/11/18', 'promo', false, 'BRO', 169, 169, NULL)");
            statement.executeUpdate("insert into Sets values('PTBRO', 'The Brothers'' War Southeast Asia Tokens', 'PTBRO', '2022/11/18', 'token', false, 'BRO', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('BOT', 'Transformers', 'TBOT', '2022/11/18', 'eternal', false, 'BRO', 29, 58, NULL)");
            statement.executeUpdate("insert into Sets values('BRO', 'The Brothers'' War', 'TBRO', '2022/11/18', 'expansion', false, NULL, 387, 287, NULL)");
            statement.executeUpdate("insert into Sets values('FBRO', 'The Brothers'' War Jumpstart Front Cards', 'FBRO', '2022/11/18', 'memorabilia', false, 'BRO', 5, 0, NULL)");
            statement.executeUpdate("insert into Sets values('SBRO', 'The Brothers'' War Substitute Cards', 'SBRO', '2022/11/18', 'token', false, 'BRO', 1, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ABRO', 'The Brothers'' War Art Series', 'ABRO', '2022/11/18', 'memorabilia', false, 'BRO', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('MBRO', 'The Brothers'' War Minigames', 'MBRO', '2022/11/18', 'minigame', false, 'BRO', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('SLC', 'Secret Lair Countdown', NULL, '2022/11/01', 'box', false, 'SLD', 114, 114, NULL)");
            statement.executeUpdate("insert into Sets values('GN3', 'Game Night: Free-for-All', 'TGN3', '2022/10/14', 'box', false, NULL, 135, 135, NULL)");
            statement.executeUpdate("insert into Sets values('ULST', 'The List (Unfinity Foil Edition)', NULL, '2022/10/07', 'funny', false, 'PLST', 62, 62, NULL)");
            statement.executeUpdate("insert into Sets values('UNF', 'Unfinity', 'TUNF', '2022/10/07', 'funny', false, NULL, 639, 244, NULL)");
            statement.executeUpdate("insert into Sets values('SUNF', 'Unfinity Sticker Sheets', NULL, '2022/10/07', 'funny', false, 'UNF', 48, 48, NULL)");
            statement.executeUpdate("insert into Sets values('40K', 'Warhammer 40,000 Commander', 'T40K', '2022/10/07', 'commander', false, NULL, 617, 617, NULL)");
            statement.executeUpdate("insert into Sets values('YDMU', 'Alchemy: Dominaria', NULL, '2022/10/05', 'alchemy', true, 'DMU', 39, 39, NULL)");
            statement.executeUpdate("insert into Sets values('PRCQ', 'Regional Championship Qualifiers 2022', NULL, '2022/10/01', 'promo', false, NULL, 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('P30H', '30th Anniversary History Promos', NULL, '2022/09/09', 'promo', false, '30A', 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('PDMU', 'Dominaria United Promos', NULL, '2022/09/09', 'promo', false, 'DMU', 161, 161, NULL)");
            statement.executeUpdate("insert into Sets values('PTDMU', 'Dominaria United Southeast Asia Tokens', 'PTDMU', '2022/09/09', 'token', false, 'DMU', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('WDMU', 'DMU Japanese Promo Tokens', 'WDMU', '2022/09/09', 'token', false, 'DMU', 5, 0, NULL)");
            statement.executeUpdate("insert into Sets values('MDMU', 'Dominaria United Minigames', 'MDMU', '2022/09/09', 'minigame', false, 'DMU', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('DMU', 'Dominaria United', 'TDMU', '2022/09/09', 'expansion', false, NULL, 436, 281, NULL)");
            statement.executeUpdate("insert into Sets values('DMC', 'Dominaria United Commander', 'TDMC', '2022/09/09', 'commander', false, 'DMU', 240, 50, NULL)");
            statement.executeUpdate("insert into Sets values('FDMU', 'Dominaria United Jumpstart Front Cards', 'FDMU', '2022/09/09', 'memorabilia', false, 'DMU', 10, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ADMU', 'Dominaria United Art Series', 'ADMU', '2022/09/09', 'memorabilia', false, 'DMU', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('P30M', '30th Anniversary Misc Promos', NULL, '2022/09/02', 'promo', false, '30A', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('P30A', '30th Anniversary Play Promos', NULL, '2022/09/02', 'promo', false, '30A', 30, 30, NULL)");
            statement.executeUpdate("insert into Sets values('PSVC', 'Summer Vacation Promos 2022', NULL, '2022/08/01', 'promo', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('PH21', '2021 Heroes of the Realm', NULL, '2022/08/01', 'funny', false, NULL, 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('EA1', 'Explorer Anthology 1', NULL, '2022/07/28', 'box', true, NULL, 20, 20, NULL)");
            statement.executeUpdate("insert into Sets values('HA6', 'Historic Anthology 6', NULL, '2022/07/28', 'box', true, NULL, 20, 20, NULL)");
            statement.executeUpdate("insert into Sets values('SCH', 'Store Championships', NULL, '2022/07/09', 'promo', false, NULL, 53, 49, NULL)");
            statement.executeUpdate("insert into Sets values('2X2', 'Double Masters 2022', 'T2X2', '2022/07/08', 'masters', false, NULL, 579, 331, NULL)");
            statement.executeUpdate("insert into Sets values('HBG', 'Alchemy Horizons: Baldur''s Gate', NULL, '2022/07/07', 'alchemy', true, NULL, 408, 456, NULL)");
            statement.executeUpdate("insert into Sets values('PLG22', 'Love Your LGS 2022', NULL, '2022/07/01', 'promo', false, NULL, 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('PCLB', 'Battle for Baldur''s Gate Promos', NULL, '2022/06/10', 'promo', false, 'CLB', 104, 108, NULL)");
            statement.executeUpdate("insert into Sets values('MCLB', 'Commander Legends: Battle for Baldur''s Gate Minigames', 'MCLB', '2022/06/10', 'minigame', false, 'CLB', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ACLB', 'Battle for Baldur''s Gate Art Series', 'ACLB', '2022/06/10', 'memorabilia', false, 'CLB', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('OCLB', 'Battle for Baldur''s Gate Oversized Cards', 'OCLB', '2022/06/10', 'memorabilia', false, 'CLB', 1, 0, NULL)");
            statement.executeUpdate("insert into Sets values('CLB', 'Commander Legends: Battle for Baldur''s Gate', 'TCLB', '2022/06/10', 'draft_innovation', false, NULL, 936, 361, NULL)");
            statement.executeUpdate("insert into Sets values('YSNC', 'Alchemy: New Capenna', NULL, '2022/06/02', 'alchemy', true, 'SNC', 30, 30, NULL)");
            statement.executeUpdate("insert into Sets values('PTSNC', 'Streets of New Capenna Southeast Asia Tokens', 'PTSNC', '2022/04/30', 'promo', false, 'PSNC', 6, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PNCC', 'New Capenna Commander Promos', NULL, '2022/04/29', 'promo', false, 'NCC', 75, 76, NULL)");
            statement.executeUpdate("insert into Sets values('SNC', 'Streets of New Capenna', 'TSNC', '2022/04/29', 'expansion', false, NULL, 469, 281, NULL)");
            statement.executeUpdate("insert into Sets values('NCC', 'New Capenna Commander', 'TNCC', '2022/04/29', 'commander', false, 'SNC', 447, 93, NULL)");
            statement.executeUpdate("insert into Sets values('PSNC', 'Streets of New Capenna Promos', NULL, '2022/04/29', 'promo', false, 'SNC', 161, 161, NULL)");
            statement.executeUpdate("insert into Sets values('MSNC', 'Streets of New Capenna Minigames', 'MSNC', '2022/04/29', 'minigame', false, 'SNC', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ASNC', 'New Capenna Art Series', 'ASNC', '2022/04/29', 'memorabilia', false, 'SNC', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('GDY', 'Game Day Promos', NULL, '2022/04/08', 'promo', false, NULL, 9, 9, NULL)");
            statement.executeUpdate("insert into Sets values('Q07', 'Challenger Decks 2022', 'Q07', '2022/04/01', 'box', false, NULL, 1, 0, NULL)");
            statement.executeUpdate("insert into Sets values('YNEO', 'Alchemy: Kamigawa', NULL, '2022/03/17', 'alchemy', true, 'NEO', 30, 30, NULL)");
            statement.executeUpdate("insert into Sets values('PW22', 'Wizards Play Network 2022', NULL, '2022/03/05', 'promo', false, NULL, 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('SLX', 'Universes Within', NULL, '2022/03/03', 'masters', false, 'SLD', 30, 32, NULL)");
            statement.executeUpdate("insert into Sets values('PL22', 'Year of the Tiger 2022', NULL, '2022/02/25', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PNEO', 'Kamigawa: Neon Dynasty Promos', NULL, '2022/02/18', 'promo', false, 'NEO', 148, 157, NULL)");
            statement.executeUpdate("insert into Sets values('MNEO', 'Kamigawa: Neon Dynasty Minigames', 'MNEO', '2022/02/18', 'minigame', false, 'NEO', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('SNEO', 'Kamigawa: Neon Dynasty Substitute Cards', 'SNEO', '2022/02/18', 'token', false, 'NEO', 9, 0, NULL)");
            statement.executeUpdate("insert into Sets values('NEO', 'Kamigawa: Neon Dynasty', 'TNEO', '2022/02/18', 'expansion', false, NULL, 515, 302, NULL)");
            statement.executeUpdate("insert into Sets values('NEC', 'Neon Dynasty Commander', 'TNEC', '2022/02/18', 'commander', false, 'NEO', 179, 38, NULL)");
            statement.executeUpdate("insert into Sets values('ANEO', 'Neon Dynasty Art Series', 'ANEO', '2022/02/18', 'memorabilia', false, 'NEO', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('CC2', 'Commander Collection: Black', 'CC2', '2022/01/28', 'arsenal', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('DBL', 'Innistrad: Double Feature', NULL, '2022/01/28', 'draft_innovation', false, NULL, 535, 534, NULL)");
            statement.executeUpdate("insert into Sets values('P22', 'Judge Gift Cards 2022', NULL, '2022/01/01', 'promo', false, NULL, 10, 11, NULL)");
            statement.executeUpdate("insert into Sets values('YMID', 'Alchemy: Innistrad', NULL, '2021/12/09', 'alchemy', true, NULL, 63, 64, NULL)");
            statement.executeUpdate("insert into Sets values('PVOW', 'Innistrad: Crimson Vow Promos', NULL, '2021/11/19', 'promo', false, 'VOW', 121, 137, NULL)");
            statement.executeUpdate("insert into Sets values('OVOC', 'Crimson Vow Commander Display Commanders', NULL, '2021/11/19', 'memorabilia', false, 'VOC', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('MVOW', 'Innistrad: Crimson Vow Minigames', 'MVOW', '2021/11/19', 'minigame', false, 'VOW', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('SVOW', 'Innistrad: Crimson Vow Substitute Cards', 'SVOW', '2021/11/19', 'token', false, 'VOW', 9, 0, NULL)");
            statement.executeUpdate("insert into Sets values('VOC', 'Crimson Vow Commander', 'TVOC', '2021/11/19', 'commander', false, 'VOW', 188, 38, NULL)");
            statement.executeUpdate("insert into Sets values('VOW', 'Innistrad: Crimson Vow', 'TVOW', '2021/11/19', 'expansion', false, NULL, 412, 277, NULL)");
            statement.executeUpdate("insert into Sets values('AVOW', 'Crimson Vow Art Series', 'AVOW', '2021/11/19', 'memorabilia', false, 'VOW', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('Q06', 'Pioneer Challenger Decks 2021', NULL, '2021/10/15', 'box', false, NULL, 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('SMID', 'Innistrad: Midnight Hunt Substitute Cards', 'SMID', '2021/09/24', 'token', false, 'MID', 9, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PMID', 'Innistrad: Midnight Hunt Promos', NULL, '2021/09/24', 'promo', false, 'MID', 155, 171, NULL)");
            statement.executeUpdate("insert into Sets values('OMIC', 'Midnight Hunt Commander Display Commanders', NULL, '2021/09/24', 'memorabilia', false, 'MIC', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('MIC', 'Midnight Hunt Commander', 'TMIC', '2021/09/24', 'commander', false, 'MID', 187, 38, NULL)");
            statement.executeUpdate("insert into Sets values('AMID', 'Midnight Hunt Art Series', 'AMID', '2021/09/24', 'memorabilia', false, 'MID', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('MID', 'Innistrad: Midnight Hunt', 'TMID', '2021/09/24', 'expansion', false, NULL, 392, 277, NULL)");
            statement.executeUpdate("insert into Sets values('MMID', 'Innistrad: Midnight Hunt Minigames', 'MMID', '2021/09/24', 'minigame', false, 'MID', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('J21', 'Jumpstart: Historic Horizons', NULL, '2021/08/26', 'draft_innovation', true, NULL, 389, 388, NULL)");
            statement.executeUpdate("insert into Sets values('CMB2', 'Mystery Booster Playtest Cards 2021', NULL, '2021/08/20', 'funny', false, NULL, 121, 121, NULL)");
            statement.executeUpdate("insert into Sets values('PH20', '2020 Heroes of the Realm', NULL, '2021/08/01', 'funny', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('PAFR', 'Adventures in the Forgotten Realms Promos', NULL, '2021/07/23', 'promo', false, 'AFR', 241, 241, NULL)");
            statement.executeUpdate("insert into Sets values('OAFC', 'Forgotten Realms Commander Display Commanders', NULL, '2021/07/23', 'memorabilia', false, 'AFC', 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('OAFR', 'Forgotten Realms Oversized Cards', 'OAFR', '2021/07/23', 'memorabilia', false, 'AFR', 3, 0, NULL)");
            statement.executeUpdate("insert into Sets values('AFC', 'Forgotten Realms Commander', 'TAFC', '2021/07/23', 'commander', false, 'AFR', 331, 62, NULL)");
            statement.executeUpdate("insert into Sets values('AAFR', 'Adventures in the Forgotten Realms Art Series', 'AAFR', '2021/07/23', 'memorabilia', false, 'AFR', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('MAFR', 'Adventures in the Forgotten Realms Minigames', 'MAFR', '2021/07/23', 'minigame', false, 'AFR', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('AFR', 'Adventures in the Forgotten Realms', 'TAFR', '2021/07/23', 'expansion', false, NULL, 402, 281, NULL)");
            statement.executeUpdate("insert into Sets values('PLG21', 'Love Your LGS 2021', NULL, '2021/06/22', 'promo', false, NULL, 11, 5, NULL)");
            statement.executeUpdate("insert into Sets values('AMH2', 'Modern Horizons 2 Art Series', 'AMH2', '2021/06/18', 'memorabilia', false, 'MH2', 162, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PMH2', 'Modern Horizons 2 Promos', NULL, '2021/06/18', 'promo', false, 'MH2', 86, 86, NULL)");
            statement.executeUpdate("insert into Sets values('MMH2', 'Modern Horizons 2 Minigames', 'MMH2', '2021/06/18', 'minigame', false, 'MH2', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PW21', 'Wizards Play Network 2021', NULL, '2021/06/18', 'promo', false, NULL, 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('H1R', 'Modern Horizons 1 Timeshifts', NULL, '2021/06/18', 'draft_innovation', false, 'MH2', 40, 40, NULL)");
            statement.executeUpdate("insert into Sets values('MH2', 'Modern Horizons 2', 'TMH2', '2021/06/18', 'draft_innovation', false, NULL, 492, 303, NULL)");
            statement.executeUpdate("insert into Sets values('HA5', 'Historic Anthology 5', NULL, '2021/05/27', 'box', true, NULL, 25, 25, NULL)");
            statement.executeUpdate("insert into Sets values('PSTX', 'Strixhaven: School of Mages Promos', NULL, '2021/04/23', 'promo', false, 'STX', 164, 180, NULL)");
            statement.executeUpdate("insert into Sets values('SSTX', 'Strixhaven: School of Mages Substitute Cards', 'SSTX', '2021/04/23', 'token', false, 'STX', 9, 0, NULL)");
            statement.executeUpdate("insert into Sets values('MSTX', 'Strixhaven: School of Mages Minigames', 'MSTX', '2021/04/23', 'minigame', false, 'STX', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('STX', 'Strixhaven: School of Mages', 'TSTX', '2021/04/23', 'expansion', false, NULL, 383, 275, NULL)");
            statement.executeUpdate("insert into Sets values('ASTX', 'Strixhaven Art Series', 'ASTX', '2021/04/23', 'memorabilia', false, 'STX', 162, 0, NULL)");
            statement.executeUpdate("insert into Sets values('OC21', 'Commander 2021 Display Commanders', NULL, '2021/04/23', 'memorabilia', false, 'C21', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('STA', 'Strixhaven Mystical Archive', NULL, '2021/04/23', 'masterpiece', false, 'STX', 126, 63, NULL)");
            statement.executeUpdate("insert into Sets values('C21', 'Commander 2021', 'TC21', '2021/04/23', 'commander', false, NULL, 409, 81, NULL)");
            statement.executeUpdate("insert into Sets values('TSR', 'Time Spiral Remastered', 'TTSR', '2021/03/19', 'masters', false, NULL, 411, 289, NULL)");
            statement.executeUpdate("insert into Sets values('HA4', 'Historic Anthology 4', NULL, '2021/03/11', 'box', true, NULL, 25, 25, NULL)");
            statement.executeUpdate("insert into Sets values('PKHM', 'Kaldheim Promos', NULL, '2021/02/05', 'promo', false, 'KHM', 158, 1, NULL)");
            statement.executeUpdate("insert into Sets values('MKHM', 'Kaldheim Minigames', 'MKHM', '2021/02/05', 'minigame', false, 'KHM', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('SKHM', 'Kaldheim Substitute Cards', 'SKHM', '2021/02/05', 'token', false, 'KHM', 9, 0, NULL)");
            statement.executeUpdate("insert into Sets values('KHM', 'Kaldheim', 'TKHM', '2021/02/05', 'expansion', false, NULL, 407, 285, NULL)");
            statement.executeUpdate("insert into Sets values('KHC', 'Kaldheim Commander', 'TKHC', '2021/02/05', 'commander', false, 'KHM', 119, 1, NULL)");
            statement.executeUpdate("insert into Sets values('AKHM', 'Kaldheim Art Series', 'AKHM', '2021/02/05', 'memorabilia', false, 'KHM', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('PL21', 'Year of the Ox 2021', 'PL21', '2021/01/25', 'promo', false, NULL, 5, 4, NULL)");
            statement.executeUpdate("insert into Sets values('PJ21', 'Judge Gift Cards 2021', NULL, '2021/01/01', 'promo', false, NULL, 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('CC1', 'Commander Collection: Green', NULL, '2020/12/04', 'arsenal', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('PCMR', 'Commander Legends Promos', NULL, '2020/11/20', 'promo', false, 'CMR', 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('CMR', 'Commander Legends', 'TCMR', '2020/11/20', 'draft_innovation', false, NULL, 718, 361, NULL)");
            statement.executeUpdate("insert into Sets values('KLR', 'Kaladesh Remastered', NULL, '2020/11/12', 'masters', true, NULL, 302, 301, NULL)");
            statement.executeUpdate("insert into Sets values('PLST', 'The List', 'PLST', '2020/09/26', 'masters', false, NULL, 5657, 5075, NULL)");
            statement.executeUpdate("insert into Sets values('PZNR', 'Zendikar Rising Promos', NULL, '2020/09/25', 'promo', false, 'ZNR', 153, 169, NULL)");
            statement.executeUpdate("insert into Sets values('AZNR', 'Zendikar Rising Art Series', 'AZNR', '2020/09/25', 'memorabilia', false, 'ZNR', 81, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ZNR', 'Zendikar Rising', 'TZNR', '2020/09/25', 'expansion', false, NULL, 392, 280, NULL)");
            statement.executeUpdate("insert into Sets values('SZNR', 'Zendikar Rising Substitute Cards', 'SZNR', '2020/09/25', 'token', false, 'ZNR', 9, 0, NULL)");
            statement.executeUpdate("insert into Sets values('ZNC', 'Zendikar Rising Commander', 'TZNC', '2020/09/25', 'commander', false, 'ZNR', 142, 1, NULL)");
            statement.executeUpdate("insert into Sets values('MZNR', 'Zendikar Rising Minigames', 'MZNR', '2020/09/25', 'minigame', false, 'ZNR', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('ZNE', 'Zendikar Rising Expeditions', NULL, '2020/09/25', 'masterpiece', false, 'ZNR', 30, 30, NULL)");
            statement.executeUpdate("insert into Sets values('ANB', 'Arena Beginner Set', NULL, '2020/08/13', 'starter', true, NULL, 118, 116, NULL)");
            statement.executeUpdate("insert into Sets values('AKR', 'Amonkhet Remastered', NULL, '2020/08/13', 'masters', true, NULL, 339, 338, NULL)");
            statement.executeUpdate("insert into Sets values('2XM', 'Double Masters', 'T2XM', '2020/08/07', 'masters', false, NULL, 384, 332, NULL)");
            statement.executeUpdate("insert into Sets values('PH19', '2019 Heroes of the Realm', NULL, '2020/08/01', 'funny', false, NULL, 7, 9, NULL)");
            statement.executeUpdate("insert into Sets values('AJMP', 'Jumpstart Arena Exclusives', NULL, '2020/07/17', 'starter', true, 'JMP', 18, 18, NULL)");
            statement.executeUpdate("insert into Sets values('JMP', 'Jumpstart', NULL, '2020/07/17', 'draft_innovation', false, NULL, 496, 78, NULL)");
            statement.executeUpdate("insert into Sets values('PM21', 'Core Set 2021 Promos', NULL, '2020/07/03', 'promo', false, 'M21', 137, 137, NULL)");
            statement.executeUpdate("insert into Sets values('M21', 'Core Set 2021', 'TM21', '2020/07/03', 'core', false, NULL, 397, 274, NULL)");
            statement.executeUpdate("insert into Sets values('SS3', 'Signature Spellbook: Chandra', NULL, '2020/06/26', 'spellbook', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('FJMP', 'Jumpstart Front Cards', 'FJMP', '2020/06/18', 'memorabilia', false, 'JMP', 46, 0, NULL)");
            statement.executeUpdate("insert into Sets values('SLU', 'Secret Lair: Ultimate Edition', NULL, '2020/05/29', 'box', false, 'SLD', 16, 1, NULL)");
            statement.executeUpdate("insert into Sets values('HA3', 'Historic Anthology 3', NULL, '2020/05/21', 'box', true, NULL, 27, 27, NULL)");
            statement.executeUpdate("insert into Sets values('PLG20', 'Love Your LGS 2020', NULL, '2020/05/18', 'promo', false, NULL, 2, 1, NULL)");
            statement.executeUpdate("insert into Sets values('PIKO', 'Ikoria: Lair of Behemoths Promos', NULL, '2020/04/24', 'promo', false, 'IKO', 137, 137, NULL)");
            statement.executeUpdate("insert into Sets values('IKO', 'Ikoria: Lair of Behemoths', 'TIKO', '2020/04/24', 'expansion', false, NULL, 389, 274, NULL)");
            statement.executeUpdate("insert into Sets values('OC20', 'Commander 2020 Oversized', NULL, '2020/04/17', 'memorabilia', false, 'C20', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('C20', 'Commander 2020', 'TC20', '2020/04/17', 'commander', false, NULL, 322, 322, NULL)");
            statement.executeUpdate("insert into Sets values('HA2', 'Historic Anthology 2', NULL, '2020/03/12', 'box', true, NULL, 25, 25, NULL)");
            statement.executeUpdate("insert into Sets values('UND', 'Unsanctioned', 'TUND', '2020/02/29', 'funny', false, NULL, 96, 96, NULL)");
            statement.executeUpdate("insert into Sets values('PTHB', 'Theros Beyond Death Promos', NULL, '2020/01/24', 'promo', false, 'THB', 137, 137, NULL)");
            statement.executeUpdate("insert into Sets values('THB', 'Theros Beyond Death', 'TTHB', '2020/01/24', 'expansion', false, NULL, 358, 254, NULL)");
            statement.executeUpdate("insert into Sets values('J20', 'Judge Gift Cards 2020', NULL, '2020/01/01', 'promo', false, NULL, 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('PF20', 'MagicFest 2020', NULL, '2020/01/01', 'promo', false, NULL, 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('SLD', 'Secret Lair Drop', 'SLD', '2019/12/02', 'box', false, NULL, 2775, 1, NULL)");
            statement.executeUpdate("insert into Sets values('HA1', 'Historic Anthology 1', NULL, '2019/11/21', 'box', true, NULL, 20, 20, NULL)");
            statement.executeUpdate("insert into Sets values('GN2', 'Game Night 2019', 'TGN2', '2019/11/15', 'box', false, NULL, 64, 64, NULL)");
            statement.executeUpdate("insert into Sets values('CMB1', 'Mystery Booster Playtest Cards 2019', NULL, '2019/11/07', 'funny', false, NULL, 121, 121, NULL)");
            statement.executeUpdate("insert into Sets values('PTG', 'Ponies: The Galloping', NULL, '2019/10/22', 'funny', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('PELD', 'Throne of Eldraine Promos', NULL, '2019/10/04', 'promo', false, 'ELD', 138, 152, NULL)");
            statement.executeUpdate("insert into Sets values('ELD', 'Throne of Eldraine', 'TELD', '2019/10/04', 'expansion', false, NULL, 397, 269, NULL)");
            statement.executeUpdate("insert into Sets values('PWCS', 'Planeswalker Championship Promos', NULL, '2019/10/01', 'promo', false, NULL, 46, 46, NULL)");
            statement.executeUpdate("insert into Sets values('OC19', 'Commander 2019 Oversized', NULL, '2019/08/23', 'memorabilia', false, 'C19', 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('C19', 'Commander 2019', 'TC19', '2019/08/23', 'commander', false, NULL, 302, 302, NULL)");
            statement.executeUpdate("insert into Sets values('PH18', '2018 Heroes of the Realm', NULL, '2019/08/01', 'funny', false, NULL, 5, 4, NULL)");
            statement.executeUpdate("insert into Sets values('PS19', 'San Diego Comic-Con 2019', NULL, '2019/07/18', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PPP1', 'M20 Promo Packs', NULL, '2019/07/12', 'promo', false, 'M20', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PM20', 'Core Set 2020 Promos', NULL, '2019/07/12', 'promo', false, 'M20', 144, 144, NULL)");
            statement.executeUpdate("insert into Sets values('M20', 'Core Set 2020', 'TM20', '2019/07/12', 'core', false, NULL, 345, 280, NULL)");
            statement.executeUpdate("insert into Sets values('SS2', 'Signature Spellbook: Gideon', NULL, '2019/06/28', 'spellbook', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('PMH1', 'Modern Horizons Promos', NULL, '2019/06/14', 'promo', false, 'MH1', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('MH1', 'Modern Horizons', 'TMH1', '2019/06/14', 'draft_innovation', false, NULL, 255, 254, NULL)");
            statement.executeUpdate("insert into Sets values('AMH1', 'Modern Horizons Art Series', 'AMH1', '2019/06/14', 'memorabilia', false, 'MH1', 54, 54, NULL)");
            statement.executeUpdate("insert into Sets values('PWAR', 'War of the Spark Promos', NULL, '2019/05/03', 'promo', false, 'WAR', 176, 176, NULL)");
            statement.executeUpdate("insert into Sets values('WAR', 'War of the Spark', 'TWAR', '2019/05/03', 'expansion', false, NULL, 311, 264, NULL)");
            statement.executeUpdate("insert into Sets values('J19', 'Judge Gift Cards 2019', NULL, '2019/04/10', 'promo', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('PRW2', 'RNA Ravnica Weekend', NULL, '2019/02/16', 'promo', false, 'RNA', 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('GK2', 'RNA Guild Kit', 'TGK2', '2019/02/15', 'box', false, NULL, 133, 133, NULL)");
            statement.executeUpdate("insert into Sets values('PRNA', 'Ravnica Allegiance Promos', NULL, '2019/01/25', 'promo', false, 'RNA', 81, 81, NULL)");
            statement.executeUpdate("insert into Sets values('RNA', 'Ravnica Allegiance', 'TRNA', '2019/01/25', 'expansion', false, NULL, 273, 259, NULL)");
            statement.executeUpdate("insert into Sets values('PF19', 'MagicFest 2019', NULL, '2019/01/01', 'promo', false, NULL, 7, 6, NULL)");
            statement.executeUpdate("insert into Sets values('PUMA', 'Ultimate Box Topper', NULL, '2018/12/07', 'masterpiece', false, 'UMA', 40, 40, NULL)");
            statement.executeUpdate("insert into Sets values('UMA', 'Ultimate Masters', 'TUMA', '2018/12/07', 'masters', false, NULL, 254, 254, NULL)");
            statement.executeUpdate("insert into Sets values('G18', 'M19 Gift Pack', NULL, '2018/11/16', 'box', false, 'M19', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('GNT', 'Game Night', NULL, '2018/11/16', 'box', false, NULL, 68, 68, NULL)");
            statement.executeUpdate("insert into Sets values('PRWK', 'GRN Ravnica Weekend', NULL, '2018/11/09', 'promo', false, 'GRN', 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('GK1', 'GRN Guild Kit', 'TGK1', '2018/11/02', 'box', false, NULL, 127, 127, NULL)");
            statement.executeUpdate("insert into Sets values('PGRN', 'Guilds of Ravnica Promos', NULL, '2018/10/05', 'promo', false, 'GRN', 84, 84, NULL)");
            statement.executeUpdate("insert into Sets values('GRN', 'Guilds of Ravnica', 'TGRN', '2018/10/05', 'expansion', false, NULL, 273, 259, NULL)");
            statement.executeUpdate("insert into Sets values('MED', 'Mythic Edition', 'TMED', '2018/10/05', 'masterpiece', false, NULL, 24, 24, NULL)");
            statement.executeUpdate("insert into Sets values('OC18', 'Commander 2018 Oversized', NULL, '2018/08/10', 'memorabilia', false, 'C18', 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('C18', 'Commander 2018', 'TC18', '2018/08/10', 'commander', false, NULL, 307, 307, NULL)");
            statement.executeUpdate("insert into Sets values('PH17', '2017 Heroes of the Realm', NULL, '2018/08/01', 'funny', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('PS18', 'San Diego Comic-Con 2018', NULL, '2018/07/19', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('XANA', 'Arena New Player Experience Extras', NULL, '2018/07/14', 'memorabilia', true, 'ANA', 20, 20, NULL)");
            statement.executeUpdate("insert into Sets values('ANA', 'Arena New Player Experience', NULL, '2018/07/14', 'starter', true, NULL, 49, 49, NULL)");
            statement.executeUpdate("insert into Sets values('PANA', 'MTG Arena Promos', NULL, '2018/07/14', 'promo', true, NULL, 83, 3, NULL)");
            statement.executeUpdate("insert into Sets values('OANA', 'Arena New Player Experience Cards', NULL, '2018/07/14', 'starter', true, 'ANA', 14, 14, NULL)");
            statement.executeUpdate("insert into Sets values('PM19', 'Core Set 2019 Promos', NULL, '2018/07/13', 'promo', false, 'M19', 95, 95, NULL)");
            statement.executeUpdate("insert into Sets values('M19', 'Core Set 2019', 'TM19', '2018/07/13', 'core', false, NULL, 314, 280, NULL)");
            statement.executeUpdate("insert into Sets values('PSS3', 'M19 Standard Showdown', NULL, '2018/07/13', 'promo', false, 'M19', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('GS1', 'Global Series Jiang Yanggu & Mu Yanling', 'GS1', '2018/06/22', 'duel_deck', false, NULL, 40, 40, NULL)");
            statement.executeUpdate("insert into Sets values('SS1', 'Signature Spellbook: Jace', NULL, '2018/06/15', 'spellbook', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('PBBD', 'Battlebond Promos', NULL, '2018/06/08', 'promo', false, 'BBD', 22, 22, NULL)");
            statement.executeUpdate("insert into Sets values('BBD', 'Battlebond', 'TBBD', '2018/06/08', 'draft_innovation', false, NULL, 256, 254, NULL)");
            statement.executeUpdate("insert into Sets values('CM2', 'Commander Anthology Volume II', 'TCM2', '2018/06/08', 'commander', false, NULL, 312, 312, NULL)");
            statement.executeUpdate("insert into Sets values('PDOM', 'Dominaria Promos', NULL, '2018/04/27', 'promo', false, 'DOM', 118, 118, NULL)");
            statement.executeUpdate("insert into Sets values('DOM', 'Dominaria', 'TDOM', '2018/04/27', 'expansion', false, NULL, 280, 269, NULL)");
            statement.executeUpdate("insert into Sets values('DDU', 'Duel Decks: Elves vs. Inventors', 'TDDU', '2018/04/06', 'duel_deck', false, NULL, 76, 76, NULL)");
            statement.executeUpdate("insert into Sets values('A25', 'Masters 25', 'TA25', '2018/03/16', 'masters', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('PLNY', 'Lunar New Year 2018', 'PLNY', '2018/02/16', 'promo', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('PNAT', 'Nationals Promos', NULL, '2018/01/25', 'promo', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('PRIX', 'Rivals of Ixalan Promos', NULL, '2018/01/19', 'promo', false, 'RIX', 98, 98, NULL)");
            statement.executeUpdate("insert into Sets values('RIX', 'Rivals of Ixalan', 'TRIX', '2018/01/19', 'expansion', false, NULL, 205, 196, NULL)");
            statement.executeUpdate("insert into Sets values('J18', 'Judge Gift Cards 2018', NULL, '2018/01/01', 'promo', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('F18', 'Friday Night Magic 2018', 'F18', '2017/12/27', 'promo', false, NULL, 3, 12, NULL)");
            statement.executeUpdate("insert into Sets values('PUST', 'Unstable Promos', NULL, '2017/12/08', 'promo', false, 'UST', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('UST', 'Unstable', 'TUST', '2017/12/08', 'funny', false, NULL, 268, 216, NULL)");
            statement.executeUpdate("insert into Sets values('V17', 'From the Vault: Transform', NULL, '2017/11/24', 'from_the_vault', false, NULL, 16, 15, NULL)");
            statement.executeUpdate("insert into Sets values('E02', 'Explorers of Ixalan', 'E02', '2017/11/24', 'box', false, NULL, 47, 47, NULL)");
            statement.executeUpdate("insert into Sets values('PXTC', 'XLN Treasure Chest', NULL, '2017/11/24', 'promo', false, 'XLN', 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('IMA', 'Iconic Masters', 'TIMA', '2017/11/17', 'masters', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('DDT', 'Duel Decks: Merfolk vs. Goblins', 'TDDT', '2017/10/24', 'duel_deck', false, NULL, 63, 63, NULL)");
            statement.executeUpdate("insert into Sets values('G17', '2017 Gift Pack', NULL, '2017/10/20', 'box', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PXLN', 'Ixalan Promos', NULL, '2017/09/29', 'promo', false, 'XLN', 120, 120, NULL)");
            statement.executeUpdate("insert into Sets values('XLN', 'Ixalan', 'TXLN', '2017/09/29', 'expansion', false, NULL, 289, 279, NULL)");
            statement.executeUpdate("insert into Sets values('PSS2', 'XLN Standard Showdown', NULL, '2017/09/29', 'promo', false, 'XLN', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('H17', 'HasCon 2017', 'H17', '2017/09/20', 'funny', false, NULL, 3, 4, NULL)");
            statement.executeUpdate("insert into Sets values('PHTR', '2016 Heroes of the Realm', NULL, '2017/09/20', 'funny', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('OC17', 'Commander 2017 Oversized', NULL, '2017/08/25', 'memorabilia', false, 'C17', 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('C17', 'Commander 2017', 'TC17', '2017/08/25', 'commander', false, NULL, 309, 309, NULL)");
            statement.executeUpdate("insert into Sets values('PS17', 'San Diego Comic-Con 2017', NULL, '2017/07/20', 'promo', false, NULL, 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('PHOU', 'Hour of Devastation Promos', NULL, '2017/07/14', 'promo', false, 'HOU', 62, 62, NULL)");
            statement.executeUpdate("insert into Sets values('HOU', 'Hour of Devastation', 'THOU', '2017/07/14', 'expansion', false, NULL, 209, 199, NULL)");
            statement.executeUpdate("insert into Sets values('OE01', 'Archenemy: Nicol Bolas Schemes', NULL, '2017/06/16', 'archenemy', false, 'E01', 20, 20, NULL)");
            statement.executeUpdate("insert into Sets values('E01', 'Archenemy: Nicol Bolas', 'TE01', '2017/06/16', 'archenemy', false, NULL, 106, 106, NULL)");
            statement.executeUpdate("insert into Sets values('CMA', 'Commander Anthology', 'TCMA', '2017/06/09', 'commander', false, NULL, 320, 320, NULL)");
            statement.executeUpdate("insert into Sets values('PAKH', 'Amonkhet Promos', NULL, '2017/04/28', 'promo', false, 'AKH', 77, 77, NULL)");
            statement.executeUpdate("insert into Sets values('MP2', 'Amonkhet Invocations', NULL, '2017/04/28', 'masterpiece', false, 'AKH', 54, 54, NULL)");
            statement.executeUpdate("insert into Sets values('AKH', 'Amonkhet', 'TAKH', '2017/04/28', 'expansion', false, NULL, 287, 269, NULL)");
            statement.executeUpdate("insert into Sets values('W17', 'Welcome Deck 2017', NULL, '2017/04/15', 'starter', false, NULL, 30, 30, NULL)");
            statement.executeUpdate("insert into Sets values('DDS', 'Duel Decks: Mind vs. Might', 'TDDS', '2017/03/31', 'duel_deck', false, NULL, 65, 65, NULL)");
            statement.executeUpdate("insert into Sets values('MM3', 'Modern Masters 2017', 'TMM3', '2017/03/17', 'masters', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('PAER', 'Aether Revolt Promos', NULL, '2017/01/20', 'promo', false, 'AER', 65, 65, NULL)");
            statement.executeUpdate("insert into Sets values('AER', 'Aether Revolt', 'TAER', '2017/01/20', 'expansion', false, NULL, 197, 184, NULL)");
            statement.executeUpdate("insert into Sets values('L17', 'League Tokens 2017', 'L17', '2017/01/01', 'token', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('F17', 'Friday Night Magic 2017', 'F17', '2017/01/01', 'promo', false, NULL, 9, 12, NULL)");
            statement.executeUpdate("insert into Sets values('J17', 'Judge Gift Cards 2017', NULL, '2017/01/01', 'promo', false, NULL, 9, 8, NULL)");
            statement.executeUpdate("insert into Sets values('PCA', 'Planechase Anthology', 'TPCA', '2016/11/25', 'planechase', false, NULL, 156, 156, NULL)");
            statement.executeUpdate("insert into Sets values('OPCA', 'Planechase Anthology Planes', NULL, '2016/11/25', 'planechase', false, 'PCA', 86, 86, NULL)");
            statement.executeUpdate("insert into Sets values('PZ2', 'Treasure Chest', NULL, '2016/11/16', 'treasure_chest', true, NULL, 270, 73, NULL)");
            statement.executeUpdate("insert into Sets values('OC16', 'Commander 2016 Oversized', NULL, '2016/11/11', 'memorabilia', false, 'C16', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('C16', 'Commander 2016', 'TC16', '2016/11/11', 'commander', false, NULL, 351, 351, NULL)");
            statement.executeUpdate("insert into Sets values('PS16', 'San Diego Comic-Con 2016', NULL, '2016/10/01', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PKLD', 'Kaladesh Promos', NULL, '2016/09/30', 'promo', false, 'KLD', 83, 83, NULL)");
            statement.executeUpdate("insert into Sets values('KLD', 'Kaladesh', 'TKLD', '2016/09/30', 'expansion', false, NULL, 278, 264, NULL)");
            statement.executeUpdate("insert into Sets values('MPS', 'Kaladesh Inventions', NULL, '2016/09/30', 'masterpiece', false, 'KLD', 54, 54, NULL)");
            statement.executeUpdate("insert into Sets values('DDR', 'Duel Decks: Nissa vs. Ob Nixilis', 'DDR', '2016/09/02', 'duel_deck', false, NULL, 70, 76, NULL)");
            statement.executeUpdate("insert into Sets values('CN2', 'Conspiracy: Take the Crown', 'TCN2', '2016/08/26', 'draft_innovation', false, NULL, 222, 221, NULL)");
            statement.executeUpdate("insert into Sets values('V16', 'From the Vault: Lore', 'V16', '2016/08/19', 'from_the_vault', false, NULL, 15, 16, NULL)");
            statement.executeUpdate("insert into Sets values('PEMN', 'Eldritch Moon Promos', 'PEMN', '2016/07/22', 'promo', false, 'EMN', 76, 77, NULL)");
            statement.executeUpdate("insert into Sets values('EMN', 'Eldritch Moon', 'TEMN', '2016/07/22', 'expansion', false, NULL, 208, 205, NULL)");
            statement.executeUpdate("insert into Sets values('EMA', 'Eternal Masters', 'TEMA', '2016/06/10', 'masters', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('PSOI', 'Shadows over Innistrad Promos', NULL, '2016/04/08', 'promo', false, 'SOI', 90, 89, NULL)");
            statement.executeUpdate("insert into Sets values('SOI', 'Shadows over Innistrad', 'TSOI', '2016/04/08', 'expansion', false, NULL, 302, 297, NULL)");
            statement.executeUpdate("insert into Sets values('W16', 'Welcome Deck 2016', NULL, '2016/04/08', 'starter', false, NULL, 16, 16, NULL)");
            statement.executeUpdate("insert into Sets values('DDQ', 'Duel Decks: Blessed vs. Cursed', 'DDQ', '2016/02/26', 'duel_deck', false, NULL, 76, 80, NULL)");
            statement.executeUpdate("insert into Sets values('POGW', 'Oath of the Gatewatch Promos', NULL, '2016/01/22', 'promo', false, 'OGW', 65, 65, NULL)");
            statement.executeUpdate("insert into Sets values('OGW', 'Oath of the Gatewatch', 'TOGW', '2016/01/22', 'expansion', false, NULL, 187, 184, NULL)");
            statement.executeUpdate("insert into Sets values('L16', 'League Tokens 2016', 'L16', '2016/01/01', 'token', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('F16', 'Friday Night Magic 2016', NULL, '2016/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('J16', 'Judge Gift Cards 2016', NULL, '2016/01/01', 'promo', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('PZ1', 'Legendary Cube Prize Pack', NULL, '2015/11/18', 'treasure_chest', true, NULL, 149, 149, NULL)");
            statement.executeUpdate("insert into Sets values('C15', 'Commander 2015', 'TC15', '2015/11/13', 'commander', false, NULL, 342, 342, NULL)");
            statement.executeUpdate("insert into Sets values('OC15', 'Commander 2015 Oversized', NULL, '2015/11/13', 'memorabilia', false, 'C15', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PBFZ', 'Battle for Zendikar Promos', NULL, '2015/10/02', 'promo', false, 'BFZ', 90, 90, NULL)");
            statement.executeUpdate("insert into Sets values('PSS1', 'BFZ Standard Series', NULL, '2015/10/02', 'promo', false, 'BFZ', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('BFZ', 'Battle for Zendikar', 'TBFZ', '2015/10/02', 'expansion', false, NULL, 299, 274, NULL)");
            statement.executeUpdate("insert into Sets values('EXP', 'Zendikar Expeditions', NULL, '2015/10/02', 'masterpiece', false, 'BFZ', 45, 45, NULL)");
            statement.executeUpdate("insert into Sets values('DDP', 'Duel Decks: Zendikar vs. Eldrazi', 'DDP', '2015/08/28', 'duel_deck', false, NULL, 75, 80, NULL)");
            statement.executeUpdate("insert into Sets values('V15', 'From the Vault: Angels', NULL, '2015/08/21', 'from_the_vault', false, NULL, 15, 15, NULL)");
            statement.executeUpdate("insert into Sets values('CP3', 'Magic Origins Clash Pack', NULL, '2015/07/17', 'starter', false, 'ORI', 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('PORI', 'Magic Origins Promos', NULL, '2015/07/17', 'promo', false, 'ORI', 54, 54, NULL)");
            statement.executeUpdate("insert into Sets values('ORI', 'Magic Origins', 'TORI', '2015/07/17', 'core', false, NULL, 288, 272, NULL)");
            statement.executeUpdate("insert into Sets values('PS15', 'San Diego Comic-Con 2015', NULL, '2015/07/09', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('MM2', 'Modern Masters 2015', 'TMM2', '2015/05/22', 'masters', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('TPR', 'Tempest Remastered', NULL, '2015/05/06', 'masters', true, NULL, 269, 269, NULL)");
            statement.executeUpdate("insert into Sets values('PTKDF', 'Tarkir Dragonfury', NULL, '2015/04/03', 'promo', false, 'DTK', 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('PDTK', 'Dragons of Tarkir Promos', NULL, '2015/03/27', 'promo', false, 'DTK', 51, 51, NULL)");
            statement.executeUpdate("insert into Sets values('DTK', 'Dragons of Tarkir', 'TDTK', '2015/03/27', 'expansion', false, NULL, 264, 264, NULL)");
            statement.executeUpdate("insert into Sets values('DDO', 'Duel Decks: Elspeth vs. Kiora', 'DDO', '2015/02/27', 'duel_deck', false, NULL, 65, 67, NULL)");
            statement.executeUpdate("insert into Sets values('PFRF', 'Fate Reforged Promos', NULL, '2015/01/23', 'promo', false, 'FRF', 43, 43, NULL)");
            statement.executeUpdate("insert into Sets values('CP2', 'Fate Reforged Clash Pack', NULL, '2015/01/23', 'starter', false, 'FRF', 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('FRF', 'Fate Reforged', 'TFRF', '2015/01/23', 'expansion', false, NULL, 191, 185, NULL)");
            statement.executeUpdate("insert into Sets values('UGIN', 'Ugin''s Fate', NULL, '2015/01/17', 'promo', false, 'FRF', 26, 185, NULL)");
            statement.executeUpdate("insert into Sets values('L15', 'League Tokens 2015', 'L15', '2015/01/01', 'token', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('F15', 'Friday Night Magic 2015', NULL, '2015/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('J15', 'Judge Gift Cards 2015', NULL, '2015/01/01', 'promo', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('JVC', 'Duel Decks Anthology: Jace vs. Chandra', 'TJVC', '2014/12/05', 'duel_deck', false, NULL, 62, 62, NULL)");
            statement.executeUpdate("insert into Sets values('GVL', 'Duel Decks Anthology: Garruk vs. Liliana', 'TGVL', '2014/12/05', 'duel_deck', false, NULL, 63, 63, NULL)");
            statement.executeUpdate("insert into Sets values('DVD', 'Duel Decks Anthology: Divine vs. Demonic', 'TDVD', '2014/12/05', 'duel_deck', false, NULL, 62, 62, NULL)");
            statement.executeUpdate("insert into Sets values('EVG', 'Duel Decks Anthology: Elves vs. Goblins', 'TEVG', '2014/12/05', 'duel_deck', false, NULL, 62, 62, NULL)");
            statement.executeUpdate("insert into Sets values('OC14', 'Commander 2014 Oversized', NULL, '2014/11/07', 'memorabilia', false, 'C14', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('C14', 'Commander 2014', 'TC14', '2014/11/07', 'commander', false, NULL, 337, 337, NULL)");
            statement.executeUpdate("insert into Sets values('PKTK', 'Khans of Tarkir Promos', NULL, '2014/09/26', 'promo', false, 'KTK', 56, 56, NULL)");
            statement.executeUpdate("insert into Sets values('KTK', 'Khans of Tarkir', 'TKTK', '2014/09/26', 'expansion', false, NULL, 293, 269, NULL)");
            statement.executeUpdate("insert into Sets values('DDN', 'Duel Decks: Speed vs. Cunning', 'DDN', '2014/09/05', 'duel_deck', false, NULL, 81, 82, NULL)");
            statement.executeUpdate("insert into Sets values('V14', 'From the Vault: Annihilation', NULL, '2014/08/22', 'from_the_vault', false, NULL, 15, 15, NULL)");
            statement.executeUpdate("insert into Sets values('CP1', 'Magic 2015 Clash Pack', NULL, '2014/07/18', 'starter', false, 'M15', 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('M15', 'Magic 2015', 'TM15', '2014/07/18', 'core', false, NULL, 284, 269, NULL)");
            statement.executeUpdate("insert into Sets values('PM15', 'Magic 2015 Promos', NULL, '2014/07/17', 'promo', false, 'M15', 14, 14, NULL)");
            statement.executeUpdate("insert into Sets values('PPC1', 'M15 Prerelease Challenge', 'PPC1', '2014/07/12', 'memorabilia', false, 'M15', 1, 2, NULL)");
            statement.executeUpdate("insert into Sets values('PDP15', 'Duels of the Planeswalkers 2015 Promos', NULL, '2014/07/09', 'promo', false, NULL, 2, 269, NULL)");
            statement.executeUpdate("insert into Sets values('PS14', 'San Diego Comic-Con 2014', NULL, '2014/07/08', 'promo', false, NULL, 6, 269, NULL)");
            statement.executeUpdate("insert into Sets values('VMA', 'Vintage Masters', NULL, '2014/06/16', 'masters', true, NULL, 325, 325, NULL)");
            statement.executeUpdate("insert into Sets values('PCNS', 'Conspiracy Promos', NULL, '2014/06/06', 'promo', false, 'CNS', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('CNS', 'Conspiracy', 'TCNS', '2014/06/06', 'draft_innovation', false, NULL, 210, 210, NULL)");
            statement.executeUpdate("insert into Sets values('MD1', 'Modern Event Deck 2014', 'TMD1', '2014/05/30', 'box', false, NULL, 26, 26, NULL)");
            statement.executeUpdate("insert into Sets values('TDAG', 'Defeat a God', 'TDAG', '2014/05/25', 'memorabilia', false, 'JOU', 8, 15, NULL)");
            statement.executeUpdate("insert into Sets values('JOU', 'Journey into Nyx', 'TJOU', '2014/05/02', 'expansion', false, NULL, 165, 165, NULL)");
            statement.executeUpdate("insert into Sets values('THP3', 'Journey into Nyx Hero''s Path', 'THP3', '2014/05/02', 'memorabilia', false, 'JOU', 7, 8, NULL)");
            statement.executeUpdate("insert into Sets values('PJOU', 'Journey into Nyx Promos', NULL, '2014/04/26', 'promo', false, 'JOU', 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('DDM', 'Duel Decks: Jace vs. Vraska', 'TDDM', '2014/03/14', 'duel_deck', false, NULL, 88, 88, NULL)");
            statement.executeUpdate("insert into Sets values('TBTH', 'Battle the Horde', 'TBTH', '2014/03/01', 'memorabilia', false, 'BNG', 10, 15, NULL)");
            statement.executeUpdate("insert into Sets values('BNG', 'Born of the Gods', 'TBNG', '2014/02/07', 'expansion', false, NULL, 165, 165, NULL)");
            statement.executeUpdate("insert into Sets values('THP2', 'Born of the Gods Hero''s Path', NULL, '2014/02/07', 'memorabilia', false, 'BNG', 7, 7, NULL)");
            statement.executeUpdate("insert into Sets values('PBNG', 'Born of the Gods Promos', NULL, '2014/02/01', 'promo', false, 'BNG', 9, 9, NULL)");
            statement.executeUpdate("insert into Sets values('L14', 'League Tokens 2014', 'L14', '2014/01/01', 'token', false, NULL, 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('F14', 'Friday Night Magic 2014', NULL, '2014/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('J14', 'Judge Gift Cards 2014', NULL, '2014/01/01', 'promo', false, NULL, 14, 9, NULL)");
            statement.executeUpdate("insert into Sets values('OC13', 'Commander 2013 Oversized', NULL, '2013/11/01', 'memorabilia', false, 'C13', 15, 15, NULL)");
            statement.executeUpdate("insert into Sets values('C13', 'Commander 2013', NULL, '2013/11/01', 'commander', false, NULL, 356, 356, NULL)");
            statement.executeUpdate("insert into Sets values('TFTH', 'Face the Hydra', 'TFTH', '2013/10/19', 'memorabilia', false, 'THS', 10, 15, NULL)");
            statement.executeUpdate("insert into Sets values('THP1', 'Theros Hero''s Path', NULL, '2013/09/27', 'memorabilia', false, 'THS', 7, 7, NULL)");
            statement.executeUpdate("insert into Sets values('THS', 'Theros', 'TTHS', '2013/09/27', 'expansion', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('PTHS', 'Theros Promos', NULL, '2013/09/21', 'promo', false, 'THS', 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('DDL', 'Duel Decks: Heroes vs. Monsters', 'TDDL', '2013/09/06', 'duel_deck', false, NULL, 81, 81, NULL)");
            statement.executeUpdate("insert into Sets values('V13', 'From the Vault: Twenty', NULL, '2013/08/23', 'from_the_vault', false, NULL, 20, 20, NULL)");
            statement.executeUpdate("insert into Sets values('M14', 'Magic 2014', 'TM14', '2013/07/19', 'core', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('PM14', 'Magic 2014 Promos', NULL, '2013/07/18', 'promo', false, 'M14', 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('PSDC', 'San Diego Comic-Con 2013', NULL, '2013/07/18', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('MMA', 'Modern Masters', 'TMMA', '2013/06/07', 'masters', false, NULL, 229, 229, NULL)");
            statement.executeUpdate("insert into Sets values('DGM', 'Dragon''s Maze', 'TDGM', '2013/05/03', 'expansion', false, NULL, 156, 156, NULL)");
            statement.executeUpdate("insert into Sets values('PDGM', 'Dragon''s Maze Promos', NULL, '2013/04/27', 'promo', false, 'DGM', 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('WMC', 'World Magic Cup Qualifiers', NULL, '2013/04/06', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('DDK', 'Duel Decks: Sorin vs. Tibalt', 'TDDK', '2013/03/15', 'duel_deck', false, NULL, 80, 80, NULL)");
            statement.executeUpdate("insert into Sets values('GTC', 'Gatecrash', 'TGTC', '2013/02/01', 'expansion', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('PGTC', 'Gatecrash Promos', NULL, '2013/01/26', 'promo', false, 'GTC', 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('PDP14', 'Duels of the Planeswalkers 2014 Promos', NULL, '2013/01/01', 'promo', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('L13', 'League Tokens 2013', 'L13', '2013/01/01', 'token', false, NULL, 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('J13', 'Judge Gift Cards 2013', 'J13', '2013/01/01', 'promo', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('F13', 'Friday Night Magic 2013', NULL, '2013/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('OCM1', 'Commander''s Arsenal Oversized', NULL, '2012/11/02', 'memorabilia', false, 'CM1', 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('CM1', 'Commander''s Arsenal', NULL, '2012/11/02', 'arsenal', false, NULL, 18, 18, NULL)");
            statement.executeUpdate("insert into Sets values('RTR', 'Return to Ravnica', 'TRTR', '2012/10/05', 'expansion', false, NULL, 274, 274, NULL)");
            statement.executeUpdate("insert into Sets values('PRTR', 'Return to Ravnica Promos', NULL, '2012/10/05', 'promo', false, 'RTR', 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('DDJ', 'Duel Decks: Izzet vs. Golgari', 'TDDJ', '2012/09/07', 'duel_deck', false, NULL, 90, 90, NULL)");
            statement.executeUpdate("insert into Sets values('V12', 'From the Vault: Realms', NULL, '2012/08/31', 'from_the_vault', false, NULL, 15, 15, NULL)");
            statement.executeUpdate("insert into Sets values('M13', 'Magic 2013', 'TM13', '2012/07/13', 'core', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('PM13', 'Magic 2013 Promos', NULL, '2012/07/12', 'promo', false, 'M13', 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('OPC2', 'Planechase 2012 Planes', NULL, '2012/06/01', 'planechase', false, 'PC2', 40, 40, NULL)");
            statement.executeUpdate("insert into Sets values('PC2', 'Planechase 2012', NULL, '2012/06/01', 'planechase', false, NULL, 156, 156, NULL)");
            statement.executeUpdate("insert into Sets values('AVR', 'Avacyn Restored', 'TAVR', '2012/05/04', 'expansion', false, NULL, 244, 244, NULL)");
            statement.executeUpdate("insert into Sets values('PHEL', 'Open the Helvault', 'PHEL', '2012/04/28', 'memorabilia', false, 'AVR', 5, 6, NULL)");
            statement.executeUpdate("insert into Sets values('PAVR', 'Avacyn Restored Promos', NULL, '2012/04/28', 'promo', false, 'AVR', 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('DDI', 'Duel Decks: Venser vs. Koth', 'TDDI', '2012/03/30', 'duel_deck', false, NULL, 77, 77, NULL)");
            statement.executeUpdate("insert into Sets values('DKA', 'Dark Ascension', 'TDKA', '2012/02/03', 'expansion', false, NULL, 158, 158, NULL)");
            statement.executeUpdate("insert into Sets values('PDKA', 'Dark Ascension Promos', NULL, '2012/01/28', 'promo', false, 'DKA', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PW12', 'Wizards Play Network 2012', NULL, '2012/01/01', 'promo', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('PDP13', 'Duels of the Planeswalkers 2013 Promos', NULL, '2012/01/01', 'promo', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('PIDW', 'IDW Comics Inserts', NULL, '2012/01/01', 'promo', false, NULL, 17, 9, NULL)");
            statement.executeUpdate("insert into Sets values('L12', 'League Tokens 2012', 'L12', '2012/01/01', 'token', false, NULL, 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('F12', 'Friday Night Magic 2012', 'F12', '2012/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('J12', 'Judge Gift Cards 2012', 'J12', '2012/01/01', 'promo', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('PD3', 'Premium Deck Series: Graveborn', NULL, '2011/11/18', 'premium_deck', false, NULL, 30, 50, NULL)");
            statement.executeUpdate("insert into Sets values('ISD', 'Innistrad', 'TISD', '2011/09/30', 'expansion', false, NULL, 264, 264, NULL)");
            statement.executeUpdate("insert into Sets values('PISD', 'Innistrad Promos', NULL, '2011/09/24', 'promo', false, 'ISD', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('DDH', 'Duel Decks: Ajani vs. Nicol Bolas', 'TDDH', '2011/09/02', 'duel_deck', false, NULL, 80, 80, NULL)");
            statement.executeUpdate("insert into Sets values('V11', 'From the Vault: Legends', NULL, '2011/08/26', 'from_the_vault', false, NULL, 15, 15, NULL)");
            statement.executeUpdate("insert into Sets values('M12', 'Magic 2012', 'TM12', '2011/07/15', 'core', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('PM12', 'Magic 2012 Promos', NULL, '2011/07/14', 'promo', false, 'M12', 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('OCMD', 'Commander 2011 Oversized', NULL, '2011/06/17', 'memorabilia', false, 'CMD', 15, 15, NULL)");
            statement.executeUpdate("insert into Sets values('PCMD', 'Commander 2011 Launch Party', NULL, '2011/06/17', 'memorabilia', false, 'CMD', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('CMD', 'Commander 2011', NULL, '2011/06/17', 'commander', false, NULL, 318, 318, NULL)");
            statement.executeUpdate("insert into Sets values('TD2', 'Duel Decks: Mirrodin Pure vs. New Phyrexia', NULL, '2011/05/14', 'duel_deck', true, NULL, 88, 88, NULL)");
            statement.executeUpdate("insert into Sets values('NPH', 'New Phyrexia', 'TNPH', '2011/05/13', 'expansion', false, NULL, 175, 175, NULL)");
            statement.executeUpdate("insert into Sets values('PNPH', 'New Phyrexia Promos', NULL, '2011/05/12', 'promo', false, 'NPH', 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('DDG', 'Duel Decks: Knights vs. Dragons', 'TDDG', '2011/04/01', 'duel_deck', false, NULL, 81, 81, NULL)");
            statement.executeUpdate("insert into Sets values('MBS', 'Mirrodin Besieged', 'TMBS', '2011/02/04', 'expansion', false, NULL, 155, 155, NULL)");
            statement.executeUpdate("insert into Sets values('PMBS', 'Mirrodin Besieged Promos', NULL, '2011/02/03', 'promo', false, 'MBS', 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('ME4', 'Masters Edition IV', NULL, '2011/01/10', 'masters', true, NULL, 269, 260, NULL)");
            statement.executeUpdate("insert into Sets values('PMPS11', 'Magic Premiere Shop 2011', NULL, '2011/01/01', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PW11', 'Wizards Play Network 2011', NULL, '2011/01/01', 'promo', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('PDP12', 'Duels of the Planeswalkers 2012 Promos', NULL, '2011/01/01', 'promo', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('G11', 'Judge Gift Cards 2011', NULL, '2011/01/01', 'promo', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('PS11', 'Salvat 2011', NULL, '2011/01/01', 'box', false, NULL, 224, 224, NULL)");
            statement.executeUpdate("insert into Sets values('OLGC', 'Legacy Championship', NULL, '2011/01/01', 'memorabilia', false, NULL, 27, 27, NULL)");
            statement.executeUpdate("insert into Sets values('P11', 'Magic Player Rewards 2011', NULL, '2011/01/01', 'promo', false, NULL, 7, 9, NULL)");
            statement.executeUpdate("insert into Sets values('F11', 'Friday Night Magic 2011', NULL, '2011/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('PD2', 'Premium Deck Series: Fire and Lightning', NULL, '2010/11/19', 'premium_deck', false, NULL, 34, 34, NULL)");
            statement.executeUpdate("insert into Sets values('TD0', 'Magic Online Theme Decks', NULL, '2010/11/08', 'box', true, NULL, 197, 197, NULL)");
            statement.executeUpdate("insert into Sets values('SOM', 'Scars of Mirrodin', 'TSOM', '2010/10/01', 'expansion', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('PSOM', 'Scars of Mirrodin Promos', NULL, '2010/09/30', 'promo', false, 'SOM', 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('DDF', 'Duel Decks: Elspeth vs. Tezzeret', 'TDDF', '2010/09/03', 'duel_deck', false, NULL, 79, 79, NULL)");
            statement.executeUpdate("insert into Sets values('V10', 'From the Vault: Relics', NULL, '2010/08/27', 'from_the_vault', false, NULL, 15, 15, NULL)");
            statement.executeUpdate("insert into Sets values('M11', 'Magic 2011', 'TM11', '2010/07/16', 'core', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('PM11', 'Magic 2011 Promos', NULL, '2010/07/15', 'promo', false, 'M11', 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('OARC', 'Archenemy Schemes', NULL, '2010/06/18', 'archenemy', false, 'ARC', 45, 45, NULL)");
            statement.executeUpdate("insert into Sets values('ARC', 'Archenemy', NULL, '2010/06/18', 'archenemy', false, NULL, 150, 150, NULL)");
            statement.executeUpdate("insert into Sets values('DPA', 'Duels of the Planeswalkers', NULL, '2010/06/04', 'box', false, NULL, 113, 113, NULL)");
            statement.executeUpdate("insert into Sets values('PROE', 'Rise of the Eldrazi Promos', NULL, '2010/04/23', 'promo', false, 'ROE', 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('ROE', 'Rise of the Eldrazi', 'TROE', '2010/04/23', 'expansion', false, NULL, 248, 248, NULL)");
            statement.executeUpdate("insert into Sets values('DDE', 'Duel Decks: Phyrexia vs. the Coalition', 'TDDE', '2010/03/19', 'duel_deck', false, NULL, 71, 71, NULL)");
            statement.executeUpdate("insert into Sets values('PWWK', 'Worldwake Promos', NULL, '2010/02/05', 'promo', false, 'WWK', 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('WWK', 'Worldwake', 'TWWK', '2010/02/05', 'expansion', false, NULL, 145, 145, NULL)");
            statement.executeUpdate("insert into Sets values('PDP10', 'Duels of the Planeswalkers 2010 Promos', NULL, '2010/01/01', 'promo', false, NULL, 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('PMPS10', 'Magic Premiere Shop 2010', NULL, '2010/01/01', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('G10', 'Judge Gift Cards 2010', NULL, '2010/01/01', 'promo', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('F10', 'Friday Night Magic 2010', NULL, '2010/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('P10', 'Magic Player Rewards 2010', NULL, '2010/01/01', 'promo', false, NULL, 13, 9, NULL)");
            statement.executeUpdate("insert into Sets values('H09', 'Premium Deck Series: Slivers', NULL, '2009/11/20', 'premium_deck', false, NULL, 41, 41, NULL)");
            statement.executeUpdate("insert into Sets values('DDD', 'Duel Decks: Garruk vs. Liliana', 'TDDD', '2009/10/30', 'duel_deck', false, NULL, 63, 63, NULL)");
            statement.executeUpdate("insert into Sets values('PZEN', 'Zendikar Promos', NULL, '2009/10/02', 'promo', false, 'ZEN', 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('ZEN', 'Zendikar', 'TZEN', '2009/10/02', 'expansion', false, NULL, 269, 249, NULL)");
            statement.executeUpdate("insert into Sets values('ME3', 'Masters Edition III', NULL, '2009/09/07', 'masters', true, NULL, 230, 230, NULL)");
            statement.executeUpdate("insert into Sets values('OHOP', 'Planechase Planes', NULL, '2009/09/04', 'planechase', false, 'HOP', 40, 40, NULL)");
            statement.executeUpdate("insert into Sets values('HOP', 'Planechase', NULL, '2009/09/04', 'planechase', false, NULL, 169, 169, NULL)");
            statement.executeUpdate("insert into Sets values('PHOP', 'Planechase Promos', NULL, '2009/09/04', 'promo', false, 'HOP', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('V09', 'From the Vault: Exiled', NULL, '2009/08/28', 'from_the_vault', false, NULL, 15, 15, NULL)");
            statement.executeUpdate("insert into Sets values('M10', 'Magic 2010', 'TM10', '2009/07/17', 'core', false, NULL, 249, 249, NULL)");
            statement.executeUpdate("insert into Sets values('PM10', 'Magic 2010 Promos', NULL, '2009/07/16', 'promo', false, 'M10', 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('PARB', 'Alara Reborn Promos', NULL, '2009/04/30', 'promo', false, 'ARB', 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('ARB', 'Alara Reborn', 'TARB', '2009/04/30', 'expansion', false, NULL, 145, 145, NULL)");
            statement.executeUpdate("insert into Sets values('DDC', 'Duel Decks: Divine vs. Demonic', 'TDDC', '2009/04/10', 'duel_deck', false, NULL, 62, 62, NULL)");
            statement.executeUpdate("insert into Sets values('PURL', 'URL/Convention Promos', NULL, '2009/02/08', 'promo', false, NULL, 18, 9, NULL)");
            statement.executeUpdate("insert into Sets values('PCON', 'Conflux Promos', NULL, '2009/02/06', 'promo', false, 'CON', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('CON', 'Conflux', 'TCON', '2009/02/06', 'expansion', false, NULL, 145, 145, NULL)");
            statement.executeUpdate("insert into Sets values('PDTP', 'Duels of the Planeswalkers 2009 Promos', NULL, '2009/01/01', 'promo', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('PMPS09', 'Magic Premiere Shop 2009', NULL, '2009/01/01', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('P09', 'Magic Player Rewards 2009', NULL, '2009/01/01', 'promo', false, NULL, 13, 7, NULL)");
            statement.executeUpdate("insert into Sets values('G09', 'Judge Gift Cards 2009', NULL, '2009/01/01', 'promo', false, NULL, 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('F09', 'Friday Night Magic 2009', NULL, '2009/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('DD2', 'Duel Decks: Jace vs. Chandra', 'TDD2', '2008/11/07', 'duel_deck', false, NULL, 64, 62, NULL)");
            statement.executeUpdate("insert into Sets values('PALA', 'Shards of Alara Promos', NULL, '2008/10/03', 'promo', false, 'ALA', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('ALA', 'Shards of Alara', 'TALA', '2008/10/03', 'expansion', false, NULL, 250, 249, NULL)");
            statement.executeUpdate("insert into Sets values('ME2', 'Masters Edition II', NULL, '2008/09/22', 'masters', true, NULL, 245, 245, NULL)");
            statement.executeUpdate("insert into Sets values('DRB', 'From the Vault: Dragons', NULL, '2008/08/29', 'from_the_vault', false, NULL, 15, 15, NULL)");
            statement.executeUpdate("insert into Sets values('PEVE', 'Eventide Promos', NULL, '2008/07/25', 'promo', false, 'EVE', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('EVE', 'Eventide', 'TEVE', '2008/07/25', 'expansion', false, NULL, 180, 180, NULL)");
            statement.executeUpdate("insert into Sets values('PSHM', 'Shadowmoor Promos', NULL, '2008/05/02', 'promo', false, 'SHM', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('SHM', 'Shadowmoor', 'TSHM', '2008/05/02', 'expansion', false, NULL, 302, 301, NULL)");
            statement.executeUpdate("insert into Sets values('P15A', '15th Anniversary Cards', NULL, '2008/04/01', 'promo', false, NULL, 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('PMOR', 'Morningtide Promos', NULL, '2008/02/01', 'promo', false, 'MOR', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('MOR', 'Morningtide', 'TMOR', '2008/02/01', 'expansion', false, NULL, 150, 150, NULL)");
            statement.executeUpdate("insert into Sets values('PMPS08', 'Magic Premiere Shop 2008', NULL, '2008/01/01', 'promo', false, NULL, 6, 5, NULL)");
            statement.executeUpdate("insert into Sets values('F08', 'Friday Night Magic 2008', NULL, '2008/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('P08', 'Magic Player Rewards 2008', NULL, '2008/01/01', 'promo', false, NULL, 7, 7, NULL)");
            statement.executeUpdate("insert into Sets values('G08', 'Judge Gift Cards 2008', NULL, '2008/01/01', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('DD1', 'Duel Decks: Elves vs. Goblins', 'TDD1', '2007/11/16', 'duel_deck', false, NULL, 62, 62, NULL)");
            statement.executeUpdate("insert into Sets values('PLRW', 'Lorwyn Promos', NULL, '2007/10/12', 'promo', false, 'LRW', 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('LRW', 'Lorwyn', 'TLRW', '2007/10/12', 'expansion', false, NULL, 301, 301, NULL)");
            statement.executeUpdate("insert into Sets values('ME1', 'Masters Edition', NULL, '2007/09/10', 'masters', true, NULL, 195, 195, NULL)");
            statement.executeUpdate("insert into Sets values('10E', 'Tenth Edition', 'T10E', '2007/07/13', 'core', false, NULL, 510, 383, NULL)");
            statement.executeUpdate("insert into Sets values('P10E', 'Tenth Edition Promos', NULL, '2007/07/13', 'promo', false, '10E', 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('PFUT', 'Future Sight Promos', NULL, '2007/05/04', 'promo', false, 'FUT', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('FUT', 'Future Sight', NULL, '2007/05/04', 'expansion', false, NULL, 180, 180, NULL)");
            statement.executeUpdate("insert into Sets values('PGPX', 'Grand Prix Promos', NULL, '2007/02/24', 'promo', false, NULL, 20, 20, NULL)");
            statement.executeUpdate("insert into Sets values('PPRO', 'Pro Tour Promos', NULL, '2007/02/09', 'promo', false, NULL, 18, 18, NULL)");
            statement.executeUpdate("insert into Sets values('PPLC', 'Planar Chaos Promos', NULL, '2007/02/02', 'promo', false, 'PLC', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('PLC', 'Planar Chaos', NULL, '2007/02/02', 'expansion', false, NULL, 165, 165, NULL)");
            statement.executeUpdate("insert into Sets values('P07', 'Magic Player Rewards 2007', NULL, '2007/01/01', 'promo', false, NULL, 7, 7, NULL)");
            statement.executeUpdate("insert into Sets values('PMPS07', 'Magic Premiere Shop 2007', NULL, '2007/01/01', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('G07', 'Judge Gift Cards 2007', NULL, '2007/01/01', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('F07', 'Friday Night Magic 2007', NULL, '2007/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('HHO', 'Happy Holidays', 'HHO', '2006/12/31', 'funny', false, NULL, 22, 21, NULL)");
            statement.executeUpdate("insert into Sets values('PTSP', 'Time Spiral Promos', NULL, '2006/10/06', 'promo', false, 'TSP', 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('TSP', 'Time Spiral', NULL, '2006/10/06', 'expansion', false, NULL, 301, 301, NULL)");
            statement.executeUpdate("insert into Sets values('TSB', 'Time Spiral Timeshifted', NULL, '2006/10/06', 'expansion', false, 'TSP', 121, 121, NULL)");
            statement.executeUpdate("insert into Sets values('PCSP', 'Coldsnap Promos', 'PCSP', '2006/07/21', 'promo', false, 'CSP', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('CSP', 'Coldsnap', NULL, '2006/07/21', 'expansion', false, NULL, 155, 155, NULL)");
            statement.executeUpdate("insert into Sets values('CST', 'Coldsnap Theme Decks', NULL, '2006/07/21', 'box', false, 'CSP', 62, 62, NULL)");
            statement.executeUpdate("insert into Sets values('PDIS', 'Dissension Promos', NULL, '2006/05/05', 'promo', false, 'DIS', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('DIS', 'Dissension', NULL, '2006/05/05', 'expansion', false, NULL, 180, 180, NULL)");
            statement.executeUpdate("insert into Sets values('PCMP', 'Champs and States', NULL, '2006/03/18', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('PGPT', 'Guildpact Promos', NULL, '2006/02/03', 'promo', false, 'GPT', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('GPT', 'Guildpact', NULL, '2006/02/03', 'expansion', false, NULL, 167, 165, NULL)");
            statement.executeUpdate("insert into Sets values('PMPS06', 'Magic Premiere Shop 2006', NULL, '2006/01/01', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PAL06', 'Arena League 2006', NULL, '2006/01/01', 'promo', false, NULL, 9, 8, NULL)");
            statement.executeUpdate("insert into Sets values('PJAS', 'Junior APAC Series', NULL, '2006/01/01', 'promo', false, NULL, 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('DCI', 'DCI Promos', NULL, '2006/01/01', 'promo', false, NULL, 80, 79, NULL)");
            statement.executeUpdate("insert into Sets values('P06', 'Magic Player Rewards 2006', NULL, '2006/01/01', 'promo', false, NULL, 7, 7, NULL)");
            statement.executeUpdate("insert into Sets values('PHUK', 'Hachette UK', NULL, '2006/01/01', 'box', false, NULL, 60, 60, NULL)");
            statement.executeUpdate("insert into Sets values('G06', 'Judge Gift Cards 2006', NULL, '2006/01/01', 'promo', false, NULL, 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('F06', 'Friday Night Magic 2006', NULL, '2006/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('P2HG', 'Two-Headed Giant Tournament', NULL, '2005/12/09', 'promo', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('PRAV', 'Ravnica: City of Guilds Promos', NULL, '2005/10/07', 'promo', false, 'RAV', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('PMPS', 'Magic Premiere Shop 2005', NULL, '2005/10/07', 'promo', false, NULL, 20, 20, NULL)");
            statement.executeUpdate("insert into Sets values('RAV', 'Ravnica: City of Guilds', NULL, '2005/10/07', 'expansion', false, NULL, 306, 306, NULL)");
            statement.executeUpdate("insert into Sets values('PSAL', 'Salvat 2005', NULL, '2005/08/22', 'box', false, NULL, 720, 720, NULL)");
            statement.executeUpdate("insert into Sets values('P9ED', 'Ninth Edition Promos', NULL, '2005/07/29', 'promo', false, '9ED', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('9ED', 'Ninth Edition', NULL, '2005/07/29', 'core', false, NULL, 710, 350, NULL)");
            statement.executeUpdate("insert into Sets values('PSOK', 'Saviors of Kamigawa Promos', NULL, '2005/06/03', 'promo', false, 'SOK', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('SOK', 'Saviors of Kamigawa', NULL, '2005/06/03', 'expansion', false, NULL, 165, 165, NULL)");
            statement.executeUpdate("insert into Sets values('PBOK', 'Betrayers of Kamigawa Promos', NULL, '2005/02/04', 'promo', false, 'BOK', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('BOK', 'Betrayers of Kamigawa', NULL, '2005/02/04', 'expansion', false, NULL, 165, 165, NULL)");
            statement.executeUpdate("insert into Sets values('PAL05', 'Arena League 2005', NULL, '2005/01/01', 'promo', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('PJSE', 'Junior Series Europe', NULL, '2005/01/01', 'promo', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('F05', 'Friday Night Magic 2005', NULL, '2005/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('P05', 'Magic Player Rewards 2005', NULL, '2005/01/01', 'promo', false, NULL, 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('G05', 'Judge Gift Cards 2005', NULL, '2005/01/01', 'promo', false, NULL, 4, 4, NULL)");
            statement.executeUpdate("insert into Sets values('PUNH', 'Unhinged Promos', NULL, '2004/11/19', 'promo', false, 'UNH', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('UNH', 'Unhinged', NULL, '2004/11/19', 'funny', false, NULL, 168, 140, NULL)");
            statement.executeUpdate("insert into Sets values('PCHK', 'Champions of Kamigawa Promos', NULL, '2004/10/01', 'promo', false, 'CHK', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('CHK', 'Champions of Kamigawa', NULL, '2004/10/01', 'expansion', false, NULL, 307, 306, NULL)");
            statement.executeUpdate("insert into Sets values('WC04', 'World Championship Decks 2004', 'WC04', '2004/09/01', 'memorabilia', false, NULL, 93, 103, NULL)");
            statement.executeUpdate("insert into Sets values('PMRD', 'Mirrodin Promos', NULL, '2004/06/04', 'promo', false, 'MRD', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('P5DN', 'Fifth Dawn Promos', NULL, '2004/06/04', 'promo', false, '5DN', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('5DN', 'Fifth Dawn', NULL, '2004/06/04', 'expansion', false, NULL, 165, 165, NULL)");
            statement.executeUpdate("insert into Sets values('PDST', 'Darksteel Promos', NULL, '2004/02/06', 'promo', false, 'DST', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('DST', 'Darksteel', NULL, '2004/02/06', 'expansion', false, NULL, 165, 165, NULL)");
            statement.executeUpdate("insert into Sets values('PAL04', 'Arena League 2004', NULL, '2004/01/01', 'promo', false, NULL, 14, 8, NULL)");
            statement.executeUpdate("insert into Sets values('F04', 'Friday Night Magic 2004', NULL, '2004/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('P04', 'Magic Player Rewards 2004', 'P04', '2004/01/01', 'promo', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('G04', 'Judge Gift Cards 2004', NULL, '2004/01/01', 'promo', false, NULL, 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('MRD', 'Mirrodin', NULL, '2003/10/02', 'expansion', false, NULL, 306, 306, NULL)");
            statement.executeUpdate("insert into Sets values('WC03', 'World Championship Decks 2003', 'WC03', '2003/08/06', 'memorabilia', false, NULL, 132, 142, NULL)");
            statement.executeUpdate("insert into Sets values('P8ED', 'Eighth Edition Promos', NULL, '2003/07/28', 'promo', false, '8ED', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('8ED', 'Eighth Edition', NULL, '2003/07/28', 'core', false, NULL, 711, 350, NULL)");
            statement.executeUpdate("insert into Sets values('PSCG', 'Scourge Promos', NULL, '2003/05/26', 'promo', false, 'SCG', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('SCG', 'Scourge', NULL, '2003/05/26', 'expansion', false, NULL, 143, 143, NULL)");
            statement.executeUpdate("insert into Sets values('PLGN', 'Legions Promos', NULL, '2003/02/03', 'promo', false, 'LGN', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('LGN', 'Legions', NULL, '2003/02/03', 'expansion', false, NULL, 145, 145, NULL)");
            statement.executeUpdate("insert into Sets values('PMOA', 'Magic Online Avatars', NULL, '2003/01/01', 'vanguard', true, NULL, 85, 85, NULL)");
            statement.executeUpdate("insert into Sets values('PJJT', 'Japan Junior Tournament', NULL, '2003/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('PAL03', 'Arena League 2003', NULL, '2003/01/01', 'promo', false, NULL, 8, 8, NULL)");
            statement.executeUpdate("insert into Sets values('P03', 'Magic Player Rewards 2003', 'P03', '2003/01/01', 'promo', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('F03', 'Friday Night Magic 2003', NULL, '2003/01/01', 'promo', false, NULL, 13, 12, NULL)");
            statement.executeUpdate("insert into Sets values('G03', 'Judge Gift Cards 2003', NULL, '2003/01/01', 'promo', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('OVNT', 'Vintage Championship', NULL, '2003/01/01', 'memorabilia', false, NULL, 35, 35, NULL)");
            statement.executeUpdate("insert into Sets values('WC02', 'World Championship Decks 2002', 'WC02', '2002/12/01', 'memorabilia', false, NULL, 143, 153, NULL)");
            statement.executeUpdate("insert into Sets values('ONS', 'Onslaught', NULL, '2002/10/07', 'expansion', false, NULL, 351, 350, NULL)");
            statement.executeUpdate("insert into Sets values('PONS', 'Onslaught Promos', NULL, '2002/09/28', 'promo', false, 'ONS', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('JP1', 'Hobby Japan Promos', NULL, '2002/07/01', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('PRM', 'Magic Online Promos', NULL, '2002/06/24', 'promo', true, NULL, 3095, 3095, NULL)");
            statement.executeUpdate("insert into Sets values('PJUD', 'Judgment Promos', NULL, '2002/05/27', 'promo', false, 'JUD', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('JUD', 'Judgment', NULL, '2002/05/27', 'expansion', false, NULL, 143, 143, NULL)");
            statement.executeUpdate("insert into Sets values('PTOR', 'Torment Promos', NULL, '2002/02/04', 'promo', false, 'TOR', 3, 1, NULL)");
            statement.executeUpdate("insert into Sets values('TOR', 'Torment', NULL, '2002/02/04', 'expansion', false, NULL, 143, 143, NULL)");
            statement.executeUpdate("insert into Sets values('PAL02', 'Arena League 2002', NULL, '2002/01/01', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('F02', 'Friday Night Magic 2002', NULL, '2002/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('PR2', 'Magic Player Rewards 2002', 'PR2', '2002/01/01', 'promo', false, NULL, 6, 6, NULL)");
            statement.executeUpdate("insert into Sets values('G02', 'Judge Gift Cards 2002', NULL, '2002/01/01', 'promo', false, NULL, 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('DKM', 'Deckmasters', NULL, '2001/12/01', 'box', false, NULL, 58, 50, NULL)");
            statement.executeUpdate("insert into Sets values('PODY', 'Odyssey Promos', NULL, '2001/10/01', 'promo', false, 'ODY', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('ODY', 'Odyssey', NULL, '2001/10/01', 'expansion', false, NULL, 353, 350, NULL)");
            statement.executeUpdate("insert into Sets values('WC01', 'World Championship Decks 2001', 'WC01', '2001/08/08', 'memorabilia', false, NULL, 128, 138, NULL)");
            statement.executeUpdate("insert into Sets values('PSDG', 'Sega Dreamcast Cards', NULL, '2001/06/28', 'box', true, NULL, 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('PAPC', 'Apocalypse Promos', NULL, '2001/06/04', 'promo', false, 'APC', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('APC', 'Apocalypse', NULL, '2001/06/04', 'expansion', false, NULL, 143, 143, NULL)");
            statement.executeUpdate("insert into Sets values('7ED', 'Seventh Edition', NULL, '2001/04/11', 'core', false, NULL, 708, 350, NULL)");
            statement.executeUpdate("insert into Sets values('PPLS', 'Planeshift Promos', NULL, '2001/02/05', 'promo', false, 'PLS', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('PLS', 'Planeshift', NULL, '2001/02/05', 'expansion', false, NULL, 146, 143, NULL)");
            statement.executeUpdate("insert into Sets values('PAL01', 'Arena League 2001', NULL, '2001/01/01', 'promo', false, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('G01', 'Judge Gift Cards 2001', NULL, '2001/01/01', 'promo', false, NULL, 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('MPR', 'Magic Player Rewards 2001', 'MPR', '2001/01/01', 'promo', false, NULL, 1, 8, NULL)");
            statement.executeUpdate("insert into Sets values('F01', 'Friday Night Magic 2001', NULL, '2001/01/01', 'promo', false, NULL, 7, 7, NULL)");
            statement.executeUpdate("insert into Sets values('PINV', 'Invasion Promos', NULL, '2000/10/02', 'promo', false, 'INV', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('INV', 'Invasion', NULL, '2000/10/02', 'expansion', false, NULL, 354, 350, NULL)");
            statement.executeUpdate("insert into Sets values('BTD', 'Beatdown Box Set', NULL, '2000/10/01', 'box', false, NULL, 90, 90, NULL)");
            statement.executeUpdate("insert into Sets values('WC00', 'World Championship Decks 2000', 'WC00', '2000/08/02', 'memorabilia', false, NULL, 107, 117, NULL)");
            statement.executeUpdate("insert into Sets values('PPCY', 'Prophecy Promos', NULL, '2000/06/05', 'promo', false, 'PCY', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('PCY', 'Prophecy', NULL, '2000/06/05', 'expansion', false, NULL, 144, 143, NULL)");
            statement.executeUpdate("insert into Sets values('S00', 'Starter 2000', NULL, '2000/04/01', 'starter', false, NULL, 20, 20, NULL)");
            statement.executeUpdate("insert into Sets values('PNEM', 'Nemesis Promos', NULL, '2000/02/14', 'promo', false, 'NEM', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('NEM', 'Nemesis', NULL, '2000/02/14', 'expansion', false, NULL, 143, 143, NULL)");
            statement.executeUpdate("insert into Sets values('PELP', 'European Land Program', NULL, '2000/02/05', 'promo', false, NULL, 15, 15, NULL)");
            statement.executeUpdate("insert into Sets values('PAL00', 'Arena League 2000', NULL, '2000/01/01', 'promo', false, NULL, 11, 11, NULL)");
            statement.executeUpdate("insert into Sets values('FNM', 'Friday Night Magic 2000', NULL, '2000/01/01', 'promo', false, NULL, 11, 350, NULL)");
            statement.executeUpdate("insert into Sets values('G00', 'Judge Gift Cards 2000', NULL, '2000/01/01', 'promo', false, NULL, 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('PSUS', 'Junior Super Series', NULL, '1999/12/01', 'promo', false, NULL, 18, 350, NULL)");
            statement.executeUpdate("insert into Sets values('BRB', 'Battle Royale Box Set', NULL, '1999/11/12', 'box', false, NULL, 136, 136, NULL)");
            statement.executeUpdate("insert into Sets values('PMMQ', 'Mercadian Masques Promos', NULL, '1999/10/04', 'promo', false, 'MMQ', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('MMQ', 'Mercadian Masques', NULL, '1999/10/04', 'expansion', false, NULL, 350, 350, NULL)");
            statement.executeUpdate("insert into Sets values('PWOS', 'Wizards of the Coast Online Store', NULL, '1999/09/04', 'promo', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('WC99', 'World Championship Decks 1999', 'WC99', '1999/08/04', 'memorabilia', false, NULL, 100, 111, NULL)");
            statement.executeUpdate("insert into Sets values('PWOR', 'World Championship Promos', NULL, '1999/08/04', 'promo', false, NULL, 2, 350, NULL)");
            statement.executeUpdate("insert into Sets values('PGRU', 'Guru', NULL, '1999/07/12', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('S99', 'Starter 1999', NULL, '1999/07/01', 'starter', false, NULL, 173, 173, NULL)");
            statement.executeUpdate("insert into Sets values('PUDS', 'Urza''s Destiny Promos', NULL, '1999/06/07', 'promo', false, 'UDS', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('UDS', 'Urza''s Destiny', NULL, '1999/06/07', 'expansion', false, NULL, 143, 143, NULL)");
            statement.executeUpdate("insert into Sets values('PPTK', 'Portal Three Kingdoms Promos', NULL, '1999/05/01', 'promo', false, 'PTK', 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('PTK', 'Portal Three Kingdoms', NULL, '1999/05/01', 'starter', false, NULL, 180, 180, NULL)");
            statement.executeUpdate("insert into Sets values('6ED', 'Classic Sixth Edition', NULL, '1999/04/21', 'core', false, NULL, 351, 350, NULL)");
            statement.executeUpdate("insert into Sets values('PULG', 'Urza''s Legacy Promos', NULL, '1999/02/15', 'promo', false, 'ULG', 2, 1, NULL)");
            statement.executeUpdate("insert into Sets values('ULG', 'Urza''s Legacy', NULL, '1999/02/15', 'expansion', false, NULL, 143, 143, NULL)");
            statement.executeUpdate("insert into Sets values('PAL99', 'Arena League 1999', NULL, '1999/01/01', 'promo', false, NULL, 10, 350, NULL)");
            statement.executeUpdate("insert into Sets values('G99', 'Judge Gift Cards 1999', NULL, '1999/01/01', 'promo', false, NULL, 1, 350, NULL)");
            statement.executeUpdate("insert into Sets values('ATH', 'Anthologies', NULL, '1998/11/01', 'box', false, NULL, 85, 85, NULL)");
            statement.executeUpdate("insert into Sets values('PUSG', 'Urza''s Saga Promos', NULL, '1998/10/12', 'promo', false, 'USG', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('USG', 'Urza''s Saga', NULL, '1998/10/12', 'expansion', false, NULL, 357, 350, NULL)");
            statement.executeUpdate("insert into Sets values('PALP', 'Asia Pacific Land Program', NULL, '1998/09/01', 'promo', false, NULL, 15, 15, NULL)");
            statement.executeUpdate("insert into Sets values('WC98', 'World Championship Decks 1998', 'WC98', '1998/08/12', 'memorabilia', false, NULL, 107, 117, NULL)");
            statement.executeUpdate("insert into Sets values('UGL', 'Unglued', 'TUGL', '1998/08/11', 'funny', false, NULL, 88, 94, NULL)");
            statement.executeUpdate("insert into Sets values('P02', 'Portal Second Age', NULL, '1998/06/24', 'starter', false, NULL, 165, 165, NULL)");
            statement.executeUpdate("insert into Sets values('PEXO', 'Exodus Promos', NULL, '1998/06/15', 'promo', false, 'EXO', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('EXO', 'Exodus', NULL, '1998/06/15', 'expansion', false, NULL, 143, 143, NULL)");
            statement.executeUpdate("insert into Sets values('PSTH', 'Stronghold Promos', NULL, '1998/03/02', 'promo', false, 'STH', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('STH', 'Stronghold', NULL, '1998/03/02', 'expansion', false, NULL, 143, 143, NULL)");
            statement.executeUpdate("insert into Sets values('JGP', 'Judge Gift Cards 1998', NULL, '1998/01/01', 'promo', false, NULL, 3, 3, NULL)");
            statement.executeUpdate("insert into Sets values('PTMP', 'Tempest Promos', NULL, '1997/10/14', 'promo', false, 'TMP', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('TMP', 'Tempest', NULL, '1997/10/14', 'expansion', false, NULL, 350, 350, NULL)");
            statement.executeUpdate("insert into Sets values('WC97', 'World Championship Decks 1997', 'WC97', '1997/08/13', 'memorabilia', false, NULL, 121, 131, NULL)");
            statement.executeUpdate("insert into Sets values('WTH', 'Weatherlight', NULL, '1997/06/09', 'expansion', false, NULL, 167, 167, NULL)");
            statement.executeUpdate("insert into Sets values('OLEP', 'Oversized League Prizes', NULL, '1997/05/30', 'memorabilia', false, NULL, 83, 83, NULL)");
            statement.executeUpdate("insert into Sets values('POR', 'Portal', NULL, '1997/05/01', 'starter', false, NULL, 257, 243, NULL)");
            statement.executeUpdate("insert into Sets values('PVAN', 'Vanguard Series', NULL, '1997/05/01', 'vanguard', false, NULL, 32, 32, NULL)");
            statement.executeUpdate("insert into Sets values('PMIC', 'MicroProse Promos', NULL, '1997/04/01', 'memorabilia', false, 'PAST', 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('PAST', 'Astral Cards', NULL, '1997/04/01', 'box', true, NULL, 12, 12, NULL)");
            statement.executeUpdate("insert into Sets values('5ED', 'Fifth Edition', NULL, '1997/03/24', 'core', false, NULL, 460, 455, NULL)");
            statement.executeUpdate("insert into Sets values('VIS', 'Visions', NULL, '1997/02/03', 'expansion', false, NULL, 167, 167, NULL)");
            statement.executeUpdate("insert into Sets values('ITP', 'Introductory Two-Player Set', NULL, '1996/12/31', 'starter', false, NULL, 67, 67, NULL)");
            statement.executeUpdate("insert into Sets values('MGB', 'Multiverse Gift Box', NULL, '1996/11/01', 'box', false, 'VIS', 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('MIR', 'Mirage', NULL, '1996/10/08', 'expansion', false, NULL, 353, 350, NULL)");
            statement.executeUpdate("insert into Sets values('PRED', 'Redemption Program', NULL, '1996/10/01', 'promo', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('PCEL', 'Celebration Cards', NULL, '1996/08/14', 'memorabilia', false, NULL, 9, 9, NULL)");
            statement.executeUpdate("insert into Sets values('PARL', 'Arena League 1996', NULL, '1996/08/02', 'promo', false, NULL, 7, 7, NULL)");
            statement.executeUpdate("insert into Sets values('RQS', 'Rivals Quick Start Set', NULL, '1996/07/01', 'box', false, NULL, 65, 65, NULL)");
            statement.executeUpdate("insert into Sets values('PLGM', 'DCI Legend Membership', NULL, '1996/07/01', 'promo', false, NULL, 2, 2, NULL)");
            statement.executeUpdate("insert into Sets values('ALL', 'Alliances', NULL, '1996/06/10', 'expansion', false, NULL, 199, 199, NULL)");
            statement.executeUpdate("insert into Sets values('PTC', 'Pro Tour Collector Set', 'PTC', '1996/05/01', 'memorabilia', false, NULL, 307, 308, NULL)");
            statement.executeUpdate("insert into Sets values('O90P', 'Oversized 90''s Promos', NULL, '1996/04/01', 'memorabilia', false, NULL, 10, 10, NULL)");
            statement.executeUpdate("insert into Sets values('HML', 'Homelands', NULL, '1995/10/01', 'expansion', false, NULL, 140, 140, NULL)");
            statement.executeUpdate("insert into Sets values('RIN', 'Rinascimento', NULL, '1995/08/01', 'masters', false, NULL, 69, 69, NULL)");
            statement.executeUpdate("insert into Sets values('REN', 'Renaissance', NULL, '1995/08/01', 'masters', false, NULL, 122, 122, NULL)");
            statement.executeUpdate("insert into Sets values('CHR', 'Chronicles', NULL, '1995/07/01', 'masters', false, NULL, 125, 125, NULL)");
            statement.executeUpdate("insert into Sets values('BCHR', 'Chronicles Foreign Black Border', NULL, '1995/07/01', 'masters', false, 'CHR', 125, 125, NULL)");
            statement.executeUpdate("insert into Sets values('ICE', 'Ice Age', NULL, '1995/06/03', 'expansion', false, NULL, 383, 383, NULL)");
            statement.executeUpdate("insert into Sets values('4BB', 'Fourth Edition Foreign Black Border', NULL, '1995/04/01', 'core', false, '4ED', 378, 378, NULL)");
            statement.executeUpdate("insert into Sets values('4ED', 'Fourth Edition', NULL, '1995/04/01', 'core', false, NULL, 380, 378, NULL)");
            statement.executeUpdate("insert into Sets values('PMEI', 'Media and Collaboration Promos', NULL, '1995/01/01', 'promo', false, NULL, 116, 116, NULL)");
            statement.executeUpdate("insert into Sets values('FEM', 'Fallen Empires', NULL, '1994/11/01', 'expansion', false, NULL, 187, 187, NULL)");
            statement.executeUpdate("insert into Sets values('PHPR', 'HarperPrism Book Promos', NULL, '1994/09/01', 'promo', false, NULL, 5, 5, NULL)");
            statement.executeUpdate("insert into Sets values('DRK', 'The Dark', NULL, '1994/08/01', 'expansion', false, NULL, 122, 119, NULL)");
            statement.executeUpdate("insert into Sets values('PDRC', 'Dragon Con', NULL, '1994/07/15', 'promo', false, NULL, 1, 1, NULL)");
            statement.executeUpdate("insert into Sets values('SUM', 'Summer Magic / Edgar', NULL, '1994/06/21', 'core', false, NULL, 306, 306, NULL)");
            statement.executeUpdate("insert into Sets values('LEG', 'Legends', NULL, '1994/06/01', 'expansion', false, NULL, 310, 310, NULL)");
            statement.executeUpdate("insert into Sets values('3ED', 'Revised Edition', NULL, '1994/04/11', 'core', false, NULL, 306, 306, NULL)");
            statement.executeUpdate("insert into Sets values('FBB', 'Foreign Black Border', NULL, '1994/04/11', 'core', false, '3ED', 307, 306, NULL)");
            statement.executeUpdate("insert into Sets values('ATQ', 'Antiquities', NULL, '1994/03/04', 'expansion', false, NULL, 102, 100, NULL)");
            statement.executeUpdate("insert into Sets values('ARN', 'Arabian Nights', NULL, '1993/12/17', 'expansion', false, NULL, 92, 78, NULL)");
            statement.executeUpdate("insert into Sets values('CEI', 'Intl. Collectors'' Edition', NULL, '1993/12/10', 'memorabilia', false, NULL, 302, 302, NULL)");
            statement.executeUpdate("insert into Sets values('CED', 'Collectors'' Edition', NULL, '1993/12/10', 'memorabilia', false, NULL, 302, 302, NULL)");
            statement.executeUpdate("insert into Sets values('2ED', 'Unlimited Edition', NULL, '1993/12/01', 'core', false, NULL, 302, 302, NULL)");
            statement.executeUpdate("insert into Sets values('LEB', 'Limited Edition Beta', NULL, '1993/10/04', 'core', false, NULL, 302, 302, NULL)");
            statement.executeUpdate("insert into Sets values('LEA', 'Limited Edition Alpha', NULL, '1993/08/05', 'core', false, NULL, 295, 295, NULL)");
            statement.executeUpdate("insert into Sets values('DD3', 'Duel Decks: Anthology', NULL, '2014/12/05', 'duel_deck', false, NULL, 0, 0, NULL)");
            statement.executeUpdate("insert into Sets values('Q01', 'Challenger Decks 2018', NULL, '2018/04/06', 'box', false, NULL, 0, 0, NULL)");
            statement.executeUpdate("insert into Sets values('Q02', 'Challenger Decks 2019', NULL, '2019/04/12', 'box', false, NULL, 0, 0, NULL)");
            statement.executeUpdate("insert into Sets values('Q03', 'Challenger Decks 2019 Japan', NULL, '2019/11/08', 'box', false, NULL, 0, 0, NULL)");
            statement.executeUpdate("insert into Sets values('Q04', 'Challenger Decks 2020', NULL, '2020/03/27', 'box', false, NULL, 0, 0, NULL)");
            statement.executeUpdate("insert into Sets values('Q05', 'Challenger Decks 2021', NULL, '2021/03/02', 'box', false, NULL, 0, 0, NULL)");
            statement.executeUpdate("insert into Sets values('Q08', 'Pioneer Challenger Decks 2022', NULL, '2022/11/04', 'box', false, NULL, 0, 0, NULL)");
            statement.executeUpdate("insert into Sets values('MB1', 'Mystery Booster', NULL, '2019/11/07', 'masters', false, NULL, 0, 249, NULL)");
            statement.executeUpdate("insert into Sets values('FWB', 'Foreign White Border', NULL, '1994/04/11', 'core', false, '3ED', 0, 0, NULL)");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
       
}
