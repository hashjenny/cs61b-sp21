/*
Using everything you’ve learned so far on this homework, you’ll now create a function with the signature public static int max(int[] m) that returns the maximum value of an int array. You may assume that all of the numbers are greater than or equal to zero.

Modify the code below (also found here) so that max works as described. Furthermore, modify main so that the max method is called on the given array and its max printed out (in this case, it should print 22).
 */
public class Exercise2 {
        /** Returns the maximum value from m. */
        public static int max(int[] m) {
            var maximum = m[0];
            for (int i = 1; i < m.length; i++) {
                if (m[i] > maximum) maximum = m[i];

            }
            return maximum;
        }
        public static void main(String[] args) {
           int[] numbers = new int[]{9, 2, 15, 2, 22, 10, 6};  
           System.out.println(max(numbers));    
        }
}
