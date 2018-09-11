package com.rocketchain.proto;

public class Account implements Transcodable {
    private String name;

    public Account(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
