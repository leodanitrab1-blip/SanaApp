package com.sana.app.ui.main

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sana.app.core.repository.DataRepository
import com.sana.app.core.theme.ThemeManager
import com.sana.app.core.utils.Constants
import com.sana.app.ui.admin.AdminDashboardScreen
import com.sana.app.ui.director.DirectorDashboardScreen
import com.sana.app.ui.login.LoginScreen
import com.sana.app.ui.login.LoginType
import com.sana.app.ui.login.RoleSelectionScreen
import com.sana.app.ui.student.StudentDashboardScreen
import com.sana.app.ui.teacher.TeacherDashboardScreen

object SanaRoutes {
    const val ROLE_SELECTION = "role_selection"
    const val LOGIN = "login/{type}"
    const val ADMIN = "admin"
    const val TEACHER = "teacher"
    const val DIRECTOR = "director"
    const val STUDENT = "student"
}

@Composable
fun SanaApp(
    themeManager: ThemeManager,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = SanaRoutes.ROLE_SELECTION) {
        
        composable(SanaRoutes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onNavigateToSchoolLogin = { navController.navigate(SanaRoutes.LOGIN.replace("{type}", "school")) },
                onNavigateToUserServices = { navController.navigate(SanaRoutes.LOGIN.replace("{type}", "user")) },
                onNavigateToAdminLogin = { navController.navigate(SanaRoutes.LOGIN.replace("{type}", "admin")) },
                themeManager = themeManager
            )
        }

        composable(
            route = SanaRoutes.LOGIN,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val loginType = when (backStackEntry.arguments?.getString("type")) {
                "school" -> LoginType.SCHOOL
                "user" -> LoginType.USER
                "admin" -> LoginType.ADMIN
                else -> LoginType.SCHOOL
            }
            
            LoginScreen(
                loginType = loginType,
                onLoginSuccess = { userId, role ->
                    val route = when (role) {
                        Constants.ROLE_ADMIN -> SanaRoutes.ADMIN
                        Constants.ROLE_TEACHER -> SanaRoutes.TEACHER
                        Constants.ROLE_DIRECTOR -> SanaRoutes.DIRECTOR
                        else -> SanaRoutes.STUDENT
                    }
                    navController.navigate(route) {
                        popUpTo(SanaRoutes.ROLE_SELECTION) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() },
                themeManager = themeManager
            )
        }

        composable(SanaRoutes.ADMIN) {
            AdminDashboardScreen(userId = 0L, onNavigateBack = { navController.navigate(SanaRoutes.ROLE_SELECTION) { popUpTo(0) { inclusive = true } } }, themeManager = themeManager)
        }

        composable(SanaRoutes.TEACHER) {
            TeacherDashboardScreen(userId = 1L, onNavigateBack = { navController.navigate(SanaRoutes.ROLE_SELECTION) { popUpTo(0) { inclusive = true } } }, themeManager = themeManager)
        }

        composable(SanaRoutes.DIRECTOR) {
            DirectorDashboardScreen(userId = 2L, onNavigateBack = { navController.navigate(SanaRoutes.ROLE_SELECTION) { popUpTo(0) { inclusive = true } } }, themeManager = themeManager)
        }

        composable(SanaRoutes.STUDENT) {
            StudentDashboardScreen(userId = 3L, onNavigateBack = { navController.navigate(SanaRoutes.ROLE_SELECTION) { popUpTo(0) { inclusive = true } } }, themeManager = themeManager)
        }
    }
}
