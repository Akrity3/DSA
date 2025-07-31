
/*
b) 
Imagine a puzzle where words represent numbers, and we need to find a unique digit mapping for 
each letter to satisfy a given equation. The rule is that no two letters can have the same digit, and 
numbers must not have leading zeros. 
Scenario 1: True Case (Valid Equation) 
Equation: 
"STAR" + "MOON" = "NIGHT" 
"STAR"+"MOON"="NIGHT" 
Step 1: Assign Unique Digits to Letters 
S = 8 
T = 4 
A = 2 
R = 5 
M = 7 
O = 1 
N = 9 
I = 6 
G = 3 
H = 0 
Step 2: Convert Words into Numbers 
"STAR" → 8425 
"MOON" → 7119 
"NIGHT" → 96350 
Step 3: Verify the Sum 
8425 + 7119 = 15544 
Equation: "CODE" + "BUG" = "DEBUG" 
"CODE"+"BUG"="DEBUG" 
Now, let's try to assign unique digits. 
Step 1: Assign Unique Digits to Letters 
C = 1 
O = 0 
D = 5 
E = 7 
B = 3 
U = 9 
G = 2 
Step 2: Convert Words into Numbers 
"CODE" → 1057 
"BUG" → 392 
"DEBUG" → 57392 
Step 3: Verify the Sum 
1057+392=1449 
Since 1449 ≠ 57392, this mapping is invalid, and no possible digit assignment satisfies the equation. 
 */


//Solution
import java.util.*;

public class CryptarithmeticSolver {

    // Track which digits (0-9) have been used in assignments
    private static boolean[] used = new boolean[10];

    // Mapping from letter -> assigned digit
    private static Map<Character, Integer> assignment = new HashMap<>();

    // List of unique letters to assign digits to
    private static List<Character> letters = new ArrayList<>();

    // Letters that appear as first character in any word (cannot be assigned 0)
    private static Set<Character> leadingLetters = new HashSet<>();

    // The three words in uppercase for uniformity
    private static String word1, word2, word3;

    /**
     * Entry point for checking solvability of equation: w1 + w2 = w3
     * @param w1 first addend word
     * @param w2 second addend word
     * @param w3 sum word
     * @return true if a valid digit assignment exists, else false
     */
    public static boolean isSolvable(String w1, String w2, String w3) {
        // Convert words to uppercase for consistency
        word1 = w1.toUpperCase();
        word2 = w2.toUpperCase();
        word3 = w3.toUpperCase();

        // Reset state for new problem
        Arrays.fill(used, false);
        assignment.clear();
        letters.clear();
        leadingLetters.clear();

        // Collect all unique letters appearing in the three words
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : (word1 + word2 + word3).toCharArray()) {
            uniqueChars.add(c);
        }
        letters.addAll(uniqueChars);

        // Mark the first letter of each word as leading letter
        // Leading letters cannot be assigned digit 0
        if (!word1.isEmpty()) leadingLetters.add(word1.charAt(0));
        if (!word2.isEmpty()) leadingLetters.add(word2.charAt(0));
        if (!word3.isEmpty()) leadingLetters.add(word3.charAt(0));

        // If there are more than 10 unique letters, solution impossible
        if (letters.size() > 10) {
            return false;
        }

        // Start recursive backtracking from first letter
        return backtrack(0);
    }

    /**
     * Recursive backtracking to assign digits to letters
     * @param idx current index in letters list to assign
     * @return true if a valid assignment found downstream, false otherwise
     */
    private static boolean backtrack(int idx) {
        // Base case: all letters assigned
        if (idx == letters.size()) {
            return isValid();  // Check if the current assignment satisfies the equation
        }

        char letter = letters.get(idx);

        // Try all digits 0-9 for this letter
        for (int digit = 0; digit <= 9; digit++) {
            // Skip if digit already used in another letter
            if (used[digit]) continue;

            // Leading letters cannot be zero
            if (digit == 0 && leadingLetters.contains(letter)) continue;

            // Assign digit to current letter
            assignment.put(letter, digit);
            used[digit] = true;

            // Recurse for next letter
            if (backtrack(idx + 1)) {
                return true;  // Found valid assignment, bubble up success
            }

            // Backtrack: unassign letter and digit
            used[digit] = false;
            assignment.remove(letter);
        }

        // No digit assignment worked for this letter
        return false;
    }

    /**
     * Check if the currently assigned digits satisfy the equation: word1 + word2 == word3
     * @return true if valid, false otherwise
     */
    private static boolean isValid() {
        try {
            long val1 = toNumber(word1);
            long val2 = toNumber(word2);
            long val3 = toNumber(word3);

            // Check sum equality
            return val1 + val2 == val3;
        } catch (NumberFormatException e) {
            // Thrown if a word has leading zero, making assignment invalid
            return false;
        }
    }

    /**
     * Convert a word to its numeric value based on current letter-to-digit assignment
     * @param word input word
     * @return numeric value represented by the word
     * @throws NumberFormatException if word has leading zero for multi-char word
     */
    private static long toNumber(String word) {
        if (word.isEmpty()) return 0;

        long num = 0;
        for (char c : word.toCharArray()) {
            num = num * 10 + assignment.get(c);
        }
        // Check for leading zero in multi-character word
        if (word.length() > 1 && assignment.get(word.charAt(0)) == 0) {
            throw new NumberFormatException("Leading zero");
        }
        return num;
    }

    /**
     * Helper to print the current assignment in sorted letter order
     */
    private static void printAssignment() {
        System.out.print("Assignment: ");
        List<String> pairs = new ArrayList<>();
        for (var entry : assignment.entrySet()) {
            pairs.add(entry.getKey() + "=" + entry.getValue());
        }
        // Sort pairs alphabetically by letter for consistent output
        Collections.sort(pairs);
        System.out.println(String.join(", ", pairs));
    }


    // ------- Test Cases ---------
    
    public static void main(String[] args) {
         // Test Case 1: "SEND" + "MORE" = "MONEY" → Classic solvable puzzle
        System.out.println("Test 1: SEND + MORE = MONEY");
        boolean result1 = isSolvable("SEND", "MORE", "MONEY");
        System.out.println("Solvable: " + (result1 ? " Yes" : " No"));
        if (result1) printAssignment();
        System.out.println();

        // Test Case 2: "CODE" + "BUG" = "DEBUG"
        System.out.println("Test 2: CODE + BUG = DEBUG");
        boolean result2 = isSolvable("CODE", "BUG", "DEBUG");
        System.out.println("Solvable: " + (result2 ? " Yes" : " No"));
        if (result2) printAssignment();
        else System.out.println("No valid assignment found.");
        System.out.println();

        // ======= Test Case 3 (True Case) =======
        // "SEND" + "MORE" = "MONEY" (classic solvable puzzle)
        System.out.println("Test 3: SEND + MORE = MONEY");
        boolean result3 = isSolvable("SEND", "MORE", "MONEY");
        System.out.println("Solvable: " + (result3 ? " Yes" : " No"));
        if (result3) printAssignment();
        else System.out.println("No valid assignment found.");
        System.out.println();

        // ======= Test Case 4 (False Case) =======
        // "ABCD" + "EFGRnH" = "AAAAB" (highly restrictive, unsolvable)
        System.out.println("Test 4: ABCD + EFGRnH = AAAAB");
        boolean result4 = isSolvable("ABCD", "EFGRnH", "AAAAB");
        System.out.println("Solvable: " + (result4 ? " Yes" : " No"));
        if (result4) printAssignment();
        else System.out.println("No valid assignment found.");
        System.out.println();
    
    }
}

/*
 * -----OUTPUT------
Test 1: SEND + MORE = MONEY
Solvable:  Yes
Assignment: D=7, E=5, M=1, N=6, O=0, R=8, S=9, Y=2

Test 2: CODE + BUG = DEBUG
Solvable:  No
No valid assignment found.

Test 3: SEND + MORE = MONEY
Solvable:  Yes
Assignment: D=7, E=5, M=1, N=6, O=0, R=8, S=9, Y=2

Test 4: ABCD + EFGRnH = AAAAB
Solvable:  No
No valid assignment found.
 */