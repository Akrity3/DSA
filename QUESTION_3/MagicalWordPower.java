// In a mystical world, powerful magical words exist. A magical word is a sequence of letters that reads
// the same forward and backward and always has an odd number of letters.
// A sorcerer has a long ancient manuscript represented as a string M. The sorcerer wants to extract two
// non-overlapping magical words from M in order to maximize their power combination.
// The power of a magical word is equal to its length, and the total power is the product of the lengths of
// the two chosen magical words.
// Task
// Given the manuscript M, determine the maximum possible power combination that can be achieved
// by selecting two non-overlapping magical words.
// Example 1
// Input:
// M = "xyzyxabc"
// Output:5
// Explanation:
// The magical word "xyzyx" (length 5) at [0:4]
// The magical word "a" (length 1) at [5:5]
// The product is 5 × 1 = 5
// Even if we instead choose:
// "xyzyx" (length 5)
// "c" (length 1)
// Max product = 5 × 1 = 5
// So the best possible product is 5.
// Example 2
// Input: M = "levelwowracecar"
// Output: 35
// Explanation:
// "level" (length 5)
// "racecar" (length 7)
// The product is 5 × 7 = 35.


//solution
public class MagicalWordPower {

    /**
     * Finds the maximum product of lengths of two non-overlapping magical words
     * (odd-length palindromes) in the manuscript M.
     *
     * @param M the manuscript string
     * @return maximum product, or 0 if no two non-overlapping magical words exist
     */
    public static int maxPowerCombination(String M) {
        if (M == null || M.length() < 2) {
            return 0;
        }

        int n = M.length();
        int[] leftMax = new int[n];  // max palindrome length ending <= i
        int[] rightMax = new int[n]; // max palindrome length starting >= i

        // Find all odd-length palindromes
        for (int center = 0; center < n; center++) {
            int l = center, r = center;
            while (l >= 0 && r < n && M.charAt(l) == M.charAt(r)) {
                int length = r - l + 1;
                if (length > leftMax[r]) {
                    leftMax[r] = length;
                }
                if (length > rightMax[l]) {
                    rightMax[l] = length;
                }
                l--;
                r++;
            }
        }

        // Update leftMax to hold max palindrome length ending at or before i
        for (int i = 1; i < n; i++) {
            leftMax[i] = Math.max(leftMax[i], leftMax[i - 1]);
        }

        // Update rightMax to hold max palindrome length starting at or after i
        for (int i = n - 2; i >= 0; i--) {
            rightMax[i] = Math.max(rightMax[i], rightMax[i + 1]);
        }

        // Try every split point between i and i+1
        int maxProduct = 0;
        for (int i = 0; i < n - 1; i++) {
            int leftPart = leftMax[i];
            int rightPart = rightMax[i + 1];
            if (leftPart > 0 && rightPart > 0) {
                int product = leftPart * rightPart;
                if (product > maxProduct) {
                    maxProduct = product;
                }
            }
        }

        return maxProduct;
    }

    public static void main(String[] args) {
        // Example 1(Test case 1)
        System.out.println("test case 1: M = \"xyzyxabc\"");
        int result1 = maxPowerCombination("xyzyxabc");
        System.out.println("Result: " + result1);
        System.out.println("Expected: 5");
        System.out.println("Pass: " + (result1 == 5 ? "Yes" : "No"));
        System.out.println();

        // Example 2 (Test case 2)
        System.out.println("test case 2: M = \"levelwowracecar\"");
        int result2 = maxPowerCombination("levelwowracecar");
        System.out.println("Result: " + result2);
        System.out.println("Expected: 35");
        System.out.println("Pass: " + (result2 == 35 ? "Yes" : "No"));
        System.out.println();

        // Test Case: No two magical words / Empty or single char
        System.out.println("Test Case No: M = \"a\" (single character string)"); 
        int resultNo = maxPowerCombination("a");
        System.out.println("Result: " + resultNo);
        System.out.println("Expected: 0");
        System.out.println("Pass: " + (resultNo == 0 ? "Yes" : "No"));
        System.out.println();

        // Test Case 4: Only one palindrome exists (whole string), expected output: 0
        System.out.println("Test Case 4: M = \"aba\" (only one palindrome)");
        int resultNo2 = maxPowerCombination("aba");
        System.out.println("Result: " + resultNo2);
        System.out.println("Expected: 0");
        System.out.println("Pass: " + (resultNo2 == 0 ? "Yes" : "No"));
        System.out.println();

    }

}


//-----Output-----------

// Test Case 1: M = "xyzyxabc"
// Result: 5
// Expected: 5
// Pass: Yes

// Test Case 2: M = "levelwowracecar"
// Result: 35
// Expected: 35
// Pass: Yes

// Test Case No: M = "a" (single character string)
// Result: 0
// Expected: 0
// Pass: Yes

// Test Case 4: M = "aba" (only one palindrome)
// Result: 1
// Expected: 0
// Pass: No


