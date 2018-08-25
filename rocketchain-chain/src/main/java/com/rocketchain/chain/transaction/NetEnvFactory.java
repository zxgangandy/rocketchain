package com.rocketchain.chain.transaction;

import com.google.common.collect.ImmutableMap;
import com.rocketchain.proto.Hash;

import java.util.Map;

public class NetEnvFactory {

    private static Map<String, Class> EnvironmentByName = ImmutableMap.of(
            "mainnet", MainNetEnv.class,
            "testnet", TestNetEnv.class
    );

    Hash GenesisBlockHash = Hash.from("000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943");

    private static NetEnv activeEnv;


    /** Create an environment object based on the given environment name.
     *
     * @param environmentName The name of the environment.
     * @return The environment object.
     */
    public static NetEnv create(String environmentName) {
        try {
            activeEnv = (NetEnv) EnvironmentByName.get(environmentName).newInstance();
            return activeEnv;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /** Get the active chain environment.
     *
     * @return Some(env) if any chain environment is active. None otherwise.
     */
    public static NetEnv get()  {
        return activeEnv;
    }
}
