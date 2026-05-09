public final class UniversityPolicy {

    public static final String UNIVERSITY_NAME = "ABC University";
    public static final double BONUS_RATE = 0.10;

    public static void showPolicyHeader() {
        System.out.println("=== University Policy ===");
    }

    public static double calculateBonus(double monthlyPayment) {
        return monthlyPayment * BONUS_RATE;
    }
}

/*
A final class cannot be inherited to prevent modification of its behavior.
*/
