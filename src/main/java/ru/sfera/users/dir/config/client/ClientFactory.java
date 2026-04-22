package ru.sfera.users.dir.config.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public interface ClientFactory {

    /**
     * Строит детерминированный ключ по паре (username, password) для использования
     * в качестве ключа кэша WebClient.
     *
     * @param username логин пользователя
     * @param password пароль пользователя в открытом виде
     * @return hex-строка с SHA-256 хэшем от "username:password"
     */
    default String buildKey(String username, String password) throws IllegalStateException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            var input = (username + ":" + password).getBytes(StandardCharsets.UTF_8);
            byte[] digest = md.digest(input);
            var sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
