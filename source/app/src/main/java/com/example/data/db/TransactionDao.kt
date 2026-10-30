package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TransactionItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionItem>>

    @Query("SELECT * FROM transactions WHERE isBioscience = 1 ORDER BY date DESC, createdAt DESC")
    fun getBioscienceTransactions(): Flow<List<TransactionItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionItem>)

    @Update
    suspend fun update(transaction: TransactionItem)

    @Delete
    suspend fun delete(transaction: TransactionItem)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}
