/*
Creative Exercise 1a: Drawing a Triangle
Finally! A chance to do something on your own.

Your goal is to create a program that prints the following figure. Your code should use loops (i.e. shouldn’t just be five print statements, that’s no fun).

*
**
***
****
*****
You can either write the program from scratch, or you can copy and paste lines of code from this link. You may find System.out.print to be a useful alternative to System.out.println. The difference is that System.out.print does not include an automatic newline.

If you go the copy and paste route, note that lines may be used once, multiple times, or not at all.

Run your code and verify that it works correctly by comparing it by eye to the program above. In next week’s lab and hw, we’ll discuss more sophisticated ways of verifying program correctness.

Save your code someplace (say by emailing it to yourself), as you’ll need it again soon.
 */
public class Exercise1a {
    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            var n = i;
            while (n > 0) {
                System.out.print('*');
                n --;
            }
            System.out.println();
        }
    }
}
