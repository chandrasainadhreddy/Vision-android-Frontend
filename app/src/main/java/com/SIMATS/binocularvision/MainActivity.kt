package com.SIMATS.binocularvision

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.SIMATS.binocularvision.ui.*
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme
import com.SIMATS.binocularvision.ui.viewmodels.AuthViewModel
import com.SIMATS.binocularvision.ui.viewmodels.TestViewModel
import com.SIMATS.binocularvision.ui.viewmodels.HomeViewModel
import android.util.Log // Import Log for debugging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BinocularvisionTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val testViewModel: TestViewModel = viewModel()
                val homeViewModel: HomeViewModel = viewModel()

                val startDestination = if (authViewModel.userProfile.value != null) "home" else "splash"
                val currentUserId = authViewModel.userProfile.value?.id?.toString() ?: "1"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("splash") {
                        SplashScreen(onNavigateNext = {
                            navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } }
                        })
                    }
                    composable("onboarding") { OnboardingScreen(onNavigateNext = { navController.navigate("intro") }) }
                    composable("intro") {
                        IntroScreen(
                            onNavigateNext = { navController.navigate("realtime_tracking") },
                            onSkip = { navController.navigate("login") }
                        )
                    }
                    composable("realtime_tracking") {
                        RealTimeTrackingScreen(
                            onNavigateNext = { navController.navigate("detailed_reports") },
                            onSkip = { navController.navigate("login") }
                        )
                    }
                    composable("detailed_reports") { DetailedReportsScreen(onNavigateNext = { navController.navigate("consent") }) }
                    composable("consent") {
                        ConsentScreen(
                            onNavigateNext = { navController.navigate("camera_access") },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("camera_access") {
                        CameraAccessScreen(
                            onAllowAccess = { navController.navigate("login") },
                            onNotNow = { navController.navigate("login") }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            onSignInClick = { navController.navigate("home") },
                            onCreateAccountClick = { navController.navigate("create_account") },
                            onGuestClick = { navController.navigate("home") },
                            onForgotPasswordClick = { navController.navigate("forgot_password") },
                            viewModel = authViewModel
                        )
                    }
                    composable("forgot_password") {
                        ForgotPasswordScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onSignInClick = { navController.popBackStack() },
                            onSendResetLink = { email ->
                                authViewModel.forgotPassword(email)
                                navController.navigate("check_email")
                            }
                        )
                    }
                    composable("check_email") {
                        CheckEmailScreen(
                            onBackToLogin = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("create_account") {
                        CreateAccountScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onLoginClick = { navController.popBackStack() },
                            onCreateAccountClick = {
                                navController.navigate("before_start") { popUpTo("create_account") { inclusive = true } }
                            },
                            viewModel = authViewModel
                        )
                    }
                    composable("before_start") {
                        BeforeWeStartScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onUnderstandClick = {
                                navController.navigate("login") { popUpTo("before_start") { inclusive = true } }
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            onNavigateToTest = { navController.navigate("new_test") },
                            onNavigateToHistory = { navController.navigate("history") },
                            onNavigateToSettings = { navController.navigate("settings") },
                            onProfileClick = { navController.navigate("profile") },
                            onNavigateToHome = { /* already here */ },
                            authViewModel = authViewModel,
                            homeViewModel = homeViewModel
                        )
                    }
                    composable("new_test") {
                        NewTestScreen(
                            onNavigateToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                            onNavigateToTest = { /* already here */ },
                            onNavigateToHistory = { navController.navigate("history") },
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateBack = { navController.popBackStack() },
                            onTestSelected = { testType ->
                                when (testType) {
                                    "fixation_test" -> navController.navigate("camera_test")
                                    "quick_screening" -> navController.navigate("camera_test2")
                                    "full_assessment" -> navController.navigate("camera_test3")
                                }
                            },
                            viewModel = testViewModel,
                            authViewModel = authViewModel
                        )
                    }

                    // --- PATH 1: Fixation Test ---
                    composable("camera_test") {
                        CameraTestScreen(onCancel = { navController.popBackStack() }, onEyesDetected = { navController.navigate("eyes_detected") })
                    }
                    composable("eyes_detected") {
                        EyesDetectedScreen(onAutoContinue = { navController.navigate("red_dot_tracking") }, onClose = { navController.popBackStack("new_test", false) })
                    }
                    composable("red_dot_tracking") {
                        RedDotTrackingScreen(onTestComplete = { navController.navigate("stare_at_center") }, onClose = { navController.popBackStack("new_test", false) }, viewModel = testViewModel)
                    }
                    composable("stare_at_center") {
                        StareAtCenterScreen(onTestComplete = { navController.navigate("saving_data") }, onClose = { navController.popBackStack("new_test", false) }, viewModel = testViewModel, userId = currentUserId)
                    }
                    composable("saving_data") { SavingDataScreen(onComplete = { navController.navigate("ai_analysis") }, viewModel = testViewModel) }
                    composable("ai_analysis") { AIAnalysisScreen(onComplete = { navController.navigate("assessment_result") }, viewModel = testViewModel) }
                    composable("assessment_result") {
                        AssessmentResultScreen(onBackToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }, onNavigateBack = { navController.popBackStack() }, viewModel = testViewModel)
                    }

                    // --- PATH 2: Quick Screening ---
                    composable("camera_test2") {
                        CameraTestScreen2(onCancel = { navController.popBackStack() }, onEyesDetected = { navController.navigate("eyes_detected2") })
                    }
                    composable("eyes_detected2") {
                        EyesDetectedScreen2(onAutoContinue = { navController.navigate("red_dot_tracking2") }, onClose = { navController.popBackStack("new_test", false) })
                    }
                    composable("red_dot_tracking2") {
                        RedDotTrackingScreen2(onTestComplete = { navController.navigate("stare_at_center2") }, onClose = { navController.popBackStack("new_test", false) }, viewModel = testViewModel)
                    }
                    composable("stare_at_center2") {
                        StareAtCenterScreen2(onTestComplete = { navController.navigate("saving_data2") }, onClose = { navController.popBackStack("new_test", false) }, viewModel = testViewModel, userId = currentUserId)
                    }
                    composable("saving_data2") { SavingDataScreen2(onComplete = { navController.navigate("ai_analysis2") }, viewModel = testViewModel) }
                    composable("ai_analysis2") { AIAnalysisScreen2(onComplete = { navController.navigate("assessment_result2") }, viewModel = testViewModel) }
                    composable("assessment_result2") {
                        AssessmentResultScreen2(onBackToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }, onNavigateBack = { navController.popBackStack() }, viewModel = testViewModel)
                    }

                    // --- PATH 3: Full Assessment ---
                    composable("camera_test3") {
                        CameraTestScreen3(onCancel = { navController.popBackStack() }, onEyesDetected = { navController.navigate("eyes_detected3") })
                    }
                    composable("eyes_detected3") {
                        EyesDetectedScreen3(onAutoContinue = { navController.navigate("red_dot_tracking3") }, onClose = { navController.popBackStack("new_test", false) })
                    }
                    composable("red_dot_tracking3") {
                        RedDotTrackingScreen3(onTestComplete = { navController.navigate("stare_at_center3") }, onClose = { navController.popBackStack("new_test", false) }, viewModel = testViewModel)
                    }
                    composable("stare_at_center3") {
                        StareAtCenterScreen3(onTestComplete = { navController.navigate("saving_data3") }, onClose = { navController.popBackStack("new_test", false) }, viewModel = testViewModel, userId = currentUserId)
                    }
                    composable("saving_data3") { SavingDataScreen3(onComplete = { navController.navigate("ai_analysis3") }, viewModel = testViewModel) }
                    composable("ai_analysis3") { AIAnalysisScreen3(onComplete = { navController.navigate("assessment_result3") }, viewModel = testViewModel) }
                    composable("assessment_result3") {
                        AssessmentResultScreen3(onBackToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }, onNavigateBack = { navController.popBackStack() }, viewModel = testViewModel)
                    }

                    // --- Common Screens ---
                    composable("history") {
                        HistoryScreen(
                            onNavigateToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                            onNavigateToTest = { navController.navigate("new_test") },
                            onNavigateToHistory = { /* already here */ },
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateBack = { navController.popBackStack() },
                            onFilterClick = { navController.navigate("filter_tests") },
                            viewModel = testViewModel,
                            authViewModel = authViewModel
                        )
                    }
                    composable("filter_tests") {
                        FilterTestsScreen(
                            onNavigateToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                            onNavigateToTest = { navController.navigate("new_test") },
                            onNavigateToHistory = { navController.navigate("history") { popUpTo("history") { inclusive = true } } },
                            onNavigateToSettings = { navController.navigate("settings") },
                            onClose = { navController.popBackStack() },
                            onApplyFilters = { dateRange, testType ->
                                testViewModel.applyFilters(dateRange, testType)
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            onNavigateToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                            onNavigateToTest = { navController.navigate("new_test") },
                            onNavigateToHistory = { navController.navigate("history") },
                            onNavigateToSettings = { /* already here */ },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToNotifications = { navController.navigate("notifications") },
                            onNavigateToHelp = { navController.navigate("help") },
                            onNavigateToPrivacy = { navController.navigate("privacy") },
                            onNavigateBack = { navController.popBackStack() },
                            onLogout = {
                                authViewModel.logout()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("profile") {
                        ProfileInformationScreen(onNavigateBack = { navController.popBackStack() }, onEditProfileClick = { navController.navigate("edit_profile") }, viewModel = authViewModel)
                    }
                    composable("edit_profile") { EditProfileScreen(onNavigateBack = { navController.popBackStack() }, viewModel = authViewModel) }
                    composable("notifications") { NotificationsScreen(onNavigateBack = { navController.popBackStack() }) }
                    composable("help") {
                        HelpScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToContact = { navController.navigate("contact_support") }
                        )
                    }
                    composable("contact_support") {
                        ContactSupportScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onMessageSent = { navController.navigate("message_sent") }
                        )
                    }
                    composable("message_sent") {
                        MessageSentScreen(
                            onBackToSettings = {
                                navController.navigate("settings") {
                                    popUpTo("settings") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("privacy") { PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() }) }
                }
            }
        }
    }
}
