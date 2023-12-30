/*
Rewrite your solution to Exercise 2 
 */
public class Exercise3 {
        /** Returns the maximum value from m. */
        public static int max(int[] m) {
            var maximum = m[0];
            var i = 1;
            while (i < m.length) {
                if (m[i] > maximum) {
                    maximum = m[i];
                }
                i++;
            }
            return maximum;
        }
        public static void main(String[] args) {
           int[] numbers = new int[]{9, 2, 15, 2, 22, 10, 6};  
           System.out.println(max(numbers));    
        }
}
