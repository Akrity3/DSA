

/* 
#Question 1 
Maximizing Tech Startup Revenue before Acquisition 
A tech startup, AlgoStart, is planning to get acquired by a larger company. To negotiate a higher 
acquisition price, AlgoStart wants to increase its revenue by launching a few high-return projects. 
However, due to limited resources, the startup can only work on at most k distinct projects before the 
acquisition. 
You are given n potential projects, where the i-th project has a projected revenue gain of revenues[i] 
and requires a minimum investment capital of investments[i] to launch. 
Initially, AlgoStart has c capital. When a project is completed, its revenue gain is added to the 
startup’s total capital, which can then be reinvested into other projects. 
Your task is to determine the maximum possible capital AlgoStart can accumulate after completing at 
most k projects.

Example 1: 
k = 2, c = 0, revenues = [2, 5, 8], investments = [0, 2, 3] 
Output: 7 
Explanation: 
 With initial capital 0, the startup can only launch Project 0 (since it requires 0 investment). 
 After completing Project 0, the capital becomes 0 + 2 = 2. 
 Now, with 2 capital, the startup can choose either Project 1 (investment 2, revenue 5) or 
Project 2 (investment 3, revenue 8). 
 To maximize revenue, it should select Project 2. However, Project 2 requires 3 capital, which is 
not available. So it selects Project 1. 
 After completing Project 1, the capital becomes 2 + 5 = 7. 
 The final maximized capital is 7.

Example 2: 
Input: 
k = 3, c = 1, revenues = [3, 6, 10], investments = [1, 3, 5] 
Output: 19 
Explanation: 
 Initially, with 1 capital, Project 0 can be launched (investment 1, revenue 3). 
 Capital after Project 0 = 1 + 3 = 4. 
 With 4 capital, the startup can now launch Project 1 (investment 3, revenue 6). 
 Capital after Project 1 = 4 + 6 = 10. 
 Now, with 10 capital, Project 2 (investment 5, revenue 10) can be launched. 
 Final capital = 10 + 10 = 19.

*/


// Maximizing Tech Startup Revenue Before Acquisition
// ==================================================
// Problem: Select up to k projects to maximize capital gain, given:
// - Initial capital
// - List of projects with investment requirements and revenue potential
// - Can only complete projects when sufficient capital is available

import java.util.PriorityQueue;

public class StartupCapitalOptimizer {

    /**
     * Project class represents each investment opportunity with:
     * - investment: Capital required to start the project
     * - revenue: Capital gained upon completion
     */
    static class InvestmentOpportunity {
        final int investment;
        final int revenue;

        public InvestmentOpportunity(int investment, int revenue) {
            this.investment = investment;
            this.revenue = revenue;
        }
    }

    /**
     * Core method to calculate maximum capital after strategic investments
     * 
     * @param k Maximum number of projects to complete
     * @param initialCapital Starting funds
     * @param revenues Array of revenue values for projects
     * @param investments Array of required investments for projects
     * @return Maximum possible capital after optimal project selection
     */
    public static int maximizeCapital(int k, int initialCapital, int[] revenues, int[] investments) {
        // Validate input parameters
        if (revenues == null || investments == null || revenues.length != investments.length) {
            throw new IllegalArgumentException("Invalid input arrays");
        }

        // Min-heap to sort projects by investment cost (cheapest first)
        PriorityQueue<InvestmentOpportunity> affordableQueue = new PriorityQueue<>(
            (a, b) -> a.investment - b.investment);

        // Max-heap to sort projects by revenue potential (highest first)
        PriorityQueue<InvestmentOpportunity> profitableQueue = new PriorityQueue<>(
            (a, b) -> b.revenue - a.revenue);

        // Initialize with all potential projects
        for (int i = 0; i < revenues.length; i++) {
            affordableQueue.add(new InvestmentOpportunity(investments[i], revenues[i]));
        }

        int currentFunds = initialCapital;

        // Strategic investment rounds
        for (int round = 0; round < k; round++) {
            // Unlock newly affordable projects
            while (!affordableQueue.isEmpty() && 
                   affordableQueue.peek().investment <= currentFunds) {
                profitableQueue.add(affordableQueue.poll());
            }

            // Exit if no investments can be made
            if (profitableQueue.isEmpty()) {
                break;
            }

            // Select and complete the most profitable project
            currentFunds += profitableQueue.poll().revenue;
        }

        return currentFunds;
    }

    // ============ TESTING  ============
    public static void main(String[] args) {
        testCase(
            "Basic Scenario", 
            2, 0, 
            new int[]{2, 5, 8}, new int[]{0, 2, 3},
            7
        );

        testCase(
            "Optimal Growth Path", 
            3, 1, 
            new int[]{3, 6, 10}, new int[]{1, 3, 5}, 
            20
        );

        testCase(
            "No Viable Projects", 
            5, 0, 
            new int[]{10, 20}, new int[]{5, 10}, 
            0
        );

        testCase(
            "Zero Investment Round", 
            0, 100, 
            new int[]{50}, new int[]{20}, 
            100
        );
    }

    /**
     * Automated test case runner with validation
     */
    private static void testCase(String description, int k, int initialCapital, 
                               int[] revenues, int[] investments, int expected) {
        System.out.println("\n" + description);
        System.out.println("k: " + k + ", Initial capital: " + initialCapital);
        
        try {
            int result = maximizeCapital(k, initialCapital, revenues, investments);
            System.out.println("Result: " + result);
            System.out.println("Status: " + 
                (result == expected ? "PASSED" : "FAILED (Expected: " + expected + ")"));
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}

/* 
# Output

Basic Scenario
k: 2, Initial capital: 0
Result: 7
Status: PASSED

Optimal Growth Path
k: 3, Initial capital: 1
Result: 20
Status: PASSED

No Viable Projects
k: 5, Initial capital: 0
Result: 0
Status: PASSED

Zero Investment Round
k: 0, Initial capital: 100
Result: 100
Status: PASSED

*/