package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BalanceCard
import com.example.ui.components.CategoryBreakdownCard
import com.example.ui.components.TransactionRow
import com.example.ui.theme.ExpenseRose
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.ScienceCyan
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.viewmodel.SpendWiseViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BioscienceScreen(
    viewModel: SpendWiseViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val allTransactions by viewModel.rawTransactions.collectAsStateWithLifecycle()
    val grantBreakdown by viewModel.bioscienceGrantBreakdown.collectAsStateWithLifecycle()

    val bioTransactions = allTransactions.filter { it.isBioscience }
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    // Calculate estimated burn rate (monthly burn)
    val monthlyBurn = if (stats.bioExpenses > 0) stats.bioExpenses else 1.0
    val monthsRunway = if (stats.bioBalance > 0) {
        String.format(Locale.US, "%.1f", (stats.bioBalance / monthlyBurn) * 1.5)
    } else "0.0"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("bioscience_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Science Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF042F2E)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0D9488)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Biotech,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Bioscience Lab Budgeting",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Grant accounting, consumables & sequencing burn",
                                    fontSize = 11.sp,
                                    color = Color(0xFF99F6E4)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("ESTIMATED RUNWAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5EEAD4))
                                Text("$monthsRunway mos", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                            Column {
                                Text("ACTIVE LAB TRANSACTIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5EEAD4))
                                Text("${bioTransactions.size} entries", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Science Net Balance Card
        item {
            BalanceCard(stats = stats, showBioscienceOnly = true)
        }

        // Science Quick Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.openAddModal(type = "expense", isBioscience = true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("log_lab_spend_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                ) {
                    Icon(imageVector = Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Lab Spend", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = { viewModel.openAddModal(type = "income", isBioscience = true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("add_grant_funding_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Grant Funding", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Project / Grant Breakdown
        item {
            CategoryBreakdownCard(
                breakdown = grantBreakdown,
                totalExpenses = stats.bioExpenses,
                title = "Lab Project & Grant Burn",
                subtitle = "By Grant / Category"
            )
        }

        // Compliance & Grant Auditing Guide Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(18.dp))
                        Text("Grant Accounting Compliance", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Direct costs (Reagents, RNA-Seq Kits, Flow cytometry antibodies) must tag grant project code.\n• APC publication fees qualify under dissemination budgets.\n• Institutional indirects (F&A) managed separately.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500, lineHeight = 18.sp)
                    )
                }
            }
        }

        // Recent Bioscience Lab Transactions
        item {
            Text(
                text = "Bioscience Ledger",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        if (bioTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No bioscience transactions logged yet.", color = Slate400, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(bioTransactions, key = { it.id }) { item ->
                TransactionRow(
                    transaction = item,
                    onDelete = { viewModel.deleteTransaction(it) },
                    onEdit = { viewModel.openEditModal(it) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
