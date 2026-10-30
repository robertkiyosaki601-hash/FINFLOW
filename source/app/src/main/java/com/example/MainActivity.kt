package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.data.db.SpendWiseDatabase
import com.example.data.repository.TransactionRepository
import com.example.ui.SpendWiseApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SpendWiseViewModel
import com.example.ui.viewmodel.SpendWiseViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: SpendWiseViewModel by viewModels {
        val database = SpendWiseDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = TransactionRepository(database.transactionDao())
        val prefs = applicationContext.getSharedPreferences("spendwise_prefs", MODE_PRIVATE)
        SpendWiseViewModelFactory(repository, prefs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SpendWiseApp(viewModel = viewModel)
            }
        }
    }
}
