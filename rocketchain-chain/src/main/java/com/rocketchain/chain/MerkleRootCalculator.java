package com.rocketchain.chain;

import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import com.rocketchain.codec.HashUtil;
import com.rocketchain.crypto.HashFunctions;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;

import java.util.List;

/**
 * Calculates Merkle root hash from transactions.
 */
public class MerkleRootCalculator {

    /** Concatenate two hash values and calculate double SHA256 on it.
     *
     * @param hash1 The first hash value
     * @param hash2 The second hash value
     * @return The hash value calcuated from the concatenated hash values.
     */
    private static Hash mergeHash(Hash hash1 , Hash hash2 )  {
        byte[] concatenated = Bytes.concat(hash1.getValue().getArray() ,hash2.getValue().getArray());

        return new Hash( HashFunctions.hash256(concatenated).getValue() );
    }
    /** Calculate the merkle root hash. The number of input hash values are always even.
     *
     * @param hashes The list of hashes for calculating the merkle root hash.
     * @return
     */
    private static List<Hash>  mergeHashes(List<Hash> hashes )  {
        // The number of hashes should be even.
        // TODO : Optimize, i%2==0 could be expensive.
        assert( hashes.size() % 2 == 0);

        // Note : We may duplicate the last element, so prepare space for one more element in the array buffer.
        List<Hash> mergedHashes = Lists.newArrayList(); //(hashes.length/2 + 1)

        for (int i= 0; i<  hashes.size(); i++) {
            // TODO : Optimize, i%2==0 could be expensive.
            if (i % 2 == 0) {
                mergedHashes.add(mergeHash( hashes.get(i), hashes.get(i+1) ));
            }
        }

        return calculateMerkleRoot(mergedHashes);
    }


    /** Duplicate the last item of the number of hash values is odd, and call calculateAlwaysEven.
     *
     * @param hashes The list of hashes for calculating the merkle root hash.
     * @return
     */
    private static List<Hash> calculateMerkleRoot(List<Hash> hashes )  {
        assert (hashes.size() > 0);
        if (hashes.size() == 1) { // The base condition. If the number of hashes is one, we are done.
            return hashes;
        } else {
            // TODO : Optimize, i%2==0 could be expensive.
            if (hashes.size() % 2 == 1) { // If the number of hashes is odd, duplicate the last one to make it even.
                Hash last = hashes.get(hashes.size()-1);
                hashes.add(last);
            }
            return mergeHashes(hashes);
        }
    }

    /** Calculate merkle root hash from a list of transactions.
     *
     * Recursive invocation steps:
     * calculate
     *   -> calculateMerkleRoot
     *      -> mergeHashes
     *         -> calculateMerkleRoot
     *            -> mergeHashes
     *               ... when it reaches the base condition, the invocation finishes ...
     *
     * @param transactions The list of transactions for calculating the merkle root hash.
     * @return The calculated merkle root hash.
     */
    public static Hash calculate(List<Transaction> transactions) {
        // Step 1 : Calculate transaction hashes for each transaction.
        // Note : We may duplicate the last element, so prepare space for one more element in the array buffer.
        List<Hash> transactionHashes = Lists.newArrayList();//(transactions.length + 1)
        transactions.stream().forEach(transaction -> {
            transactionHashes.add(HashUtil.hashTransaction(transaction));
        });

        // Step 2 : Duplicate the last hash item if the number of hashes is odd, and calculate the merkle root hash.
        List<Hash> merkleRootHashes = calculateMerkleRoot(transactionHashes);

        assert (merkleRootHashes.size() == 1);
        return merkleRootHashes.get(0);
    }

}
