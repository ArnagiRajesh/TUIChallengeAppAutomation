package com.TUIChallengeApp.TUIChallenge;

import java.lang.reflect.Field;

import io.appium.java_client.pagefactory.AppiumFieldDecorator;

/** Initializes Appium-annotated fields using the Appium 11 decorator API. */
public final class AppiumPageFactory {

    private AppiumPageFactory() {
    }

    public static void initElements(AppiumFieldDecorator decorator, Object page) {
        Class<?> type = page.getClass();
        while (type != Object.class) {
            for (Field field : type.getDeclaredFields()) {
                Object element = decorator.decorate(page.getClass().getClassLoader(), field);
                if (element == null) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    field.set(page, element);
                } catch (IllegalAccessException exception) {
                    throw new IllegalStateException("Unable to initialize Appium field " + field.getName(), exception);
                }
            }
            type = type.getSuperclass();
        }
    }
}
