package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionItem
import com.example.ui.theme.ExpenseRose
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionRow(
    transaction: TransactionItem,
    onDelete: (TransactionItem) -> Unit,
    onEdit: (TransactionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val isIncome = transaction.type.equals("income", ignoreCase = true)
    val categoryIcon = CategoryIconHelper.getIcon(transaction.category)
    val categoryColor = CategoryIconHelper.getColor(transaction.category)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transaction_row_${transaction.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEdit(transaction) }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Category Icon Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = transaction.category,
                        tint = categoryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = transaction.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate500,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                        )
                        Text(
                            text = transaction.date,
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                        )
                    }

                    if (transaction.projectCode.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (transaction.isBioscience) Color(0xFF0F766E).copy(alpha = 0.12f) else Slate500.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = transaction.projectCode,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (transaction.isBioscience) Color(0xFF0F766E) else Slate500,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Amount & Delete Button
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isIncome) "+" else "-"}${currencyFormat.format(transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isIncome) IncomeGreen else ExpenseRose
                    ),
                    modifier = Modifier.testTag("amount_text_${transaction.id}")
                )

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = { onDelete(transaction) },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("delete_button_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Transaction",
                        tint = Slate400,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
