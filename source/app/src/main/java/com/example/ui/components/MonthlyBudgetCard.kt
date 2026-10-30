package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpenseRose
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.viewmodel.MonthlyBudgetInfo
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthlyBudgetCard(
    budgetInfo: MonthlyBudgetInfo,
    onEditBudget: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    // State variable holding the active budget amount for real-time visualization and setting
    var budgetAmount by remember(budgetInfo.budgetCap) { mutableStateOf(budgetInfo.budgetCap) }
    var isSettingGoal by remember { mutableStateOf(false) }
    var goalInputText by remember(budgetAmount) { mutableStateOf(budgetAmount.toInt().toString()) }
    var inputError by remember { mutableStateOf<String?>(null) }

    val presetGoals = listOf(3000, 5000, 7500, 10000, 15000, 20000)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    // Calculate real-time progress against the current budget amount state
    val progressRatio = if (budgetAmount > 0) {
        (budgetInfo.spentThisMonth / budgetAmount).toFloat()
    } else 0f

    val clampedProgress = progressRatio.coerceIn(0f, 1f)
    val spentPercentage = progressRatio * 100f
    val isExceeded = budgetInfo.spentThisMonth > budgetAmount
    val remainingBudget = budgetAmount - budgetInfo.spentThisMonth

    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = tween(durationMillis = 600),
        label = "budget_progress_anim"
    )

    val progressColor by animateColorAsState(
        targetValue = when {
            isExceeded -> ExpenseRose
            progressRatio >= 0.80f -> Color(0xFFF59E0B) // Amber warning
            else -> IncomeGreen
        },
        label = "budget_color_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_budget_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Title, status badge, and Toggle Set Goal button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IndigoPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Monthly Budget Goal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${budgetInfo.monthName} Target",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate400,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Status Badge
                    Surface(
                        shape = CircleShape,
                        color = when {
                            isExceeded -> ExpenseRose.copy(alpha = 0.14f)
                            progressRatio >= 0.80f -> Color(0xFFF59E0B).copy(alpha = 0.14f)
                            else -> IncomeGreen.copy(alpha = 0.14f)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isExceeded) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = progressColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when {
                                    isExceeded -> "Exceeded"
                                    progressRatio >= 0.80f -> "Near Limit"
                                    else -> "On Track"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = progressColor
                            )
                        }
                    }

                    // Button to toggle the inline Set Budget Goal section
                    Surface(
                        shape = CircleShape,
                        color = if (isSettingGoal) IndigoPrimary else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .clickable {
                                isSettingGoal = !isSettingGoal
                                inputError = null
                            }
                            .testTag("set_budget_goal_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isSettingGoal) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = "Set Budget Goal",
                                tint = if (isSettingGoal) Color.White else IndigoPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSettingGoal) "Close" else "Set Goal",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSettingGoal) Color.White else IndigoPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Numbers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "SPENT THIS MONTH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate500,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = currencyFormat.format(budgetInfo.spentThisMonth),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.testTag("budget_spent_text")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "of ${currencyFormat.format(budgetAmount)} goal",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Slate400,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.testTag("budget_cap_text")
                        )
                    }
                }

                // Percentage Indicator
                Text(
                    text = String.format(Locale.US, "%.1f%%", spentPercentage),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    ),
                    modifier = Modifier.testTag("budget_percent_text")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFE2E8F0))
                    .testTag("budget_progress_bar")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(progressColor)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Remaining Amount or Over Budget Alert
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExceeded) {
                        "Exceeded goal by ${currencyFormat.format(abs(remainingBudget))}"
                    } else {
                        "${currencyFormat.format(remainingBudget)} remaining before target"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isExceeded) ExpenseRose else Slate500
                    ),
                    modifier = Modifier.testTag("budget_remaining_text")
                )

                if (!isSettingGoal) {
                    Text(
                        text = "Adjust Goal",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = IndigoPrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { isSettingGoal = true }
                    )
                }
            }

            // Inline Set Monthly Budget Goal Section (Collapsible & Interactive)
            AnimatedVisibility(
                visible = isSettingGoal,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(16.dp)
                        .testTag("inline_set_budget_section")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Set Monthly Budget Target",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Instant Update",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = IndigoPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Text Field for entering custom budget target
                    OutlinedTextField(
                        value = goalInputText,
                        onValueChange = {
                            goalInputText = it
                            inputError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inline_budget_input"),
                        placeholder = { Text("7500") },
                        leadingIcon = {
                            Text(
                                text = "$",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        trailingIcon = {
                            Button(
                                onClick = {
                                    val parsed = goalInputText.toDoubleOrNull()
                                    if (parsed == null || parsed <= 0) {
                                        inputError = "Please enter a valid amount"
                                        return@Button
                                    }
                                    budgetAmount = parsed
                                    onEditBudget(parsed)
                                    isSettingGoal = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .testTag("save_budget_goal_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndigoPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    if (inputError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = inputError ?: "",
                            color = ExpenseRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Preset Chips for Easy Selection
                    Text(
                        text = "Quick Presets:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate500
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetGoals.forEach { preset ->
                            val isSelected = goalInputText == preset.toString()
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) IndigoPrimary else Color.White,
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .clickable {
                                        goalInputText = preset.toString()
                                        budgetAmount = preset.toDouble()
                                        onEditBudget(preset.toDouble())
                                        inputError = null
                                    }
                                    .testTag("preset_goal_$preset")
                            ) {
                                Text(
                                    text = "$$preset",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Slate500,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Exceeded Warning Alert Banner
            if (isExceeded) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ExpenseRose.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = ExpenseRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "You have exceeded your monthly budget goal. Consider reviewing discretionary expenses or adjusting your target.",
                            fontSize = 11.sp,
                            color = ExpenseRose,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
