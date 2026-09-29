package company.vk.edu.distrib.compute.robert.urlshortener.utils;

public record Credentials(
    String user,
    String password
) {
    public static Credentials from(String str) {
        if (str == null) {
            throw new IllegalArgumentException();
        }

        String[] list = str.split(":", 2);
        if (list.length != 2) {
            throw new IllegalArgumentException();
        }

        return new Credentials(list[0], list[1]);
    }
}
