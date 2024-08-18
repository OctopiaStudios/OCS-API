package fr.octopiastudios.api.utils.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUtils {
    private static final String[] numeric = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    private static final String[] alpha = new String[]{
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z"};

    public static String randomAlphaNumeric(Integer maxChar) {
        List<String> alphaText = new ArrayList<>();
        byte b;
        int j;
        String[] arrayOfString;
        for (j = (arrayOfString = alpha).length, b = 0; b < j; ) {
            String text = arrayOfString[b];
            alphaText.add(text);
            b++;
        }
        for (j = (arrayOfString = numeric).length, b = 0; b < j; ) {
            String nubmer = arrayOfString[b];
            alphaText.add(nubmer);
            b++;
        }
        String randomName = null;
        for (int i = 0; i < maxChar.intValue(); i++) {
            Random random = new Random();
            Integer rand = Integer.valueOf(random.nextInt(alphaText.size()));
            if (randomName == null)
                randomName = alphaText.get(rand.intValue());
            randomName = String.valueOf(randomName) + (String) alphaText.get(rand.intValue());
        }
        return randomName;
    }

    public static Integer randomNumeric(Integer maxChar) {
        List<String> alphaText = new ArrayList<>();
        byte b;
        int j;
        String[] arrayOfString;
        for (j = (arrayOfString = numeric).length, b = 0; b < j; ) {
            String nubmer = arrayOfString[b];
            alphaText.add(nubmer);
            b++;
        }
        String randomName = null;
        for (int i = 0; i < maxChar.intValue(); i++) {
            Random random = new Random();
            Integer rand = Integer.valueOf(random.nextInt(alphaText.size()));
            if (randomName == null)
                randomName = alphaText.get(rand.intValue());
            randomName = String.valueOf(randomName) + (String) alphaText.get(rand.intValue());
        }
        return Integer.valueOf(Integer.parseInt(randomName));
    }

    public static String randomString(Integer maxChar) {
        List<String> alphaText = new ArrayList<>();
        byte b;
        int j;
        String[] arrayOfString;
        for (j = (arrayOfString = alpha).length, b = 0; b < j; ) {
            String text = arrayOfString[b];
            alphaText.add(text);
            b++;
        }
        StringBuilder randomName = null;
        for (int i = 0; i < maxChar.intValue(); i++) {
            Random random = new Random();
            Integer rand = Integer.valueOf(random.nextInt(alphaText.size()));
            if (randomName == null) randomName = new StringBuilder(alphaText.get(rand.intValue()));
            randomName.append(alphaText.get(rand.intValue()));
        }
        return randomName.toString();
    }

    public static int randomInt(int min, int max) {
        Random rn = new Random();
        int range = max - min + 1;
        int randomNum = rn.nextInt(range) + min;
        return randomNum;
    }
}
