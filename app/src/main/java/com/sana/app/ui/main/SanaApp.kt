package com.sana.app.ui.main

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sana.app.core.theme.ThemeManager
import com.sana.app.ui.admin.AdminDashboardScreen
import com.sana.app.ui.director.DirectorDashboardScreen
import com.sana.app.ui.login.LoginScreen
import com.sana.app.ui.login.LoginType
import com.sana.app.ui.login.RoleSelectionScreen
import com.sana.app.ui.student.StudentDashboardScreen
import com.sana.app.ui.teacher.TeacherDashboardScreen
import javax.inject.Inject

object SanaRoutes {
    const val ROLE_SELECTION = "role_selection"
    const val LOGIN = "login/{type}"
    const val STUDENT_DASHBOARD = "student_dashboard/{userId}"
    const val TEACHER_DASHBOARD = "teacher_dashboard/{userId}"
    const val DIRECTOR_DASHBOARD = "director_dashboard/{userId}"
    const val ADMIN_DASHBOARD = "admin_dashboard/{userId}"
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
                onLoginSuccess = { userId, _ ->
                    navController.navigate(SanaRoutes.STUDENT_DASHBOARD.replace("{userId}", userId.toString())) {
                        popUpTo(SanaRoutes.ROLE_SELECTION) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() },
                themeManager = themeManager
            )
        }

        composable(
            route = SanaRoutes.STUDENT_DASHBOARD,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
            StudentDashboardScreen(userId = userId, onNavigateBack = { navController.popBackStack() }, themeManager = themeManager)
        }

        composable(
            route = SanaRoutes.TEACHER_DASHBOARD,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
            TeacherDashboardScreen(userId = userId, onNavigateBack = { navController.popBackStack() }, themeManager = themeManager)
        }

        composable(
            route = SanaRoutes.DIRECTOR_DASHBOARD,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
            DirectorDashboardScreen(userId = userId, onNavigateBack = { navController.popBackStack() }, themeManager = themeManager)
        }

        composable(
            route = SanaRoutes.ADMIN_DASHBOARD,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
            AdminDashboardScreen(userId = userId, onNavigateBack = { navController.popBackStack() }, themeManager = themeManager)
        }
    }
}
