public class Exercise1b {
    static void drawTriangle(int N) {
        for (int i = 1; i <= N; i++) {
            var n = i;
            while (n > 0) {
                System.out.print('*');
                n--;
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        drawTriangle(5);
    }
}
