package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Loads configuration for the DrawNumber app from a file.
 */
public final class ConfigurationResourceLoader {
    /**
     * Prevent initialization.
     */
    private ConfigurationResourceLoader() {

    }
    /**
     * Loads configuration from a resource name.
     * 
     * @param resourceName the name of the resource that as the game configuration.
     * @throws IOException if there is an error with loading the config file.
     */
    public static Configuration loadConfiguration(final String resourceName) throws IOException {
        final Configuration.Builder builder = new Configuration.Builder(); 
        final InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);
        if (is == null) {
            throw new FileNotFoundException("The configuration file was not found.");
        }
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            br.lines().forEach(line -> {
                final StringTokenizer st = new StringTokenizer(line, ":");
                if (st.countTokens() != 2 && st.countTokens() != 0) {
                    throw new IllegalArgumentException("The provided file is incorrectly formatted.");
                }
                final String key = st.nextToken();
                final int value = Integer.parseInt(st.nextToken().trim());
                switch (key) {
                    case "minimum" -> builder.setMin(value);
                    case "maximum" -> builder.setMax(value);
                    case "attempts" -> builder.setAttempts(value);
                    default -> {}
                }
            });
        }
        return builder.build();
    }
}
