package it.unibo.mvc;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the controller and the app entry point.
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    private static final String DEFAULT_CONFIG_PATH = "config.yml";

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * Constructor.
     *
     * @param views
     *            the views to attach
     * @throws IOException 
     *            if the file is not found
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        Configuration config;
        try {
            config = ConfigurationResourceLoader.loadConfiguration(DEFAULT_CONFIG_PATH);
        } catch (final IOException e) {
            this.views.forEach(view -> {
                view.displayError(e.getMessage());
            });
            config = new Configuration.Builder().build();
        }
        this.model = new DrawNumberImpl(config.getMin(), config.getMax(), config.getAttempts());
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (final IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    @SuppressFBWarnings(
        value = "DM_EXIT",
        justification = "Acceptable for exercising purposes."
    )
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * Application entry point.
     *
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     *            if the default configuration file is not found
     */
    public static void main(final String... args) {
        try {
            new DrawNumberApp(
                new DrawNumberViewImpl(),
                new PrintStreamView(System.out),
                new PrintStreamView(System.getProperty("user.home") + File.separator + ".drawNumberConfig")
            );
        } catch (final FileNotFoundException e) {
            e.printStackTrace(); // NOPMD Accepted by tutor
        }
    }
}
