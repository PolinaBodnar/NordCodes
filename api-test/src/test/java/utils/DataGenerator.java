package utils;

import java.security.SecureRandom;

public class DataGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Генерирует случайный токен заданной длины
     * @param length длина токена
     * @return сгенерированный токен
     */
    public static String generateRandomToken(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Длина токена должна быть положительной");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * Генерирует случайную сумму в заданном диапазоне
     * @param min минимальное значение
     * @param max максимальное значение
     * @return сгенерированная сумма, округленная до 2 знаков после запятой
     */
    public static double generateRandomAmount(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("Минимальное значение не может быть больше максимального");
        }

        return Math.round((min + (max - min) * RANDOM.nextDouble()) * 100.0) / 100.0;
    }

    /**
     * Генерирует стандартный токен длиной 32 символа
     * @return сгенерированный токен
     */
    public static String generateStandardToken() {
        return generateRandomToken(32);
    }
}