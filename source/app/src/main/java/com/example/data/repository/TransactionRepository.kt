package com.example.data.repository

import com.example.data.db.TransactionDao
import com.example.data.model.TransactionItem
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {
    val allTransactions: Flow<List<TransactionItem>> = dao.getAllTransactions()
    val bioscienceTransactions: Flow<List<TransactionItem>> = dao.getBioscienceTransactions()

    suspend fun insertTransaction(transaction: TransactionItem): Long {
        return dao.insert(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionItem) {
        dao.update(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionItem) {
        dao.delete(transaction)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }

    suspend fun insertBatch(items: List<TransactionItem>) {
        dao.insertAll(items)
    }
}
