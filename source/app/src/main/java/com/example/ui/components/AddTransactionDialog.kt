package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TransactionCategories
import com.example.data.model.TransactionItem
import com.example.ui.theme.ExpenseRose
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.ScienceCyan
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionDialog(
    initialType: String = "expense",
    initialIsBio: Boolean = false,
    editingTransaction: TransactionItem? = null,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        type: String,
        amount: Double,
        category: String,
        description: String,
        date: String,
        projectCode: String,
        isBioscience: Boolean
    ) -> Unit
) {
    var type by remember { mutableStateOf(editingTransaction?.type ?: initialType) }
    var isBioscience by remember { mutableStateOf(editingTransaction?.isBioscience ?: initialIsBio) }

    val defaultCategories = when {
        isBioscience && type == "income" -> TransactionCategories.bioscienceIncome
        isBioscience -> TransactionCategories.bioscienceExpenses
        type == "income" -> TransactionCategories.personalIncome
        else -> TransactionCategories.personalExpenses
    }

    var amountText by remember {
        mutableStateOf(editingTransaction?.amount?.toString()?.removeSuffix(".0") ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(editingTransaction?.category ?: defaultCategories.first())
    }
    var description by remember { mutableStateOf(editingTransaction?.description ?: "") }
    var dateText by remember {
        mutableStateOf(
            editingTransaction?.date ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        )
    }
    var projectCode by remember { mutableStateOf(editingTransaction?.projectCode ?: "") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = when {
        isBioscience && type == "income" -> TransactionCategories.bioscienceIncome
        isBioscience -> TransactionCategories.bioscienceExpenses
        type == "income" -> TransactionCategories.personalIncome
        else -> TransactionCategories.personalExpenses
    }

    // Ensure selected category is valid for current type/bio mode
    if (selectedCategory !in categories) {
        selectedCategory = categories.first()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(28.dp))
                .testTag("add_transaction_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (editingTransaction != null) "Edit Transaction" else "New Transaction",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Slate400
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Type Toggle (Expense vs Income)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (type == "expense") Color.White else Color.Transparent)
                            .clickable {
                                type = "expense"
                            }
                            .padding(vertical = 10.dp)
                            .testTag("type_toggle_expense"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Expense",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (type == "expense") ExpenseRose else Slate500
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (type == "income") Color.White else Color.Transparent)
                            .clickable {
                                type = "income"
                            }
                            .padding(vertical = 10.dp)
                            .testTag("type_toggle_income"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Income",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (type == "income") IncomeGreen else Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mode Selector: Personal vs Bioscience Lab
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isBioscience) IndigoPrimary.copy(alpha = 0.12f) else Color.Transparent)
                            .clickable { isBioscience = false }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (!isBioscience) IndigoPrimary else Slate400,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Standard",
                            fontSize = 12.sp,
                            fontWeight = if (!isBioscience) FontWeight.Bold else FontWeight.Medium,
                            color = if (!isBioscience) IndigoPrimary else Slate500
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isBioscience) ScienceCyan.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { isBioscience = true }
                            .padding(vertical = 8.dp)
                            .testTag("bioscience_mode_toggle"),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = if (isBioscience) Color(0xFF0F766E) else Slate400,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bioscience Lab",
                            fontSize = 12.sp,
                            fontWeight = if (isBioscience) FontWeight.Bold else FontWeight.Medium,
                            color = if (isBioscience) Color(0xFF0F766E) else Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Amount Field
                Text(
                    text = "AMOUNT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_amount"),
                    placeholder = { Text("0.00", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = {
                        Text(
                            text = "$",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (type == "income") IncomeGreen else IndigoPrimary,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (type == "income") IncomeGreen else IndigoPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category Dropdown
                Text(
                    text = "CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        leadingIcon = {
                            Icon(
                                imageVector = CategoryIconHelper.getIcon(selectedCategory),
                                contentDescription = null,
                                tint = CategoryIconHelper.getColor(selectedCategory),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("select_category"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndigoPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = CategoryIconHelper.getIcon(cat),
                                            contentDescription = null,
                                            tint = CategoryIconHelper.getColor(cat),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(cat, fontSize = 14.sp)
                                    }
                                },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description Field
                Text(
                    text = "DESCRIPTION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_description"),
                    placeholder = {
                        Text(
                            text = if (isBioscience) "e.g. CRISPR Kits, NovaSeq Flowcell" else "e.g. Weekly Groceries, Electric Bill",
                            color = Slate400,
                            fontSize = 14.sp
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                // Quick suggestions chips
                Spacer(modifier = Modifier.height(8.dp))
                val suggestions = if (isBioscience) {
                    listOf("NovaSeq Reagents", "CRISPR Guides", "Cell Culture Media", "Grant Disbursal", "APC Fee")
                } else {
                    listOf("Weekly Groceries", "Apartment Rent", "Metro Pass", "Freelance Work", "Dinner")
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    suggestions.forEach { suggestion ->
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.clickable { description = suggestion }
                        ) {
                            Text(
                                text = "+ $suggestion",
                                fontSize = 11.sp,
                                color = Slate500,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date Field
                Text(
                    text = "DATE (YYYY-MM-DD)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_date"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoPrimary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )

                // Grant / Project Code (Visible in Bioscience mode or editable)
                if (isBioscience) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "GRANT / PROJECT CODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F766E),
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = projectCode,
                        onValueChange = { projectCode = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_project_code"),
                        placeholder = { Text("e.g. NIH R01-AI14892, NSF-BIO-2026-7A", color = Slate400, fontSize = 13.sp) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0F766E),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = ExpenseRose,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Submit Button
                Button(
                    onClick = {
                        val parsedAmount = amountText.toDoubleOrNull()
                        if (parsedAmount == null || parsedAmount <= 0) {
                            errorMessage = "Please enter a valid amount greater than 0"
                            return@Button
                        }
                        if (description.isBlank()) {
                            errorMessage = "Please enter a description"
                            return@Button
                        }
                        onSave(
                            editingTransaction?.id ?: 0L,
                            type,
                            parsedAmount,
                            selectedCategory,
                            description.trim(),
                            dateText.trim(),
                            projectCode.trim(),
                            isBioscience
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_transaction_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == "income") IncomeGreen else IndigoPrimary
                    )
                ) {
                    Text(
                        text = if (editingTransaction != null) "Update Transaction" else "Save Transaction",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
