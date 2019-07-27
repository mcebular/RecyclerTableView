package com.mc0239.recyclertableviewexample;

import com.mc0239.recyclertableviewexample.rows.UserCheckable;

import java.util.Random;

public class SampleUserGenerator {

private static final String[]
        names = {"Ellie", "Trevor", "Hilly", "Julie", "Ebba", "Darin", "Mary", "Shelly", "Angie", "Nelson"},
        surnames = {"Sniders", "Danielson", "Romilly", "Durand", "Vipond", "Wakefield", "Wilson"};

    public static UserCheckable generateUser() {
        Random r = new Random();
        UserCheckable u = new UserCheckable();
        u.id = r.nextInt(1000);
        u.name = names[r.nextInt(names.length)];
        u.surname = surnames[r.nextInt(surnames.length)];
        u.username = u.name.substring(3) + u.surname.substring(3);
        u.checked = r.nextInt(100) < 10;
        return u;
    }
}
