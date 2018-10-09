package com.rocketchain.chain;

import com.google.common.collect.Lists;
import com.rocketchain.chain.mining.BlockTemplate;
import com.rocketchain.chain.mining.TemporaryCoinsView;
import com.rocketchain.chain.transaction.CoinAddress;
import com.rocketchain.chain.transaction.CoinsView;
import com.rocketchain.codec.HashUtil;
import com.rocketchain.codec.TransactionCodec;
import com.rocketchain.proto.CoinbaseData;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.storage.index.TransactingKeyValueDatabase;
import com.rocketchain.storage.index.TransactionDescriptorIndex;
import com.rocketchain.utils.exception.ChainException;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class BlockMining {
    private Logger logger = LoggerFactory.getLogger(BlockMining.class);

    private KeyValueDatabase db;
    private TransactionDescriptorIndex txDescIndex ;
    private TransactionPool transactionPool ;
    private CoinsView coinsView ;

    public BlockMining(KeyValueDatabase db, TransactionDescriptorIndex txDescIndex, TransactionPool transactionPool, CoinsView coinsView) {
        this.db = db;
        this.txDescIndex = txDescIndex;
        this.transactionPool = transactionPool;
        this.coinsView = coinsView;
    }


    public KeyValueDatabase getDb() {
        return db;
    }

    public TransactionDescriptorIndex getTxDescIndex() {
        return txDescIndex;
    }

    public TransactionPool getTransactionPool() {
        return transactionPool;
    }

    public CoinsView getCoinsView() {
        return coinsView;
    }


    /** Get the template for creating a block containing a list of transactions.
     *
     * @return The block template which has a sorted list of transactions to include into a block.
     */
    public BlockTemplate getBlockTemplate(CoinbaseData coinbaseData , CoinAddress minerAddress , int maxBlockSize )  {
        // TODO : P1 - Use difficulty bits
        //val difficultyBits = getDifficulty()
        long difficultyBits = 10;


        int bytesPerTransaction = 128;
        int estimatedTransactionCount = maxBlockSize / bytesPerTransaction;

//    watch.start("candidateTransactions")

        List<Pair<Hash, Transaction>>  candidateTransactions = transactionPool.getOldestTransactions(db, estimatedTransactionCount);

//    watch.stop("candidateTransactions")

//    val newCandidates0 = transactionPool.storage.getOldestTransactionHashes(1)(db)
//    val newFirstCandidateHash0 = if (newCandidates0.isEmpty) None else Some(newCandidates0.head)


//    watch.start("validTransactions")

        //int candidateTxCount = 0;
        //int validTxCount = 0;

        List<Transaction> validTransactions = candidateTransactions.stream()
                .filter(pair -> {
                    //candidateTxCount += 1;
                    Hash txHash = pair.getKey();
                    if (txDescIndex.getTransactionDescriptor(db, txHash) == null) {
                        return true;
                    } else {
                        // Remove transactions from the pool if it is in a block as well.
                        //transactionPool.removeTransactionFromPool(txHash)(db)
                        return false;
                    }
                })
                .map(pair->{
                    Transaction transaction = pair.getValue();

                    //validTxCount += 1;
                    return transaction;
                })
                .collect(Collectors.toList());

        // Remove transactions from the pool if it is in a block as well.
        // BUGBUG : Can we make sure that a transaction is not in the pool if it is in a block?


        candidateTransactions.stream().filter(pair->{
            Hash txHash = pair.getKey();
            //val transaction = pair.second

            // Because we are concurrently putting transactions into the pool while putting blocks,
            // There can be some transactions in the pool as well as on txDescIndex, where only transactions in a block is stored.
            // Skip all transactions that has the transaction descriptor.

            // If the transaction descriptor exists, it means the transaction is in a block.
            return txDescIndex.getTransactionDescriptor(db, txHash) != null;
        }).forEach(pair->{
            Hash txHash = pair.getKey();
            //val transaction = pair.second

//        logger.info(s"A Transaction in a block removed from pool. Hash : ${txHash} ")
            transactionPool.removeTransactionFromPool(db, txHash);
        });


//    val newCandidates1 = transactionPool.storage.getOldestTransactionHashes(1)(db)
//    val newFirstCandidateHash1 = if (newCandidates1.isEmpty) None else Some(newCandidates1.head)


        Transaction generationTransaction =
                TransactionBuilder.newGenerationTransaction(coinbaseData, minerAddress);
//    watch.stop("validTransactions")


//    watch.start("selectTx")
        // Select transactions by priority and fee. Also, sort them.
        Pair<Integer, List<Transaction>> transactions = selectTransactions(generationTransaction, validTransactions, maxBlockSize);
        List<Transaction> sortedTransactions = transactions.getValue();
//    watch.stop("selectTx")

//    val firstCandidateHash = if (candidateTransactions.isEmpty) None else Some(candidateTransactions.head.*1)
//    val newCandidates2 = transactionPool.storage.getOldestTransactionHashes(1)(db)
        //   val newFirstCandidateHash2 = if (newCandidates2.isEmpty) None else Some(newCandidates2.head)

//    logger.info("Coin Miner stats : ${watch.toString()}, Candidate Tx Count : ${candidateTxCount}, Valid Tx Count : ${validTxCount}, Attachable Tx Count : ${txCount}")
        logger.info("Coin Miner stats : Candidate Tx Count : ${candidateTxCount}, Valid Tx Count : ${validTxCount}, Attachable Tx Count : ${txCount}");
        return new BlockTemplate(difficultyBits, sortedTransactions);
    }


    /** Select transactions to include into a block.
     *
     *  Order transactions by dependency and by fee(in descending order).
     *  List N transactions based on the priority and fee so that the serialzied size of block
     *  does not exceed the max size. (ex> 1MB)
     *
     *  <Called by>
     *  When a miner tries to create a block, we have to create a block template first.
     *  The block template has the transactions to keep in the block.
     *  In the block template, it has all fields set except the nonce and the timestamp.
     *
     *  The first criteria for ordering transactions in a block is the transaction dependency.
     *
     *  Why is ordering transactions in a block based on dependency is necessary?
     *    When blocks are reorganized, transactions in the block are detached the reverse order of the transactions stored in a block.
     *    Also, they are attached in the same order of the transactions stored in a block.
     *    The order of transactions in a block should be based on the dependency, otherwise, an outpoint in an input of a transaction may point to a non-existent transaction by the time it is attached.    *
     *
     *
     *  How?
     *    1. Create a priority queue that has complete(= all required transactions exist) transactions.
     *    2. The priority is based on the transaction fee, for now. In the future, we need to improve the priority to consider the amount of coin to transfer.
     *    3. Prepare a temporary transaction pool. The pool will be used to look up dependent transactions while trying to attach transactions.
     *    4. Try to attach each transaction in the input list depending on transactions on the temporary transaction pool instead of the transaction pool in Blockchain. (We should not actually attach the transaction, but just 'try to' attach the transaction without changing the "spent" in-point of UTXO.)
     *    5. For all complete transactions that can be attached, move from the input list to the priority queue.
     *    6. If there is any transaction in the priority queue, pick the best transaction with the highest priority into the temporary transaction pool, and Go to step 4. Otherwise, stop iteration.
     *
     * @param transactions The candidate transactions
     * @param maxBlockSize The maximum block size. The serialized block size including the block header and transactions should not exceed the size.
     * @return The count and list of transactions to put into a block.
     */
    public Pair<Integer, List<Transaction>> selectTransactions(Transaction generationTransaction, List<Transaction> transactions , int maxBlockSize )  {

        List<Transaction> candidateTransactions = Lists.newArrayList();
        candidateTransactions.addAll( transactions );
        List<Transaction> selectedTransactions = Lists.newArrayList();

        int BLOCK_HEADER_SIZE = 80;
        int MAX_TRANSACTION_LENGTH_SIZE = 9; // The max size of variable int encoding.
        int serializedBlockSize = BLOCK_HEADER_SIZE + MAX_TRANSACTION_LENGTH_SIZE;


        serializedBlockSize += new TransactionCodec().encode(generationTransaction).length;
        selectedTransactions.add(generationTransaction);


        // Create a temporary database just for checking if transactions can be attached.
        // We should never commit the tempDB.
        TransactingKeyValueDatabase tempDB = db.transacting();
        tempDB.beginTransaction();


        // Remove all transactions in the pool


        // For all attachable transactions, attach them, and move to the priority queue.
        //    val tempPoolDbPath = File(s"target/temp-tx-pool-for-mining-${Random.nextLong}")
        //    tempPoolDbPath.mkdir
        TemporaryCoinsView tempCoinsView = new TemporaryCoinsView(coinsView);

        try {
            // The TemporaryCoinsView with additional transactions in the temporary transaction pool.
            // TemporaryCoinsView returns coins in the transaction pool of the coinsView, which may not be included in tempTranasctionPoolIndex,
            // But this should be fine, because we are checking if a transaction can be attached without including the transaction pool of the coinsView.
            TransactionPriorityQueue txQueue = new TransactionPriorityQueue(tempCoinsView);

            TransactionMagnet txMagnet = new TransactionMagnet(txDescIndex, tempCoinsView.getTempTranasctionPoolIndex(),
                    tempCoinsView.getTempTranasctionTimeIndex() );

            int txCount = 0;
            Transaction newlySelectedTransaction = null;
            do {
                ListIterator<Transaction> iter = candidateTransactions.listIterator();
                int consequentNonAttachableTx = 0;
                // If only some of transaction is attachable, (ex> 1 out of 4000), the loop takes too long.
                // So get out of the loop if N consecutive transactions are not attachable.
                // BUGBUG : Need to Use a random value instead of 16
                while( consequentNonAttachableTx < 16 && iter.hasNext()) {
                    Transaction tx = iter.next();
                    Hash txHash = HashUtil.hashTransaction(tx);

                    // Test if it can be attached.
                    boolean isTxAttachable;

                    try {
                        txMagnet.attachTransaction(tempDB, txHash, tx,true, null, null, null);
                        isTxAttachable = true;
                    } catch(ChainException e) {
                        // The transaction can't be attached.
                        isTxAttachable = false;
                    }

                    if (isTxAttachable) {
                        //println("attachable : ${txHash}")
                        // move the the transaction queue
                        iter.remove();
                        txQueue.enqueue(tempDB, tx);

                        consequentNonAttachableTx = 0;
                    } else {
                        consequentNonAttachableTx += 1;
                    }
                }

                newlySelectedTransaction = txQueue.dequeue();
                //println("fromQueue : ${newlySelectedTransaction?.hash()}")

                //        println(s"newlySelectedTransaction ${newlySelectedTransaction}")

                if (newlySelectedTransaction != null) {
                    Transaction newTx = newlySelectedTransaction;
                    serializedBlockSize += new TransactionCodec().encode(newTx).length;
                    if (serializedBlockSize <= maxBlockSize) {
                        // Attach the transaction
                        txMagnet.attachTransaction(tempDB, HashUtil.hashTransaction(newTx), newTx, false, null, null, null);
                        selectedTransactions.add(newTx) ;
                    }
                }

            } while(newlySelectedTransaction != null && (serializedBlockSize <= maxBlockSize) );
            // Caution : serializedBlockSize is greater than the actual block size


// Test code is only for debugging purpose. never uncomment this code block
/*
      var txCount = 0

      val iter = transactions.iterator()

      println("all txs ${transactions}")
      while( iter.hasNext() && (serializedBlockSize <= maxBlockSize) ) {
        val tx: Transaction = iter.next()
//        println("selected tx : ${tx}, ${serializedBlockSize}, ${maxBlockSize}, ${iter.hasNext()}")
        val txHash = tx.hash()

        // Test if it can be atached.
        try {
          txMagnet.attachTransaction(tempDB, txHash, tx, checkOnly = true)
          serializedBlockSize += TransactionCodec.encode(tx).size

          if (serializedBlockSize <= maxBlockSize) {
            // Attach the transaction
            txMagnet.attachTransaction(tempDB, txHash, tx, checkOnly = false)

            selectedTransactions += tx
            txCount += 1

          }

        } catch(e: ChainException) {
          // The transaction can't be attached.
        }
      }
*/


/*
      if (selectedTransactions.size != selectedTransactions.toSet.size) {
        logger.error(s"Duplicate transactions found while creating a block : ${selectedTransactions.map(_.hash).mkString("\n")}")
        assert(false)
      }
*/
            return new MutablePair<>(txCount, selectedTransactions);

        } finally {
            tempDB.abortTransaction();
        }
    }

}
