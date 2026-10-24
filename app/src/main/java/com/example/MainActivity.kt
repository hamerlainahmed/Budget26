package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.BudgetDatabase
import com.example.data.BudgetRepository
import com.example.ui.BudgetViewModel
import com.example.ui.ROUTE_SCHOOL_BUDGET_DASHBOARD
import com.example.ui.SchoolBudgetDashboardScreen
import com.example.ui.theme.SchoolBudget2026Theme

class MainActivity : ComponentActivity() {

    private val database by lazy {
        BudgetDatabase.getInstance(applicationContext, lifecycleScope)
    }

    private val repository by lazy {
        BudgetRepository(database.budgetDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(true) }

            SchoolBudget2026Theme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val budgetViewModel: BudgetViewModel = viewModel(
                    factory = BudgetViewModel.Factory(repository)
                )

                NavHost(
                    navController = navController,
                    startDestination = ROUTE_SCHOOL_BUDGET_DASHBOARD
                ) {
                    composable(route = ROUTE_SCHOOL_BUDGET_DASHBOARD) {
                        SchoolBudgetDashboardScreen(
                            viewModel = budgetViewModel,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme }
                        )
                    }
                }
            }
        }
    }
}
