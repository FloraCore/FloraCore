package team.floracore.common.api;

import team.floracore.api.*;

import java.lang.reflect.*;

/**
 * FC API的注册类。
 */
public class ApiRegistrationUtil {
    private static final Method REGISTER;
    private static final Method UNREGISTER;

    static {
        try {
            REGISTER = FloraCoreProvider.class.getDeclaredMethod("register", FloraCore.class);
            REGISTER.setAccessible(true);

            UNREGISTER = FloraCoreProvider.class.getDeclaredMethod("unregister");
            UNREGISTER.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void registerProvider(FloraCore floraCoreApi) {
        try {
            REGISTER.invoke(null, floraCoreApi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unregisterProvider() {
        try {
            UNREGISTER.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
