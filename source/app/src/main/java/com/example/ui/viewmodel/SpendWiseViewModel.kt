package com.example.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.TransactionItem
import com.example.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class NavigationTab {
    DASHBOARD,
    ACTIVITY,
    BIOSCIENCE,
    ANALYTICS
}

enum class DateFilter(val label: String) {
    ALL("All Time"),
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month")
}

data class FinancialStats(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0,
    val savingsRate: Double = 0.0,
    val bioIncome: Double = 0.0,
    val bioExpenses: Double = 0.0,
    val bioBalance: Double = 0.0
)

data class MonthlyBudgetInfo(
    val budgetCap: Double = 7500.0,
    val spentThisMonth: Double = 0.0,
    val remainingBudget: Double = 7500.0,
    val spentPercentage: Float = 0f,
    val isExceeded: Boolean = false,
    val monthName: String = "This Month"
)

class SpendWiseViewModel(
    private val repository: TransactionRepository,
    private val preferences: SharedPreferences? = null
) : ViewModel() {

    private val _currentTab = MutableStateFlow(NavigationTab.DASHBOARD)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _typeFilter = MutableStateFlow("All") // "All", "expense", "income"
    val typeFilter: StateFlow<String> = _typeFilter.asStateFlow()

    private val _categoryFilter = MutableStateFlow<String?>(null)
    val categoryFilter: StateFlow<String?> = _categoryFilter.asStateFlow()

    private val _dateFilter = MutableStateFlow(DateFilter.ALL)
    val dateFilter: StateFlow<DateFilter> = _dateFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isModalOpen = MutableStateFlow(false)
    val isModalOpen: StateFlow<Boolean> = _isModalOpen.asStateFlow()

    private val _editingTransaction = MutableStateFlow<TransactionItem?>(null)
    val editingTransaction: StateFlow<TransactionItem?> = _editingTransaction.asStateFlow()

    private val _prefilledType = MutableStateFlow("expense")
    val prefilledType: StateFlow<String> = _prefilledType.asStateFlow()

    private val _prefilledIsBio = MutableStateFlow(false)
    val prefilledIsBio: StateFlow<Boolean> = _prefilledIsBio.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val initialBudgetCap = preferences?.getFloat("monthly_budget_cap", 7500.0f)?.toDouble() ?: 7500.0
    private val _monthlyBudgetCap = MutableStateFlow(initialBudgetCap)
    val monthlyBudgetCap: StateFlow<Double> = _monthlyBudgetCap.asStateFlow()

    private val _isBudgetDialogOpen = MutableStateFlow(false)
    val isBudgetDialogOpen: StateFlow<Boolean> = _isBudgetDialogOpen.asStateFlow()

    private var lastDeletedTransaction: TransactionItem? = null

    val rawTransactions: StateFlow<List<TransactionItem>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val monthlyBudgetInfo: StateFlow<MonthlyBudgetInfo> = combine(
        rawTransactions,
        _monthlyBudgetCap
    ) { list, cap ->
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val monthPrefix = String.format(Locale.US, "%04d-%02d", year, month)
        val monthName = SimpleDateFormat("MMMM", Locale.US).format(calendar.time)

        val thisMonthExpenses = list
            .filter { it.type == "expense" && it.date.startsWith(monthPrefix) }
            .sumOf { it.amount }

        val remaining = cap - thisMonthExpenses
        val percentage = if (cap > 0) ((thisMonthExpenses / cap) * 100.0).toFloat() else 0f
        val isExceeded = thisMonthExpenses > cap

        MonthlyBudgetInfo(
            budgetCap = cap,
            spentThisMonth = thisMonthExpenses,
            remainingBudget = remaining,
            spentPercentage = percentage,
            isExceeded = isExceeded,
            monthName = monthName
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MonthlyBudgetInfo(budgetCap = initialBudgetCap)
    )

    val stats: StateFlow<FinancialStats> = rawTransactions.combine(_currentTab) { list, _ ->
        val income = list.filter { it.type == "income" }.sumOf { it.amount }
        val expenses = list.filter { it.type == "expense" }.sumOf { it.amount }
        val balance = income - expenses
        val savingsRate = if (income > 0) ((income - expenses) / income * 100.0).coerceAtLeast(0.0) else 0.0

        val bioList = list.filter { it.isBioscience }
        val bioInc = bioList.filter { it.type == "income" }.sumOf { it.amount }
        val bioExp = bioList.filter { it.type == "expense" }.sumOf { it.amount }
        val bioBal = bioInc - bioExp

        FinancialStats(
            totalIncome = income,
            totalExpenses = expenses,
            balance = balance,
            savingsRate = savingsRate,
            bioIncome = bioInc,
            bioExpenses = bioExp,
            bioBalance = bioBal
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinancialStats()
    )

    val categoryBreakdown: StateFlow<List<Pair<String, Double>>> = rawTransactions.combine(_currentTab) { list, _ ->
        val expenseList = list.filter { it.type == "expense" }
        val map = mutableMapOf<String, Double>()
        for (item in expenseList) {
            map[item.category] = (map[item.category] ?: 0.0) + item.amount
        }
        map.toList().sortedByDescending { it.second }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val bioscienceGrantBreakdown: StateFlow<List<Pair<String, Double>>> = rawTransactions.combine(_currentTab) { list, _ ->
        val bioExpenses = list.filter { it.isBioscience && it.type == "expense" }
        val map = mutableMapOf<String, Double>()
        for (item in bioExpenses) {
            val key = if (item.projectCode.isNotBlank()) item.projectCode else item.category
            map[key] = (map[key] ?: 0.0) + item.amount
        }
        map.toList().sortedByDescending { it.second }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredTransactions: StateFlow<List<TransactionItem>> = combine(
        rawTransactions,
        _typeFilter,
        _categoryFilter,
        _dateFilter,
        _searchQuery
    ) { list, type, cat, dateFilt, query ->
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)
        val currentMonth = now.get(Calendar.MONTH) + 1 // 1-12

        val lastMonthYear = if (currentMonth == 1) currentYear - 1 else currentYear
        val lastMonth = if (currentMonth == 1) 12 else currentMonth - 1

        val currentMonthPrefix = String.format(Locale.US, "%04d-%02d", currentYear, currentMonth)
        val lastMonthPrefix = String.format(Locale.US, "%04d-%02d", lastMonthYear, lastMonth)

        list.filter { item ->
            // Type filter
            val matchesType = when (type.lowercase(Locale.ROOT)) {
                "expense" -> item.type == "expense"
                "income" -> item.type == "income"
                else -> true
            }

            // Category filter
            val matchesCategory = cat == null || item.category.equals(cat, ignoreCase = true)

            // Date filter
            val matchesDate = when (dateFilt) {
                DateFilter.THIS_MONTH -> item.date.startsWith(currentMonthPrefix)
                DateFilter.LAST_MONTH -> item.date.startsWith(lastMonthPrefix)
                DateFilter.ALL -> true
            }

            // Query filter
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                item.description.contains(query, ignoreCase = true) ||
                        item.category.contains(query, ignoreCase = true) ||
                        item.projectCode.contains(query, ignoreCase = true)
            }

            matchesType && matchesCategory && matchesDate && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun setTypeFilter(type: String) {
        _typeFilter.value = type
    }

    fun setCategoryFilter(category: String?) {
        _categoryFilter.value = category
    }

    fun setDateFilter(filter: DateFilter) {
        _dateFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openAddModal(type: String = "expense", isBioscience: Boolean = false) {
        _editingTransaction.value = null
        _prefilledType.value = type
        _prefilledIsBio.value = isBioscience
        _isModalOpen.value = true
    }

    fun openEditModal(transaction: TransactionItem) {
        _editingTransaction.value = transaction
        _prefilledType.value = transaction.type
        _prefilledIsBio.value = transaction.isBioscience
        _isModalOpen.value = true
    }

    fun closeModal() {
        _isModalOpen.value = false
        _editingTransaction.value = null
    }

    fun saveTransaction(
        id: Long = 0,
        type: String,
        amount: Double,
        category: String,
        description: String,
        date: String,
        projectCode: String,
        isBioscience: Boolean
    ) {
        viewModelScope.launch {
            val item = TransactionItem(
                id = id,
                type = type,
                amount = amount,
                category = category,
                description = description,
                date = date,
                projectCode = projectCode.trim(),
                isBioscience = isBioscience,
                createdAt = System.currentTimeMillis()
            )
            if (id == 0L) {
                repository.insertTransaction(item)
                _snackbarMessage.value = "Transaction added successfully"
            } else {
                repository.updateTransaction(item)
                _snackbarMessage.value = "Transaction updated successfully"
            }
            closeModal()
        }
    }

    fun deleteTransaction(item: TransactionItem) {
        viewModelScope.launch {
            lastDeletedTransaction = item
            repository.deleteTransaction(item)
            _snackbarMessage.value = "Deleted \"${item.description}\""
        }
    }

    fun undoDelete() {
        val item = lastDeletedTransaction ?: return
        viewModelScope.launch {
            repository.insertTransaction(item.copy(id = 0))
            lastDeletedTransaction = null
            _snackbarMessage.value = "Restored \"${item.description}\""
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.clearAll()
            val sampleData = listOf(
                TransactionItem(
                    type = "income",
                    category = "NIH / NSF Grant",
                    amount = 25000.0,
                    description = "NIH R01-AI14892 Q4 Research Allocation",
                    date = getCurrentDateString(),
                    projectCode = "NIH R01-AI14892",
                    isBioscience = true
                ),
                TransactionItem(
                    type = "income",
                    category = "Fellowship Stipend",
                    amount = 4500.0,
                    description = "Monthly Bioscience Fellowship Stipend",
                    date = getCurrentDateString(),
                    projectCode = "NSF-BIO-2026-7A",
                    isBioscience = true
                ),
                TransactionItem(
                    type = "income",
                    category = "Salary",
                    amount = 3500.0,
                    description = "Biotech Academic Appointment",
                    date = getCurrentDateString(),
                    projectCode = "",
                    isBioscience = false
                ),
                TransactionItem(
                    type = "expense",
                    category = "Sequencing & Genomics",
                    amount = 3200.0,
                    description = "NovaSeq 6000 300-cycle Flow Cell",
                    date = getCurrentDateString(),
                    projectCode = "NIH R01-AI14892",
                    isBioscience = true
                ),
                TransactionItem(
                    type = "expense",
                    category = "Reagents & Chemicals",
                    amount = 640.0,
                    description = "CRISPR-Cas9 Editing Kits & Guide RNAs",
                    date = getCurrentDateString(),
                    projectCode = "Cancer Inst Pilot #4",
                    isBioscience = true
                ),
                TransactionItem(
                    type = "expense",
                    category = "Housing",
                    amount = 1400.0,
                    description = "Apartment Rent & Building Utilities",
                    date = getCurrentDateString(),
                    projectCode = "",
                    isBioscience = false
                ),
                TransactionItem(
                    type = "expense",
                    category = "Food & Dining",
                    amount = 128.45,
                    description = "Groceries & Nutrition",
                    date = getCurrentDateString(),
                    projectCode = "",
                    isBioscience = false
                ),
                TransactionItem(
                    type = "expense",
                    category = "Publication & APC Fees",
                    amount = 2100.0,
                    description = "Peer-reviewed Open Access Publication",
                    date = getCurrentDateString(),
                    projectCode = "NIH R01-AI14892",
                    isBioscience = true
                )
            )
            repository.insertBatch(sampleData)
            _snackbarMessage.value = "Reset to sample dataset"
        }
    }

    fun openBudgetDialog() {
        _isBudgetDialogOpen.value = true
    }

    fun closeBudgetDialog() {
        _isBudgetDialogOpen.value = false
    }

    fun setMonthlyBudgetCap(newCap: Double) {
        if (newCap > 0) {
            _monthlyBudgetCap.value = newCap
            preferences?.edit()?.putFloat("monthly_budget_cap", newCap.toFloat())?.apply()
            _snackbarMessage.value = "Monthly budget cap set to $${String.format(Locale.US, "%,.2f", newCap)}"
            closeBudgetDialog()
        }
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }
}

class SpendWiseViewModelFactory(
    private val repository: TransactionRepository,
    private val preferences: SharedPreferences? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpendWiseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpendWiseViewModel(repository, preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
