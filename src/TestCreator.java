import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class TestCreator {
    //File manipulation
    static String apiName;
    static Scanner scan;
    static Writer writer;

    //Tokens List
    private static final ArrayList<SchemaToken> allTokens = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        setupInputAndOutput();
        convertSchemaToTokens();
        allPresentTestCreation();
        requiredNotNullTestCreation();
        regexTestCreation();
    }

    private static void allPresentTestCreation() throws IOException {
        writer = new FileWriter("output/" + apiName + "-allPresent.txt");

        ArrayList<String> allNames = new ArrayList<>();
        for (SchemaToken el : allTokens) {
            allNames.add("\n        " + el.name);
        }

        writer.write("" +
                "//Test to loop through all the fields are present\n" +
                "pm.test(\"Check all fields are present\", function () {\n" +
                "   var jsonData = pm.response.json();\n" +
                "   var randomIndex = Math.floor(Math.random() * jsonData.length)\n" +
                "\n   var requiredElements ="
                +
                allNames
                +
                ";\n" +
                "\n" +
                "   //Loop through all the required fields\n" +
                "   requiredElements.forEach(element => pm.expect\n" +
                "   (jsonData[randomIndex]).to.have.property(element));\n" +
                "});"
        );

        writer.close();
    }

    private static void requiredNotNullTestCreation() throws IOException {
        writer = new FileWriter("output/" + apiName + "-requiredNotNull.txt");

        ArrayList<String> allNonNullFields = new ArrayList<>();
        for (SchemaToken el : allTokens.stream().filter(i -> i.isRequired).collect(Collectors.toList())) {
            allNonNullFields.add("\n        " + el.name);
        }

        writer.write("" +
                "//Test to loop through all the required fields are not null\n" +
                "pm.test(\"Check all fields are present\", function () {\n" +
                "   var jsonData = pm.response.json();\n" +
                "   var randomIndex = Math.floor(Math.random() * jsonData.length)\n" +
                "\n   var requiredElements ="
                +
                allNonNullFields
                +
                ";\n" +
                "\n" +
                " //Loop through all the required fields checking they are not null\n" +
                " requiredElements.forEach(element =>\n" +
                " pm.expect(jsonData[randomIndex][element]).to.not.equal(null));\n" +
                "});"
        );

        writer.close();
    }

    private static void regexTestCreation() throws IOException {
        writer = new FileWriter("output/" + apiName + "-regex.txt");
        writer.write("//Get globalData and a random index to check\n" +
                "var jsonData = pm.response.json();\n" +
                "var randomIndex = Math.floor(Math.random() * jsonData.length)\n\n");

        for (SchemaToken token : allTokens) {
            switch (token.tokenType) {
                case ("number") -> regexNumberHelper(token);
                case ("array") -> regexArrayHelper(token);
                case ("boolean") -> regexBoolean(token);
                case ("string") -> regexStringHelper(token);
            }
        }

        writer.close();
    }

    private static void regexNumberHelper(SchemaToken token) throws IOException {
        //TODO intergrate with ability to be null
        consoleClear();
        //TODO automate this part, ADD to input file instead/option to have auto selector?
        //Eg user chooses if manual or not
        String message =
                "For the field \"" + token.name + "\" please select which regex option would you like:\n"
                        + "(1). Positive Number\n"
                        + "(2). Non-Negative Number\n"
                        + "(3). Date\n"
                        + "(4). Day\n"
                        + "(5). Month\n"
                        + "(6). Year\n";

        System.out.println(message);
        scan = new Scanner(System.in);
        String response = scan.next();

        while (!response.matches("1|2|3|4|5|6")) {
            consoleClear();
            System.out.println("Invalid Response\n" + message);
            response = scan.next();
        }

        switch (parseInt(response)) {
            case (1) -> regexNumberPositive(token);
            case (2) -> regexNumberNonNegative(token);
            case (3) -> regexNumberDate(token);
            case (4) -> regexNumberDayOfMonth(token);
            case (5) -> regexNumberMonth(token);
            case (6) -> regexNumberYear(token);
        }
    }

    private static void regexNumberPositive(SchemaToken token) throws IOException {
        writer.write("" +
                "//Test to check " + token.name + " is positive\n" +
                "pm.test(\"Check " + token.name + " is positive\", function () {\n" +
                "    pm.expect(jsonData[randomIndex]." + token.name + ").to.greaterThan(0);\n" +
                "});\n\n");
    }

    private static void regexNumberNonNegative(SchemaToken token) throws IOException {
        writer.write("" +
                "//Test to check " + token.name + " is non-negative\n" +
                "pm.test(\"Check " + token.name + " is non-negative\", function () {\n" +
                "    pm.expect(jsonData[randomIndex]." + token.name + ").to.greaterThan(-1);\n" +
                "});\n\n");
    }

    private static void regexNumberDayOfMonth(SchemaToken token) throws IOException {
        writer.write("" +
                "//Test to check " + token.name + " is valid day of month; between (inclusive) 1 and 31\n" +
                "pm.test(\"Check " + token.name + " is valid day of month; between (inclusive) 1 and 31\", function () {\n" +
                "    var result = jsonData[randomIndex]." + token.name + " > 0 && jsonData[randomIndex]." + token.name + " < 32;\n" +
                "    pm.expect(result).to.equal(true)\n" +
                "});\n\n");
    }

    private static void regexNumberMonth(SchemaToken token) throws IOException {
        writer.write("" +
                "//Test to check " + token.name + " is valid month; between (inclusive) 1 and 12\n" +
                "pm.test(\"Check " + token.name + " is valid month; between (inclusive) 1 and 12\", function () {\n" +
                "    var result = jsonData[randomIndex]." + token.name + " > 0 && jsonData[randomIndex]." + token.name + " < 13;\n" +
                "    pm.expect(result).to.equal(true)\n" +
                "});\n\n");
    }

    private static void regexNumberYear(SchemaToken token) throws IOException {
        writer.write("" +
                "//Test to check " + token.name + " is valid year\n" +
                "pm.test(\"Check " + token.name + " is valid year\", function () {\n" +
                "    pm.expect(jsonData[randomIndex]." + token.name + ").to.match(/^\\d{4}$/);\n" +
                "});\n\n");
    }

    private static void regexNumberDate(SchemaToken token) throws IOException {
        writer.write("" +
                "//Test to check " + token.name + " is valid date\n" +
                "pm.test(\"Check " + token.name + " is valid date\", function () {\n" +
                "    pm.expect(Date.parse(jsonData[randomIndex]." + token.name + ")).not.equal(NaN)\n" +
                "});\n\n");
    }

    private static void regexBoolean(SchemaToken token) throws IOException {
        //TODO ADD SKIPS FOR NON REQUIRED FIELDS TO BE NULL EG  ADD ON FOR ALL NON REQUIRED FIELDS if(jsonData[randomIndex].organisationId != null)
        writer.write("" +
                "//Test to check " + token.name + " is a boolean\n" +
                "pm.test(\"Check " + token.name + " is a boolean\", function () {\n" +
                "    pm.expect(typeof jsonData[randomIndex]." + token.name + ").to.equal('boolean')\n" +
                "});\n\n");

    }

    /**
     *
     * @param token The Array Token to process
     * @throws IOException if the file isn't found
     */
    private static void regexArrayHelper(SchemaToken token) throws IOException {
       //TODO
    }

    /**
     *
     * @param token The Array Token to process
     * @throws IOException if the file isn't found
     */
    private static void regexStringHelper(SchemaToken token) throws IOException {
        writer.write("" +
                "//Test to check " + token.name + " is a string\n" +
                "pm.test(\"Check " + token.name + " is a string\", function () {\n" +
                "    pm.expect(typeof jsonData[randomIndex]." + token.name + ").to.equal('string')\n" +
                "});\n\n");
    }

    /** Used to get the name for the file outputs, also initializes the scanner to point at the input file
     *
     * @throws IOException if the file isn't found
     */
    private static void setupInputAndOutput() throws IOException {
        //Initialize the Scanner
        scan = new Scanner(new File("src/input.txt"));

        //Store the API name
        apiName = scan.next();

        //Reset to first parameter
        scan.nextLine();
        scan.nextLine();
        scan.nextLine();
    }

    /**
     * Convert a schema to token
     */
    private static void convertSchemaToTokens() {
        //While more tokens to scan
        while (scan.hasNext()) {
            //Store the information for a token
            String fieldName = scan.next();
            String tokenType = scan.next();
            boolean isRequired = scan.hasNext("required");

            //Add the token to the List
            allTokens.add(new SchemaToken(fieldName, tokenType, isRequired));
            //Void the required tag
            if (isRequired) scan.next();
        }
    }

    /**
     * Insert 100 new lines into the terminal,
     * Effectively a cls command
     */
    private static void consoleClear() {
        for (int i = 0; i < 100; i++)
            System.out.println("\n");
    }
}
