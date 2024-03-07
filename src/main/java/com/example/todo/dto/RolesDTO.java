package com.example.todo.dto;

public record RolesDTO(boolean admin, boolean user) {
    public static String ROLE_ADMIN = "ROLE_ADMIN";
    public static String ROLE_USER = "ROLE_USER";

    @Override
    public final String toString() {
        return (admin ? ROLE_ADMIN : "") + (user && admin ? ", ":"") + (user ? ROLE_USER : "");
    }

    public static RolesDTO fromString(String roles) {
        return new RolesDTO(roles.contains(ROLE_ADMIN), roles.contains(ROLE_USER));
    }
}
