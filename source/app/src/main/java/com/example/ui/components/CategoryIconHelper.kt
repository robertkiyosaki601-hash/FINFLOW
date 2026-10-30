package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.*

object CategoryIconHelper {
    fun getIcon(category: String): ImageVector {
        return when (category.lowercase()) {
            "food & dining", "food" -> Icons.Default.LocalDining
            "housing" -> Icons.Default.Home
            "transportation", "commute" -> Icons.Default.DirectionsBus
            "utilities" -> Icons.Default.Receipt
            "entertainment" -> Icons.Default.Movie
            "healthcare", "health" -> Icons.Default.MedicalServices
            "shopping" -> Icons.Default.ShoppingBag
            "personal care", "personal" -> Icons.Default.Person
            "education" -> Icons.Default.School
            "salary" -> Icons.Default.Paid
            "freelance" -> Icons.Default.Work
            "investment" -> Icons.Default.TrendingUp
            "gift" -> Icons.Default.CardGiftcard
            "rental" -> Icons.Default.Home
            "reagents & chemicals", "reagents" -> Icons.Default.Science
            "consumables & labware", "labware" -> Icons.Default.Biotech
            "cell culture & media" -> Icons.Default.Biotech
            "sequencing & genomics", "genomics" -> Icons.Default.Psychology
            "antibodies & assays" -> Icons.Default.Science
            "lab equipment & repair", "equipment" -> Icons.Default.Handyman
            "conference & travel" -> Icons.Default.Flight
            "publication & apc fees" -> Icons.Default.MenuBook
            "nih / nsf grant", "grant" -> Icons.Default.AccountBalance
            "fellowship stipend" -> Icons.Default.School
            "institutional award" -> Icons.Default.LocalAtm
            else -> Icons.Default.Category
        }
    }

    fun getColor(category: String): Color {
        return when (category.lowercase()) {
            "food & dining", "food" -> CatFood
            "housing" -> CatHousing
            "transportation", "commute" -> CatTransport
            "utilities" -> CatUtilities
            "entertainment" -> CatEntertainment
            "healthcare", "health" -> CatHealthcare
            "shopping" -> CatShopping
            "personal care", "personal" -> CatPersonal
            "education" -> CatEducation
            "salary", "freelance", "investment" -> IncomeGreen
            "reagents & chemicals", "reagents" -> CatReagents
            "cell culture & media" -> ScienceCyan
            "sequencing & genomics", "genomics" -> CatSequencing
            "antibodies & assays" -> Color(0xFF0284C7)
            "lab equipment & repair", "equipment" -> CatEquipment
            "publication & apc fees" -> CatPublication
            "nih / nsf grant", "fellowship stipend" -> CatGrant
            else -> IndigoPrimary
        }
    }
}
