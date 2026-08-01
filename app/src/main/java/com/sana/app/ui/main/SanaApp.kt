package com.sana.app.ui.main

import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sana.app.ui.admin.AdminDashboardScreen
import com.sana.app.ui.director.DirectorDashboardScreen
import com.sana.app.ui.login.LoginScreen
import com.sana.app.ui.login.LoginType
import com.sana.app.ui.login.RoleSelectionScreen
import com.sana.app.ui.student.StudentDashboardScreen
import com.sana.app.ui.teacher.TeacherDashboardScreen

/**
 * 🌿 SANA - Navegación Principal
 * 
 * Define las rutas de navegación de toda la aplicación:
 * 
 * Rutas:
 * - role_selection: Pantalla inicial con 3 botones
 * - login/{type}: Login según tipo (school, user, admin)
 * - student_dashboard/{userId}: Panel de alumno
 * - teacher_dashboard/{userId}: Panel de docente
 * - director_dashboard/{userId}: Panel de director
 * - admin_dashboard/{userId}: Panel de administrador
 */
object SanaRoutes {
    const val ROLE_SELECTION = "role_selection"
    const val LOGIN = "login/{type}"
    const val STUDENT_DASHBOARD = "student_dashboard/{userId}"
    const val TEACHER_DASHBOARD = "teacher_dashboard/{userId}"
    const val DIRECTOR_DASHBOARD = "director_dashboard/{userId}"
    const val ADMIN_DASHBOARD = "admin_dashboard/{userId}"
    
    fun login(type: String) = "login/$type"
    fun studentDashboard(userId: Long) = "student_dashboard/$userId"
    fun teacherDashboard(userId: Long) = "teacher_dashboard/$userId"
    fun directorDashboard(userId: Long) = "director_dashboard/$userId"
    fun adminDashboard(userId: Long) = "admin_dashboard/$userId"
}

@Composable
fun SanaApp(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SanaRoutes.ROLE_SELECTION,
        modifier = Modifier
    ) {
        // Pantalla inicial - Selección de rol
        composable(SanaRoutes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onNavigateToSchoolLogin = {
                    navController.navigate(SanaRoutes.login("school"))
                },
                onNavigateToUserServices = {
                    navController.navigate(SanaRoutes.login("user"))
                },
                onNavigateToAdminLogin = {
                    navController.navigate(SanaRoutes.login("admin"))
                }
            )
        }

        // Pantalla de login
        composable(
            route = SanaRoutes.LOGIN,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType }
            )
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
                    val route = when (loginType) {
                        LoginType.SCHOOL -> {
                            // Determinar ruta según rol del usuario
                            // Por ahora va a student, en producción se verifica el rol
                            SanaRoutes.studentDashboard(userId)
                        }
                        LoginType.USER -> SanaRoutes.studentDashboard(userId)
                        LoginType.ADMIN -> SanaRoutes.adminDashboard(userId)
                    }
                    navController.navigate(route) {
                        popUpTo(SanaRoutes.ROLE_SELECTION) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Dashboard de alumno
        composable(
            route = SanaRoutes.STUDENT_DASHBOARD,
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
            StudentDashboardScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Dashboard de docente
        composable(
            route = SanaRoutes.TEACHER_DASHBOARD,
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
            TeacherDashboardScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Dashboard de director
        composable(
            route = SanaRoutes.DIRECTOR_DASHBOARD,
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
            DirectorDashboardScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Dashboard de administrador
        composable(
            route = SanaRoutes.ADMIN_DASHBOARD,
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
            AdminDashboardScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}