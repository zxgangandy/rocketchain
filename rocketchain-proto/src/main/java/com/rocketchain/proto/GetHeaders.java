package com.rocketchain.proto;

/** GetHeaders ; Return a headers packet containing the headers of blocks starting right after the last known hash
 * in the block locator object, up to hash_stop or 2000 blocks, whichever comes first.
 * To receive the next block headers, one needs to issue getheaders again with a new block locator object.
 * The getheaders command is used by thin clients to quickly download the block chain where the contents
 * of the transactions would be irrelevant (because they are not ours).
 * Keep in mind that some clients may provide headers of blocks which are invalid
 * if the block locator object contains a hash on the invalid branch.
 */
public class GetHeaders {

}
