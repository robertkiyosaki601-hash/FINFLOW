package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "expense" or "income"
    val category: String,
    val amount: Double,
    val description: String,
    val date: String, // "YYYY-MM-DD"
    val projectCode: String = "",
    val isBioscience: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

object TransactionCategories {
    val personalExpenses = listOf(
        "Food & Dining",
        "Housing",
        "Transportation",
        "Utilities",
        "Entertainment",
        "Healthcare",
        "Shopping",
        "Personal Care",
        "Education",
        "Other"
    )

    val personalIncome = listOf(
        "Salary",
        "Freelance",
        "Investment",
        "Gift",
        "Rental",
        "Other"
    )

    val bioscienceExpenses = listOf(
        "Reagents & Chemicals",
        "Consumables & Labware",
        "Cell Culture & Media",
        "Sequencing & Genomics",
        "Antibodies & Assays",
        "Lab Equipment & Repair",
        "Conference & Travel",
        "Publication & APC Fees",
        "Biospecimens & Storage",
        "Safety & Waste Disposal"
    )

    val bioscienceIncome = listOf(
        "NIH / NSF Grant",
        "Institutional Award",
        "Fellowship Stipend",
        "Industry Sponsorship",
        "Lab Service Revenue",
        "Discretionary Research Fund"
    )

    val commonGrantProjects = listOf(
        "NIH R01-AI14892",
        "NSF-BIO-2026-7A",
        "Cancer Inst Pilot #4",
        "Core Facility Fund",
        "Department Startup",
        "General Personal"
    )
}
