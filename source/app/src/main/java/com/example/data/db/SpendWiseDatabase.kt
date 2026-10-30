package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.TransactionItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TransactionItem::class], version = 1, exportSchema = false)
abstract class SpendWiseDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: SpendWiseDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SpendWiseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpendWiseDatabase::class.java,
                    "spendwise_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.transactionDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: TransactionDao) {
                val initialData = listOf(
                    TransactionItem(
                        type = "income",
                        category = "NIH / NSF Grant",
                        amount = 18500.0,
                        description = "NIH R01 Research Sub-Award Disbursal",
                        date = "2026-10-02",
                        projectCode = "NIH R01-AI14892",
                        isBioscience = true
                    ),
                    TransactionItem(
                        type = "income",
                        category = "Fellowship Stipend",
                        amount = 4250.0,
                        description = "Graduate Research Fellowship Stipend",
                        date = "2026-10-05",
                        projectCode = "NSF-BIO-2026-7A",
                        isBioscience = true
                    ),
                    TransactionItem(
                        type = "income",
                        category = "Salary",
                        amount = 3200.0,
                        description = "Biotech Academic Appointment",
                        date = "2026-10-15",
                        projectCode = "",
                        isBioscience = false
                    ),
                    TransactionItem(
                        type = "expense",
                        category = "Sequencing & Genomics",
                        amount = 2150.0,
                        description = "Illumina NovaSeq Reagents (100bp PE)",
                        date = "2026-10-08",
                        projectCode = "NIH R01-AI14892",
                        isBioscience = true
                    ),
                    TransactionItem(
                        type = "expense",
                        category = "Housing",
                        amount = 1350.0,
                        description = "Monthly Apartment Rent",
                        date = "2026-10-01",
                        projectCode = "",
                        isBioscience = false
                    ),
                    TransactionItem(
                        type = "expense",
                        category = "Cell Culture & Media",
                        amount = 480.0,
                        description = "Fetal Bovine Serum & DMEM High Glucose",
                        date = "2026-10-12",
                        projectCode = "NSF-BIO-2026-7A",
                        isBioscience = true
                    ),
                    TransactionItem(
                        type = "expense",
                        category = "Food & Dining",
                        amount = 145.50,
                        description = "Whole Foods Weekly Groceries",
                        date = "2026-10-19",
                        projectCode = "",
                        isBioscience = false
                    ),
                    TransactionItem(
                        type = "expense",
                        category = "Antibodies & Assays",
                        amount = 690.0,
                        description = "Phospho-Akt & GAPDH Monoclonal Abs",
                        date = "2026-10-21",
                        projectCode = "Cancer Inst Pilot #4",
                        isBioscience = true
                    ),
                    TransactionItem(
                        type = "expense",
                        category = "Transportation",
                        amount = 75.0,
                        description = "Campus Transit Monthly Pass",
                        date = "2026-10-10",
                        projectCode = "",
                        isBioscience = false
                    ),
                    TransactionItem(
                        type = "expense",
                        category = "Publication & APC Fees",
                        amount = 1950.0,
                        description = "Open Access Article Processing Charge",
                        date = "2026-10-25",
                        projectCode = "NIH R01-AI14892",
                        isBioscience = true
                    )
                )
                dao.insertAll(initialData)
            }
        }
    }
}
