package com.rocketchain.wallet;

/////////////////////////////////////////////////////////////////////////////////////////////////
// Account -> Output Ownerships
/////////////////////////////////////////////////////////////////////////////////////////////////
// Keys and Values (K, V) :
// A. (Account + '\0' + OutputOwnership, Dummy) => for Search 2, 3
// B. (OutputOwnership, OwnershipDescriptor) => for Search case 1
// C. (Account, OutputOwnership) => For keeping the receiving address. For Modification 3 and Search 4
//
// OwnershipDescriptor has the following fields.
// 1. Account     : String
// 1. privateKeys : List<PrivateKey>
//
// Modifications :
// 1. Add an output ownership to an account. Create an account if it does not exist.
// 2. Put the private key into an address.
// 3. Mark an address of an account as the receiving address.
//
// Searches :
// 1. Get an account by an address.
// 2. Iterate for each output ownerships for all accounts.
// 3. Iterate private keys for all accounts.
// 4. Get the receiving address of an account.
// 5. Get a private key for an address.


/////////////////////////////////////////////////////////////////////////////////////////////////
// Output Ownership -> Transactions
/////////////////////////////////////////////////////////////////////////////////////////////////
// Keys and Values (K, V) :
// A. ( OutputOwnership + '\0' + (transaction)Hash ) => For Search 1,2
//
// Modifications :
// 1. Put a transaction into the output ownership.
// 2. Remove a transaction from the output ownership by transaction hash.
//
// Searches :
// 1. Iterate transactions by providing an account and the skip count. Include watch-only ownerships.
// 2. Iterate transactions by providing an account and the skip count. Exclude watch-only ownerships.


/////////////////////////////////////////////////////////////////////////////////////////////////
// Output Ownership -> UTXOs
/////////////////////////////////////////////////////////////////////////////////////////////////
// Keys and Values (K, V) :
// A. ( OutputOwnership + '\0' + OutPoint, None ) => For Search 1, 2, 3
// B. ( OutPoint, WalletOutput ) => For Modification 2
//
// WalletOutput has the following fields :
// 1. spent : Boolean
// 1. transactionOutput : TransactionOutput
//
// Modifications :
// 1. Put a UTXO into the output ownership.
// 2. Mark a UTXO spent searching by OutPoint.
// 3. Remove a UTXO from the output ownership.
//
// Searches :
// 1. Iterate UTXOs for an output ownership.
// 2. Iterate UTXOs for all output ownerships. Filter UTXOs based on confirmations.
// 3. Iterate UTXOs for a given addresses. Filter UTXOs based on confirmations.


/////////////////////////////////////////////////////////////////////////////////////////////////
// (transaction)Hash -> Transaction
/////////////////////////////////////////////////////////////////////////////////////////////////
// Keys and Values (K, V) :
// A. ((transaction)Hash, WalletTransaction)
//
// Modifications :
// 1. Add a transaction.
// 2. Remove a transaction.
//
// Searches :
// 1. Search a transaction by the hash.


import com.google.common.collect.Lists;
import com.rocketchain.chain.transaction.OutputOwnership;
import com.rocketchain.chain.transaction.OutputOwnershipCodec;
import com.rocketchain.chain.transaction.PrivateKey;
import com.rocketchain.codec.*;
import com.rocketchain.codec.primitive.OneByteCodec;
import com.rocketchain.proto.*;
import com.rocketchain.storage.index.ClosableIterator;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.WalletException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/** A storage for the wallet.
 *
 *   The wallet store stores transactions and unspent outputs for a given output ownership.
 *   An example of an output ownership is coin address. A coin address owns an output.
 *   Also a public key script can be an output ownership.
 *
 *   The wallet store also stores a list of accounts. Each account has a list of output ownership.
 *
 *   To summarize,
 *   1. An account has multiple output ownerships.
 *   2. An output ownership has multiple transactions(either receiving UTXOs or spending UTXOs).
 *   3. An output ownership has multiple unspent outputs.
 *
 *   We need to keep track of the statuses of outputs depending on whether it was spent or not.
 *
 * Why not have this class in the storage layer?
 *   The storage layer keeps data for maintaining blockchain itself.
 *   We plan to have different implementations of the storage layer such as
 *     (1) keeping all blocks in each peer.  Ex> keep all blocks for N peers.
 *     (2) keeping some blocks in each peer. Ex> keep 1/N blocks for N peers.
 */
public class WalletStore {

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // OutPoint -> WalletOutput
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Keys and Values (K, V) :
    // A. (OutPoint, WalletOutput)
    //
    // Modifications :
    // 1. Add a transaction output.
    // 2. Remove a transaction output.
    //
    // Searches :
    // 1. Search a transaction output by the outpoint.

    // Naming convention rules
    // 1. Name the prefix with the name of data we store.
    //    Ex> OWNERSHIPS stores onerships for an account.
    // 2. Use plural if we keep multiple entities under an entity.
    //    Ex> OWNERSHIPS, TXHASHES, OUTPOINTS


    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Account -> Output Ownerships
    /////////////////////////////////////////////////////////////////////////////////////////////////

    // A. (Account + '\0' + OutputOwnership, Dummy)
    public static final byte OWNERSHIPS = (byte) 'O';

    // B. (OutputOwnership, OwnershipDescriptor)
    public static final byte OWNERSHIP_DESC = (byte) 'D';

    // C. (Account, OutputOwnership)
    public static final byte RECEIVING = (byte) 'R';

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Output Ownership -> Transactions
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // A. ( OutputOwnership + '\0' + (transaction)Hash )
    public static final byte TXHASHES = (byte) 'H';

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Output Ownership -> UTXOs
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // A. ( OutputOwnership + '\0' + OutPoint, None )
    public static final byte OUTPOINTS = (byte) 'P';

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // (transaction)Hash -> Transaction
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Keys and Values (K, V) :
    // A. ((transaction)Hash, WalletTransaction)
    public static final byte WALLETTX = (byte) 'T';

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // OutPoint -> WalletOutput
    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Keys and Values (K, V) :
    // A. (OutPoint, WalletOutput)
    public static final byte WALLETOUTPUT = (byte) 'U';


    /*******************************************************************************************************
     * Category : <Account -> Output Ownerships>
     *******************************************************************************************************/

    /** Add an output ownership to an account. Create an account if it does not exist.
     *
     * Category : <Account -> Output Ownerships> - Modification
     *
     * Used by : RPCs adding a address to an account.
     *   1. importaddress RPC.
     *   2. getnewaddress RPC.
     *
     * @param accountName The name of the account to create.
     * @param outputOwnership The address or public key script to add to the account.
     */
    public void putOutputOwnership(KeyValueDatabase db , String accountName , OutputOwnership outputOwnership  )  {
        // We don't need a value mapped here. Just use one byte 0 for the value.
        // As we are iterating output ownerships for an account by using key prefix, we don't need any value here.
        db.putPrefixedObject(new OutputOwnershipCodec(), new OneByteCodec(), OWNERSHIPS, accountName, outputOwnership, new OneByte((byte)0));
        db.putObject(new OutputOwnershipCodec(), new  OwnershipDescriptorCodec(), OWNERSHIP_DESC, outputOwnership,
                new OwnershipDescriptor(accountName, Lists.newArrayList()));
    }


    /** Delete an output ownership.
     *
     * This method is not used by Wallet yet, but is likely to be used in the future.
     *
     * @param accountName The name of the account to create.
     * @param outputOwnership The address or public key script to add to the account.
     */
    public void delOutputOwnership(KeyValueDatabase db , String accountName , OutputOwnership outputOwnership  ) {
        // We don't need a value mapped here. Just use one byte 0 for the value.
        // As we are iterating output ownerships for an account by using key prefix, we don't need any value here.
        db.delPrefixedObject(new OutputOwnershipCodec(), OWNERSHIPS, accountName, outputOwnership);
        db.delObject(new OutputOwnershipCodec(), OWNERSHIP_DESC, outputOwnership);
    }
    /** Put the receiving address into an account.
     *
     * Category : <Account -> Output Ownerships> - Modification
     */

    /** Mark an address of an account as the receiving address.
     *
     * Category : <Account -> Output Ownerships> - Modification
     *
     * @throws  if the address was not found.
     */
    public void putReceivingAddress(KeyValueDatabase db , String accountName , OutputOwnership outputOwnership  )  {
        db.putObject(new AccountCodec(), new OutputOwnershipCodec(), RECEIVING, new Account(accountName), outputOwnership);
    }

    /** Find an account by coin address.
     *
     * Used by : getaccount RPC.
     *
     * Category : <Account -> Output Ownerships> - Search
     *
     * @param ownership The output ownership, which is attached to the account.
     * @return The found account.
     */
    public String getAccount(KeyValueDatabase db , OutputOwnership ownership )  {
        OwnershipDescriptor account  = db.getObject(new OutputOwnershipCodec(), new OwnershipDescriptorCodec(), OWNERSHIP_DESC, ownership);
        return account.getAccount();
    }

    /** Get an iterator for each output ownerships for all accounts.
     *
     * Category : <Account -> Output Ownerships> - Search
     *
     * @param accountOption Some(account) to get ownerships for an account. None to get all ownerships for all accounts.
     */
    public List<OutputOwnership> getOutputOwnerships(KeyValueDatabase db , String accountOption ) {

        ClosableIterator<Pair<CStringPrefixed<OutputOwnership>, OneByte>> closableIterator;
        if (accountOption == null) {
            closableIterator = db.seekPrefixedObject(new OutputOwnershipCodec(), new OneByteCodec(), OWNERSHIPS);
        } else {
            closableIterator = db.seekPrefixedObject(new OutputOwnershipCodec(), new OneByteCodec(), OWNERSHIPS, accountOption);
        }

        List<OutputOwnership> list = Lists.newArrayList();
        while (closableIterator.hasNext()) {
            Pair<CStringPrefixed<OutputOwnership>, OneByte> pair = closableIterator.next();
            CStringPrefixed<OutputOwnership> cstringPrefixedKey = pair.getLeft();
            OutputOwnership ownership = cstringPrefixedKey.getData();
            list.add(ownership);
        }

        closableIterator.close();

        return list;
    }

    /** Get an iterator private keys for an address or all accounts.
     *
     * TODO : Instead of getting a CoinAddress, how about getting an OutputOwnership?
     *
     * Category : <Account -> Output Ownerships> - Search
     *
     * @param addressOption Some(address) to get private keys for an address. A Multisig address may have multiple keys for it.
     *                      None to get private keys for all accounts.
     */
    public List<PrivateKey> getPrivateKeys(KeyValueDatabase db , OutputOwnership addressOption )  {
        if (addressOption == null) {
            List<OutputOwnership> ownershipList = getOutputOwnerships(db, null);
            List<PrivateKey> privateKeyList = Lists.newArrayList();
            for (OutputOwnership outputOwnership : ownershipList) {
                privateKeyList.addAll(getPrivateKeys(db, outputOwnership));
            }

            return privateKeyList;
        } else {
            OwnershipDescriptor ownershipDescriptorOption  =
                    db.getObject(new OutputOwnershipCodec(), new OwnershipDescriptorCodec(), OWNERSHIP_DESC, addressOption);
            // Get PrivateKey object from base58 encoded private key string.
            List<PrivateKey> privateKeyListOption = ownershipDescriptorOption.getPrivateKeys()
                    .stream()
                    .map(it->PrivateKey.from(it))
                    .collect(Collectors.toList());
            // Get rid of the Option wrapper.
            return privateKeyListOption == null ?  Lists.newArrayList() : privateKeyListOption;
        }
    }

    /** Get the receiving address of an account.
     *
     * Category : <Account -> Output Ownerships> - Search
     */
    public OutputOwnership getReceivingAddress( KeyValueDatabase db , String account)  {
        return db.getObject(new AccountCodec(), new OutputOwnershipCodec(), RECEIVING, new  Account(account));
    }


    /** Put private keys for a coin address.
     *
     * After putting the private keys, we can sign transaction inputs
     * which are pointing to an output whose locking script has public key hashes,
     * which matches the private keys.
     *
     * We need to be able to put multiple private keys for multisig addresses.
     *
     * @param ownership The output ownership(ex>address) generated from the private key.
     * @param privateKeys The private key to put under the coin address.
     * @throws WalletException(ErrorCode) if the address was not found.
     */
    public void putPrivateKeys(KeyValueDatabase db , OutputOwnership ownership , List<PrivateKey> privateKeys )  {
        OwnershipDescriptor ownershipDescriptor  =
                db.getObject(new OutputOwnershipCodec(), new OwnershipDescriptorCodec(), OWNERSHIP_DESC, ownership);
        if (ownershipDescriptor == null) {
            throw new WalletException(ErrorCode.OwnershipNotFound);
        } else {
            ownershipDescriptor.setPrivateKeys(privateKeys.stream().map(it->it.base58()).collect(Collectors.toList()));
            db.putObject(new OutputOwnershipCodec(), new OwnershipDescriptorCodec(), OWNERSHIP_DESC, ownership, ownershipDescriptor);
        }
    }

    /** Check if an output exists.
     *
     * @param outputOwnership The output ownership to check.
     * @return true if the ownership exists; false otherwise.
     */
    public boolean ownershipExists(KeyValueDatabase db , OutputOwnership outputOwnership )  {
        OwnershipDescriptor ownershipOption = db.getObject(new OutputOwnershipCodec(), new OwnershipDescriptorCodec(), OWNERSHIP_DESC, outputOwnership);
        return ownershipOption != null;
    }
    /*******************************************************************************************************
     * Category : <Output Ownership -> TransactionHashes>
     *******************************************************************************************************/

    /** Put a transaction into the output ownership.
     *
     * Category : <Output Ownership -> Transactions> - Modification
     *
     * @throws WalletException(ErrorCode) if the output ownership was not found.
     */
    public void putTransactionHash(KeyValueDatabase db , OutputOwnership outputOwnership , Hash transactionHash )  {
        if (!ownershipExists(db, outputOwnership)) {
            throw new WalletException(ErrorCode.OwnershipNotFound);
        }

        db.putPrefixedObject(new HashCodec(), new OneByteCodec(), TXHASHES, outputOwnership.stringKey(), transactionHash, new OneByte((byte)0));
    }

    /** Remove a transaction from the output ownership by transaction hash.
     *
     * Category : <Output Ownership -> Transactions> - Modification
     */
    public void delTransactionHash(KeyValueDatabase db ,OutputOwnership outputOwnership , Hash transactionHash )  {
        db.delPrefixedObject(new HashCodec(), TXHASHES, outputOwnership.stringKey(), transactionHash);
    }

    /** Get an iterator of transaction hashes searched by an optional account.
     *
     * Category : <Output Ownership -> Transactions> - Search
     *
     * @param outputOwnershipOption Some(ownership) to get transactions hashes related to an output ownership
     *                              None to get all transaction hashes for all output ownerships.
     */
    public List<Hash> getTransactionHashes(KeyValueDatabase db , OutputOwnership outputOwnershipOption )  {
        ClosableIterator<Pair<CStringPrefixed<Hash>, OneByte>> closableIterator;

        if (outputOwnershipOption == null) {
            // seekPrefixedObject returns (key, value) pairs, whereas we need the value only. map the pair to the value(2nd).
            closableIterator = db.seekPrefixedObject(new HashCodec(), new OneByteCodec(), TXHASHES);
        } else {
            closableIterator = db.seekPrefixedObject(new HashCodec(), new OneByteCodec(), TXHASHES, outputOwnershipOption.stringKey());
        }

        List<Hash> hashList = Lists.newArrayList();
        while (closableIterator.hasNext()) {
            Pair<CStringPrefixed<Hash>, OneByte> pair = closableIterator.next();
            CStringPrefixed<Hash> cstringPrefixedKey = pair.getLeft();
            Hash hash = cstringPrefixedKey.getData();
            hashList.add(hash);
        }

        closableIterator.close();
        return hashList;
    }

    /*******************************************************************************************************
     * Category : Category : <Output Ownership -> OutPoint(UTXOs)>
     *******************************************************************************************************/
    /** Put a UTXO into the output ownership.
     *
     * Category : <Output Ownership -> UTXOs> - Modification
     *
     * @throws WalletException(ErrorCode) if the output ownership was not found.
     */
    public void putTransactionOutPoint(KeyValueDatabase db , OutputOwnership outputOwnership, OutPoint output )  {
        if (!ownershipExists(db, outputOwnership)) {
            throw new WalletException(ErrorCode.OwnershipNotFound);
        }

        db.putPrefixedObject(new OutPointCodec(), new OneByteCodec(), OUTPOINTS, outputOwnership.stringKey(), output, new OneByte((byte)0));
    }

    /** Remove a UTXO from the output ownership.
     *
     * Category : <Output Ownership -> UTXOs> - Modification
     */
    public void delTransactionOutPoint(KeyValueDatabase db , OutputOwnership outputOwnership, OutPoint output )  {
        db.delPrefixedObject(new OutPointCodec(), OUTPOINTS, outputOwnership.stringKey(), output);
    }


    /** Get an iterator for transaction outpoints
     *
     * @param outputOwnershipOption Some(ownership) to iterate UTXOs for a specific output ownership.
     *                              None to iterate UTXOs for all output ownership.
     * @return The iterator for outpoints.
     */
    List<OutPoint> getTransactionOutPoints(KeyValueDatabase db , OutputOwnership outputOwnershipOption )  {
        ClosableIterator<Pair<CStringPrefixed<OutPoint>, OneByte>> closableIterator;

        if (outputOwnershipOption == null) {
            // seekPrefixedObject returns (key, value) pairs, whereas we need the value only. map the pair to the value(2nd).
            closableIterator = db.seekPrefixedObject(new OutPointCodec(), new OneByteCodec(), OUTPOINTS);
        } else {
            closableIterator = db.seekPrefixedObject(new OutPointCodec(), new OneByteCodec(), OUTPOINTS, outputOwnershipOption.stringKey());
        }

        List<OutPoint> outPointList = Lists.newArrayList();
        while (closableIterator.hasNext()) {
            Pair<CStringPrefixed<OutPoint>, OneByte> pair = closableIterator.next();

            CStringPrefixed<OutPoint> cstringPrefixedKey = pair.getLeft();
            OutPoint outPoint = cstringPrefixedKey.getData();
            outPointList.add(outPoint);
        }

        closableIterator.close();
        return outPointList;
    }

    /*******************************************************************************************************
     * Category : <(transaction)Hash -> Transaction>
     *******************************************************************************************************/

    /** Add a transaction.
     *
     * Category : <(transaction)Hash -> Transaction> - Modification
     *
     */
    public void putWalletTransaction(KeyValueDatabase db , Hash transactionHash , WalletTransaction transaction )  {
        db.putObject(new HashCodec(), new WalletTransactionCodec(), WALLETTX, transactionHash, transaction);
    }

    /** Remove a transaction.
     *
     * Category : <(transaction)Hash -> Transaction> - Modification
     */
    public void delWalletTransaction(KeyValueDatabase db , Hash transactionHash)  {
        db.delObject(new HashCodec(), WALLETTX, transactionHash);
    }

    /** Search a transaction by the hash.
     *
     * Category : <(transaction)Hash -> Transaction> - Search
     */
    public WalletTransaction getWalletTransaction(KeyValueDatabase db , Hash transactionHash )  {
        return db.getObject(new HashCodec(), new WalletTransactionCodec(), WALLETTX, transactionHash);
    }


    /*******************************************************************************************************
     * Category : <OutPoint -> TransactionOutput>
     *******************************************************************************************************/

    /** Add a transaction output.
     *
     * Category : <OutPoint -> TransactionOutput> - Modifications
     */
    public void putWalletOutput(KeyValueDatabase db , OutPoint outPoint , WalletOutput walletOutput )  {
        db.putObject(new OutPointCodec(), new WalletOutputCodec(), WALLETOUTPUT, outPoint, walletOutput);
    }

    /** Remove a transaction output.
     *
     * Category : <OutPoint -> TransactionOutput> - Modifications
     */
    public void delWalletOutput(KeyValueDatabase db , OutPoint outPoint ) {
        db.delObject(new OutPointCodec(), WALLETOUTPUT, outPoint);
    }

    /** Search a transaction output by the outpoint.
     *
     * Category : <OutPoint -> TransactionOutput> - Search
     */
    public WalletOutput getWalletOutput(KeyValueDatabase db , OutPoint outPoint )  {
        return db.getObject(new OutPointCodec(), new WalletOutputCodec(), WALLETOUTPUT, outPoint);
    }

    /** Mark a UTXO spent searching by OutPoint.
     *
     * Category : <Output Ownership -> UTXOs> - Modification
     *
     * @return true if the output was found in the wallet; false otherwise.
     */
    public boolean markWalletOutputSpent(KeyValueDatabase db , OutPoint outPoint , boolean spent )  {
        WalletOutput outPointOption  = db.getObject(new OutPointCodec(), new WalletOutputCodec(), WALLETOUTPUT, outPoint);
        if (outPointOption == null) {
            return false;
        } else {

            outPointOption.setSpent(spent);
            db.putObject(new OutPointCodec(), new WalletOutputCodec(), WALLETOUTPUT, outPoint, outPointOption);
            return true;
        }
    }


}
