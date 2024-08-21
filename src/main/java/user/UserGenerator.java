package user;

import data.User;

import static utilities.Util.randomString;

public class UserGenerator {
    public static User getRandomUser() {

        return new User()
                .setEmail(randomString(8) + "@gmail.com")
                .setPassword(randomString(8))
                .setName(randomString(8));

    }
}