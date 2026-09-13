package Services;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGeneratorService {
    private static final String LOWERCASE =
            "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS =
            "0123456789";
    private static final String SPECIAL =
            "!@#$%^&*()-_=+[]{};:,.<>?";
    private final SecureRandom secureRandom = new SecureRandom();

    public String generatePassword(int length, boolean lowercase, boolean uppercase, boolean numbers, boolean special) {

        List<String> selectedSets = new ArrayList<>();

        if (lowercase) {
            selectedSets.add(LOWERCASE);
        }

        if (uppercase) {
            selectedSets.add(UPPERCASE);
        }

        if (numbers) {
            selectedSets.add(NUMBERS);
        }

        if (special) {
            selectedSets.add(SPECIAL);
        }

        if (selectedSets.isEmpty()) {
            throw new IllegalArgumentException("Potrebno je odabrati barem jednu vrstu znakova.");
        }

        if (length < selectedSets.size()) {
            throw new IllegalArgumentException("Duljina lozinke je premala za odabrane vrste znakova.");
        }

        StringBuilder allowedCharacters = new StringBuilder();

        for (String set : selectedSets) {
            allowedCharacters.append(set);
        }

        List<Character> password = new ArrayList<>();

        for (String set : selectedSets) {

            int index = secureRandom.nextInt(set.length());

            password.add(set.charAt(index));
        }

        while (password.size() < length) {
            int index = secureRandom.nextInt(allowedCharacters.length());
            password.add(allowedCharacters.charAt(index));
        }

        Collections.shuffle(password, secureRandom);

        StringBuilder result = new StringBuilder();

        for (char c : password) {
            result.append(c);
        }

        return result.toString();
    }
}