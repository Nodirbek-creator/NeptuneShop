package com.example.neptuneshop.screens

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.neptuneshop.R
import com.example.neptuneshop.ui.theme.myBlue

data class BottomNavItem(
    val title:String,
    val icon: Int,
    val route:String
)


@Composable
fun BottomNavBar(navController: NavHostController){

    //bottom icons' list. here are 3 icons: Favorite, Recent, and Contacts.
    val bottomIcons = listOf(
        BottomNavItem(
            title = "Home",
            icon = R.drawable.home,
            route = Routes.HomeScreen.route
        ),
        BottomNavItem(
            title = "Categories",
            icon = R.drawable.category,
            route = Routes.CategoriesScreen.route
        ),
        BottomNavItem(
            title = "Records",
            icon = R.drawable.orders,
            route = Routes.OrdersScreen.route
        ),
        BottomNavItem(
            title = "Profile",
            icon = R.drawable.account,
            route = Routes.ProfileScreen.route
        ),
    )
    /*Line of code that gets the current route of the app*/
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomIcons.forEachIndexed { _, bottomNavItem ->
            NavigationBarItem(
                selected =  currentRoute == bottomNavItem.route,
                onClick = {
                    navController.navigate(bottomNavItem.route)
                },
                icon = {
                    Image(
                        painter = painterResource(bottomNavItem.icon),
                        contentDescription = bottomNavItem.title,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = myBlue,
                    unselectedIconColor = Color.LightGray,
                    selectedTextColor = myBlue,
                    unselectedTextColor = Color.LightGray
                ),
                label = {
                    Text(bottomNavItem.title)
                }
            )
        }
    }
}