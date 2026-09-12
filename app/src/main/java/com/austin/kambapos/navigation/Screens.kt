package com.austin.kambapos.navigation


sealed class Screens(val route: String) {

    object Cart: Screens("cart")

    object Orders: Screens("orders")

    object MainScreen: Screens("MainScreen")
    object Profile: Screens("profile")
    object Home: Screens("home")

    object SignUp: Screens("signup")
    object Login: Screens("login")
    object Categories: Screens("categories")

    object Products: Screens("products")

}
