
package com.rocketchain.net.message;

/**
 * Factory interface to create messages
 *
 */
public interface MessageFactory {


    Message create(byte code, byte[] encoded);

}
