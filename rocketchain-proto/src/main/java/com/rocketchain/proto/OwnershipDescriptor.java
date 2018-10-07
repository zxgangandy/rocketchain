package com.rocketchain.proto;

import java.util.List;

public class OwnershipDescriptor implements Transcodable {
    private String account ;
    private List<String> privateKeys ;

    public OwnershipDescriptor(String account, List<String> privateKeys) {
        this.account = account;
        this.privateKeys = privateKeys;
    }

    public String getAccount() {
        return account;
    }

    public List<String> getPrivateKeys() {
        return privateKeys;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPrivateKeys(List<String> privateKeys) {
        this.privateKeys = privateKeys;
    }
}
