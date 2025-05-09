import java.util.Random;

public class RandomNumberGenerator{
    Random rand;

    public RandomNumberGenerator(){
        rand = new Random();
    }

    public int getRandomNumber(int max){
        return rand.nextInt(max);
    }

    public int getRandomNumberInRange(int min, int max){
        return rand.nextInt(max-min+1) + min;
    }

    public int rollDice(){
        return getRandomNumberInRange(1, 6);
    }
}