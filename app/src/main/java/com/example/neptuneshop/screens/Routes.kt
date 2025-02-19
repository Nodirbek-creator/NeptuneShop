package com.example.neptuneshop.screens

enum class Screens {
    Login,
    Splash,
    Home,
    Categories,
    Orders,
    Profile
}
sealed class Routes(val route: String){
    data object LoginScreen :Routes(Screens.Login.name)
    data object SplashScreen :Routes(Screens.Splash.name)
    data object HomeScreen :Routes(Screens.Home.name)
    data object CategoriesScreen :Routes(Screens.Categories.name)
    data object OrdersScreen :Routes(Screens.Orders.name)
    data object ProfileScreen :Routes(Screens.Profile.name)
}