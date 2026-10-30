package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CategoryBreakdownCard
import com.example.ui.theme.ExpenseRose
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.ScienceCyan
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.viewmodel.SpendWiseViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: SpendWiseViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val categoryBreakdown by viewModel.categoryBreakdown.collectAsStateWithLifecycle()
    val transactions by viewModel.rawTransactions.collectAsStateWithLifecycle()

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Analytics & Insights",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Cash flow health and allocation ratios",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = IndigoPrimary.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
            }
        }

        // Key Ratios Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "FINANCIAL METRICS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate500,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("SAVINGS / RETENTION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                            Text(
                                text = String.format(Locale.US, "%.1f%%", stats.savingsRate),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (stats.savingsRate > 20) IncomeGreen else Slate500
                            )
                        }

                        Column {
                            Text("LAB EXPENSE RATIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                            val bioRatio = if (stats.totalExpenses > 0) {
                                (stats.bioExpenses / stats.totalExpenses * 100).toFloat()
                            } else 0f
                            Text(
                                text = String.format(Locale.US, "%.1f%%", bioRatio),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F766E)
                            )
                        }

                        Column {
                            Text("NET MARGIN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                            val margin = if (stats.totalIncome > 0) {
                                (stats.balance / stats.totalIncome * 100).toFloat()
                            } else 0f
                            Text(
                                text = String.format(Locale.US, "%.1f%%", margin),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (margin >= 0) IncomeGreen else ExpenseRose
                            )
                        }
                    }
                }
            }
        }

        // Full Category Breakdown
        item {
            CategoryBreakdownCard(
                breakdown = categoryBreakdown,
                totalExpenses = stats.totalExpenses,
                title = "Total Expense Distribution",
                subtitle = "All Categories"
            )
        }

        // Export / Share Report Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Export & Summary",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Generate and share an executive summary of your income, expenses, and science grant balances.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val report = buildString {
                                appendLine("=== SpendWise Financial & Grant Report ===")
                                appendLine("Total Balance: ${currencyFormat.format(stats.balance)}")
                                appendLine("Total Income: ${currencyFormat.format(stats.totalIncome)}")
                                appendLine("Total Expenses: ${currencyFormat.format(stats.totalExpenses)}")
                                appendLine("Savings/Retention Rate: ${String.format(Locale.US, "%.1f%%", stats.savingsRate)}")
                                appendLine()
                                appendLine("--- Bioscience & Grant Accounting ---")
                                appendLine("Grant Balance: ${currencyFormat.format(stats.bioBalance)}")
                                appendLine("Grant Inflows: ${currencyFormat.format(stats.bioIncome)}")
                                appendLine("Lab Burn: ${currencyFormat.format(stats.bioExpenses)}")
                                appendLine()
                                appendLine("--- Total Transactions: ${transactions.size} ---")
                            }
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, report)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share SpendWise Report")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("share_report_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Financial & Lab Summary", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
