

/*
 * Scenario: Secure Bank PIN Policy Upgrade 
A bank is implementing a new PIN security policy to strengthen customer account protection. Every 
customer's banking PIN must meet the following criteria: 
1. The PIN must be between 6 and 20 characters (inclusive). 
2. It must contain at least one lowercase letter, one uppercase letter, and one digit. 
3. It must not contain three consecutive repeating characters (e.g., "AAA123" is weak, but 
"AA123B" is strong). 

The bank wants to ensure all PINs comply with these security rules. 

Given a string pin_code, return the minimum number of changes required to make it strong. If the PIN 
is already strong, return 0. 
In one step, you can: 
 Insert a character. 
 Delete a character. 
 Replace one character with another character. 

Example 1: 
Input: pin_code = "X1!" 
Output: 3 
Explanation: 
Length is too short (3 characters, needs at least 6) → Insert 3 characters 
Missing a lowercase letter → Insert "a" 
Final strong PIN: "X1!abc" 

Example 2: 
Input: pin_code = "123456" 
Output: 2 
Explanation: 
Missing an uppercase letter → Replace "1" with "A" 
Missing a lowercase letter → Replace "2" with "b" 
Final strong PIN: "Ab3456" 

Example 3: 
Input: pin_code = "Aa1234!" 
Output: 0 
Explanation: Already meets all criteria 
*/


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecureBankPIN {

    /**
     * Method to calculate the minimum number of changes required to make the PIN strong.
     * 
     * @param pin_code The input PIN code string
     * @return The minimum number of changes required
     */
    public static int minimumChangesForStrongPIN(String pin_code) {
        int n = pin_code.length();

        // Check for missing character types
        boolean hasLower = false, hasUpper = false, hasDigit = false;
        for (char c : pin_code.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        int missingTypes = 0;
        if (!hasLower) missingTypes++;
        if (!hasUpper) missingTypes++;
        if (!hasDigit) missingTypes++;

        // Case 1: Too short (n < 6)
        if (n < 6) {
            int insertionsNeeded = 6 - n;
            return Math.max(insertionsNeeded, missingTypes);
        }

        // Identify runs of 3 or more identical consecutive characters
        List<Integer> runs = new ArrayList<>();
        for (int i = 0; i < n; ) {
            int j = i;
            while (j < n && pin_code.charAt(j) == pin_code.charAt(i)) {
                j++;
            }
            int runLength = j - i;
            if (runLength >= 3) {
                runs.add(runLength);
            }
            i = j;
        }

        // Case 2: Length is within [6, 20]
        if (n <= 20) {
            int replaceNeeded = 0;
            for (int run : runs) {
                replaceNeeded += run / 3;  // each run of L needs L//3 replacements
            }
            return Math.max(replaceNeeded, missingTypes);
        }

        // Case 3: Too long (n > 20)
        int over = n - 20; // number of deletions required
        int deletionsLeft = over;

        // Convert runs to array for mutation
        int[] runArray = runs.stream().mapToInt(Integer::intValue).toArray();

        // Use deletions optimally: prioritize runs by mod 3 (0, 1, 2)
        // Why? Because deleting 1 char from run with mod 0 reduces replacement need by 1
        for (int mod = 0; mod < 3 && deletionsLeft > 0; mod++) {
            for (int i = 0; i < runArray.length && deletionsLeft > 0; i++) {
                if (runArray[i] < 3) continue;
                if (runArray[i] % 3 != mod) continue;

                // Max deletions we can use here to save one replacement
                int deleteCost = (mod == 0) ? 1 : (mod == 1) ? 2 : 3;
                if (deletionsLeft >= deleteCost) {
                    runArray[i] -= deleteCost;
                    deletionsLeft -= deleteCost;
                }
            }
        }

        // Calculate remaining replacements needed after deletions
        int replaceNeeded = 0;
        for (int run : runArray) {
            if (run >= 3) {
                replaceNeeded += run / 3;
            }
        }

        // Total steps = deletions (over) + max(replacements, missing types)
        return over + Math.max(replaceNeeded, missingTypes);
    }

    // Test Cases (Including yours)
    public static void main(String[] args) {
        List<Testcase> testcases = Arrays.asList(
            // --- Official Examples ---
            new Testcase("X1!", 3),
            new Testcase("123456", 2),
            new Testcase("Aa1234!", 0)
        
        );

        System.out.println("Running Secure PIN Checker Test Cases...\n");
        int passed = 0;
        for (int i = 0; i < testcases.size(); i++) {
            Testcase t = testcases.get(i);
            int result = minimumChangesForStrongPIN(t.pin);
            boolean success = result == t.expected;
            if (success) passed++;

            System.out.printf("Test %d: PIN = '%s'\n", i + 1, t.pin);
            System.out.printf("Expected: %d, Got: %d → %s\n\n", 
                              t.expected, result, success ? " PASS" : " FAIL");
        }

        System.out.println("     Passed " + passed + " / " + testcases.size() + " tests.");
    }

    static class Testcase {
        String pin;
        int expected;

        Testcase(String pin, int expected) {
            this.pin = pin;
            this.expected = expected;
        }
    }
}


/*
 * -----OUTPUT------
 * Running Secure PIN Checker Test Cases...

Test 1: PIN = 'X1!'
Expected: 3, Got: 3 ?  PASS

Test 2: PIN = '123456'
Expected: 2, Got: 2 ?  PASS

Test 3: PIN = 'Aa1234!'
Expected: 0, Got: 0 ?  PASS

    Passed 3 / 3 tests.
 */