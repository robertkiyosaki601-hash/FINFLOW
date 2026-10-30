package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddTransactionDialog
import com.example.ui.components.EditBudgetDialog
import com.example.ui.screens.ActivityScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BioscienceScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.Slate400
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.SpendWiseViewModel
import kotlinx.coroutines.launch

@Composable
fun SpendWiseApp(
    viewModel: SpendWiseViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isModalOpen by viewModel.isModalOpen.collectAsStateWithLifecycle()
    val isBudgetDialogOpen by viewModel.isBudgetDialogOpen.collectAsStateWithLifecycle()
    val monthlyBudgetCap by viewModel.monthlyBudgetCap.collectAsStateWithLifecycle()
    val editingTransaction by viewModel.editingTransaction.collectAsStateWithLifecycle()
    val prefilledType by viewModel.prefilledType.collectAsStateWithLifecycle()
    val prefilledIsBio by viewModel.prefilledIsBio.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle Back Button to return to Dashboard if on another tab
    BackHandler(enabled = currentTab != NavigationTab.DASHBOARD) {
        viewModel.setTab(NavigationTab.DASHBOARD)
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = msg,
                    actionLabel = if (msg.startsWith("Deleted")) "Undo" else null,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.undoDelete()
                }
                viewModel.clearSnackbar()
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("spendwise_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("spendwise_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // Dashboard Tab
                NavigationBarItem(
                    selected = currentTab == NavigationTab.DASHBOARD,
                    onClick = { viewModel.setTab(NavigationTab.DASHBOARD) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary,
                        indicatorColor = IndigoPrimary.copy(alpha = 0.12f),
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    ),
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )

                // Activity / History Tab
                NavigationBarItem(
                    selected = currentTab == NavigationTab.ACTIVITY,
                    onClick = { viewModel.setTab(NavigationTab.ACTIVITY) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.ACTIVITY) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = "Activity"
                        )
                    },
                    label = { Text("Activity", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary,
                        indicatorColor = IndigoPrimary.copy(alpha = 0.12f),
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    ),
                    modifier = Modifier.testTag("nav_tab_activity")
                )

                // Bioscience Lab Tab
                NavigationBarItem(
                    selected = currentTab == NavigationTab.BIOSCIENCE,
                    onClick = { viewModel.setTab(NavigationTab.BIOSCIENCE) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.BIOSCIENCE) Icons.Filled.Biotech else Icons.Outlined.Biotech,
                            contentDescription = "BioScience"
                        )
                    },
                    label = { Text("BioLab", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0F766E),
                        selectedTextColor = Color(0xFF0F766E),
                        indicatorColor = Color(0xFF0F766E).copy(alpha = 0.15f),
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    ),
                    modifier = Modifier.testTag("nav_tab_bioscience")
                )

                // Analytics Tab
                NavigationBarItem(
                    selected = currentTab == NavigationTab.ANALYTICS,
                    onClick = { viewModel.setTab(NavigationTab.ANALYTICS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.ANALYTICS) Icons.Filled.Assessment else Icons.Outlined.Assessment,
                            contentDescription = "Analytics"
                        )
                    },
                    label = { Text("Analytics", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary,
                        indicatorColor = IndigoPrimary.copy(alpha = 0.12f),
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    ),
                    modifier = Modifier.testTag("nav_tab_analytics")
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val isBio = currentTab == NavigationTab.BIOSCIENCE
                    viewModel.openAddModal(type = "expense", isBioscience = isBio)
                },
                containerColor = if (currentTab == NavigationTab.BIOSCIENCE) Color(0xFF0F766E) else IndigoPrimary,
                contentColor = Color.White,
                shape = FloatingActionButtonDefaults.shape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier.testTag("fab_add_transaction")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Transaction",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                NavigationTab.ACTIVITY -> ActivityScreen(viewModel = viewModel)
                NavigationTab.BIOSCIENCE -> BioscienceScreen(viewModel = viewModel)
                NavigationTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
            }
        }
    }

    // Add / Edit Transaction Dialog
    if (isModalOpen) {
        AddTransactionDialog(
            initialType = prefilledType,
            initialIsBio = prefilledIsBio,
            editingTransaction = editingTransaction,
            onDismiss = { viewModel.closeModal() },
            onSave = { id, type, amount, category, desc, date, project, isBio ->
                viewModel.saveTransaction(
                    id = id,
                    type = type,
                    amount = amount,
                    category = category,
                    description = desc,
                    date = date,
                    projectCode = project,
                    isBioscience = isBio
                )
            }
        )
    }

    // Set / Edit Monthly Budget Cap Dialog
    if (isBudgetDialogOpen) {
        EditBudgetDialog(
            currentBudget = monthlyBudgetCap,
            onDismiss = { viewModel.closeBudgetDialog() },
            onSave = { newCap -> viewModel.setMonthlyBudgetCap(newCap) }
        )
    }
}
