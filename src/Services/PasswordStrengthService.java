package Services;

import java.util.Set;

public class PasswordStrengthService {
    public enum Strength {VRLO_SLABA, SLABA, SREDNJA, JAKA, VRLO_JAKA}
    public static class Result {
        private final Strength strength;
        private final int score;
        private final String message;
        private final boolean accepted;

        public Result(Strength strength, int score, String message, boolean accepted) {
            this.strength = strength;
            this.score = score;
            this.message = message;
            this.accepted = accepted;
        }

        public Strength getStrength() {
            return strength;
        }

        public int getScore() {
            return score;
        }

        public String getMessage() {
            return message;
        }

        public boolean isAccepted() {
            return accepted;
        }
    }

    private static final int MIN_LENGTH = 12;

    private static final Set<String> COMMON_PASSWORDS =
            Set.of(
                    "password",
                    "password123",
                    "123456",
                    "12345678",
                    "123456789",
                    "qwerty",
                    "qwerty123",
                    "admin",
                    "admin123",
                    "letmein",
                    "welcome",
                    "iloveyou",
                    "lozinka",
                    "lozinka123"
            );

    private PasswordStrengthService() {
    }

    public static Result evaluate(char[] password, String username) {
        if (password == null || password.length == 0) {
            return new Result(Strength.VRLO_SLABA, 0, "Unesite lozinku.", false);
        }

        String value = new String(password);
        String lower = value.toLowerCase();

        if (password.length < MIN_LENGTH) {
            return new Result(Strength.VRLO_SLABA, 0, "Lozinka mora imati najmanje " + MIN_LENGTH + " znakova.", false);
        }

        if (COMMON_PASSWORDS.contains(lower)) {
            return new Result(Strength.VRLO_SLABA, 0, "Ova lozinka je previše česta.", false);
        }

        if (username != null && !username.isBlank() && lower.contains(username.toLowerCase())) {
            return new Result(Strength.SLABA, 1, "Lozinka ne smije sadržavati korisničko ime.", false);
        }

        int score = 0;
        boolean lowerCase = false;
        boolean upperCase = false;
        boolean digit = false;
        boolean special = false;

        for (char c : password) {
            if (Character.isLowerCase(c)) {
                lowerCase = true;
            }
            else if (Character.isUpperCase(c)) {
                upperCase = true;
            }
            else if (Character.isDigit(c)) {
                digit = true;
            }
            else {
                special = true;
            }
        }

        if (password.length >= 12) {
            score++;
        }
        if (password.length >= 16) {
            score++;
        }
        if (password.length >= 20) {
            score++;
        }

        int groups = 0;
        if (lowerCase) groups++;
        if (upperCase) groups++;
        if (digit) groups++;
        if (special) groups++;

        if (groups >= 3) {
            score++;
        }

        if (groups == 4) {
            score++;
        }

        if (imaJednostavanUzorak(lower)) {
            score -= 2;
        }

        if (imaMnogoPonavljanja(value)) {
            score--;
        }

        score = Math.max(0, Math.min(score, 5));
        Strength strength;
        String message;
        boolean accepted;

        if (score <= 1) {
            strength = Strength.VRLO_SLABA;
            message = "Lozinka je preslaba.";
            accepted = false;
        } else if (score == 2) {
            strength = Strength.SLABA;
            message = "Lozinka je slaba. " + "Povećajte duljinu ili složenost.";
            accepted = false;
        } else if (score == 3) {
            strength = Strength.SREDNJA;
            message = "Lozinka zadovoljava minimalne zahtjeve.";
            accepted = true;
        } else if (score == 4) {
            strength = Strength.JAKA;
            message = "Lozinka je jaka.";
            accepted = true;
        } else {
            strength = Strength.VRLO_JAKA;
            message = "Lozinka je vrlo jaka.";
            accepted = true;
        }

        return new Result(strength, score, message, accepted);
    }

    private static boolean imaJednostavanUzorak(String value) {
        String[] patterns = {
                "123456",
                "abcdef",
                "qwerty",
                "asdfgh",
                "111111",
                "000000"
        };

        for (String pattern : patterns) {
            if (value.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    private static boolean imaMnogoPonavljanja(String value) {
        if (value.length() < 4) {
            return false;
        }

        int ponavljanja = 1;
        int maksimum = 1;

        for (int i = 1; i < value.length(); i++) {
            if (value.charAt(i) == value.charAt(i - 1)) {
                ponavljanja++;
                maksimum = Math.max(maksimum, ponavljanja);
            } else {
                ponavljanja = 1;
            }
        }

        return maksimum >= 4;
    }
}