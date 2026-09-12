package com.austin.kambapos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.austin.kambapos.core.lib.SunmiPrinter
import com.austin.kambapos.navigation.Screens
import com.austin.kambapos.screens.MainScreen
import com.austin.kambapos.screens.auth.LoginScreen
import com.austin.kambapos.screens.home.CartScreen
import com.austin.kambapos.screens.home.ProductsScreen
import com.austin.kambapos.screens.profile.OrdersScreen
import com.austin.kambapos.screens.profile.ProfileScreen
import com.austin.kambapos.ui.theme.ShopEasyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var printer: SunmiPrinter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        printer = SunmiPrinter(this)
        printer.connectPrinter()
        installSplashScreen()
        enableEdgeToEdge()

        // 🚀 ADICIONE ESTA LINHA AQUI:
        // Ela remove o azul que o sistema "herda" do splash ou do tema base da janela
        window.setBackgroundDrawableResource(android.R.color.white)
        val window = window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false

        setContent {
            ShopEasyTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screens.MainScreen.route
                ) {
                    composable(Screens.MainScreen.route) {
                        MainScreen(navController = navController)
                    }

                    composable(Screens.Login.route) {
                        LoginScreen(navController = navController)
                    }

                    composable(Screens.Home.route) {
                        ProductsScreen(
                            navController = navController,
                            onProfileClick = {
                                navController.navigate(Screens.Profile.route)
                            },
                            onCartClick = {
                                navController.navigate(Screens.Cart.route)
                            }
                        )
                    }

                    composable(Screens.Profile.route) {
                        ProfileScreen(
                            navController = navController,
                            onProfileClick = {
                                navController.navigate(Screens.Profile.route)
                            },
                            onCartClick = {
                                navController.navigate(Screens.Cart.route)
                            },
                            isConnected = printer.isConnected.value
                        )
                    }

                    composable(Screens.Cart.route) {
                        CartScreen(
                            onCartClick = {},
                            navController = navController,
                            onProfileClick = {
                                navController.navigate(Screens.Profile.route)
                            }
                        )
                    }

                    composable(Screens.Orders.route) {
                        OrdersScreen(
                            navController = navController,
                            onProfileClick = {
                                navController.navigate(Screens.Profile.route)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        printer.disconnect()
    }
}
