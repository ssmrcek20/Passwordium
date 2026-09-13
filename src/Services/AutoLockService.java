package Services;

import Views.Prijava;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;

public class AutoLockService {
    private static final int TIMEOUT_MINUTES = 5;
    private static final long TIMEOUT_MS =
            TIMEOUT_MINUTES * 60 * 1000L;
    private static Timer timer;
    private static long lastActivity;
    private static AWTEventListener activityListener;
    private static boolean locked = false;
    private AutoLockService() {
    }

    public static void start() {
        stop();
        locked = false;
        lastActivity = System.currentTimeMillis();

        activityListener = event -> {
            if (!locked) {
                lastActivity = System.currentTimeMillis();
            }
        };

        Toolkit.getDefaultToolkit().addAWTEventListener(activityListener,
                AWTEvent.KEY_EVENT_MASK
                        | AWTEvent.MOUSE_EVENT_MASK
                        | AWTEvent.MOUSE_MOTION_EVENT_MASK
                        | AWTEvent.MOUSE_WHEEL_EVENT_MASK
        );


        timer = new Timer(1000, e -> {
            long inactiveTime = System.currentTimeMillis() - lastActivity;

            if (inactiveTime >= TIMEOUT_MS) {
                lock();
            }
        });

        timer.start();
    }


    public static void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }

        if (activityListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(activityListener);
            activityListener = null;
        }
    }


    private static void lock() {
        if (locked) {
            return;
        }
        locked = true;
        stop();
        VaultSession.lock();

        SwingUtilities.invokeLater(() -> {
            for (Window window : Window.getWindows()) {
                if (window.isDisplayable()) {
                    window.dispose();
                }
            }

            JOptionPane.showMessageDialog(null,
                    "Trezor je automatski zaključan zbog neaktivnosti.",
                    "Trezor zaključan", JOptionPane.INFORMATION_MESSAGE
            );

            new Prijava();
        });
    }
}