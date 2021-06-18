import java.util.Random;

/**
 * A class to contain general utility methods
 */
public class UtilityMethods {

    // A static random for all methods to use
    public static final Random RAND = new Random();

    /**
     * A method to shuffle a array using the 'Fisher and Yates' method <br>
     * source: https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
     * @param arr - the input array to be shuffled
     * @param <T> - the array's type
     */
    public static <T> void shuffleArray(T[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            // select a random index j such that 0 <= j <= i
            int j = RAND.nextInt(i + 1);
            // swap the element in position i with the element in position j
            T temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

}
