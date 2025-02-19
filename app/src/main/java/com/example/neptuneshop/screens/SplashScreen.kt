package com.example.neptuneshop.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.neptuneshop.R
import com.example.neptuneshop.screens.sign_in.GoogleAuthUiClient
import com.example.neptuneshop.ui.theme.myBlue
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    googleAuthUiClient: GoogleAuthUiClient
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = myBlue
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NEPTUNE.",
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                letterSpacing = 3.sp
            )
        }
        LaunchedEffect(Unit) {
            delay(500)
            if(googleAuthUiClient.getSignedInUser() != null){
                navController.navigate(Routes.HomeScreen.route)
            }
            else{
                navController.navigate(Routes.LoginScreen.route)
            }


        }
    }
}
