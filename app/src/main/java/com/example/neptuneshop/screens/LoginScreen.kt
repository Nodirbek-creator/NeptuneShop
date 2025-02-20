package com.example.neptuneshop.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.neptuneshop.R
import com.example.neptuneshop.model.Login
import com.example.neptuneshop.model.User
import com.example.neptuneshop.network.ApiService
import com.example.neptuneshop.screens.sign_in.SignInState
import com.example.neptuneshop.ui.theme.myBlue
import com.example.neptuneshop.ui.theme.myRed

import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*Todo: For now, the Ui of the login is simple. However, we will make more complex in the progress*/
//@Preview(
//    showSystemUi = true,
//    showBackground = true
//)

fun isValidUsername(username: String): Boolean {
    val regex = "^[a-zA-Z0-9_.]{5,20}\$".toRegex()
    return username.matches(regex)
}
fun isValidPassword(password: String): Boolean {
//    val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!])(?!.*\\s).{8,64}\$".toRegex() todo: too much complex regex, won't match the dummyjson's pass
    val regex = "^[a-zA-Z0-9_.]{5,20}\$".toRegex()
    return password.matches(regex)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    apiService: ApiService,
    onGoogleClick:() -> Unit,
    state: SignInState,
) {


    //todo: new unknown feature
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var isError by remember { mutableStateOf(false) }
    var isLoading by remember{ mutableStateOf(false) }


    var openBottomDialog by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
            text = "Login to Neptune",
            textAlign = TextAlign.Start,
            fontSize = 28.sp,
            fontWeight = FontWeight.W700)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { onGoogleClick() },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(min = 48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Unspecified,
            ),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.size(32.dp)){
                    Image(
                        painter = painterResource(R.drawable.google),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.Center).size(24.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Continue with Google",
                    fontSize = 20.sp,
                    color = Color.Black.copy(alpha = 0.8f),
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(4f)
                )
            }

        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(2f),
                thickness = 2.dp,
                color = Color.LightGray
            )
            Text(
                text = "OR",
                color = Color.LightGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            HorizontalDivider(
                modifier = Modifier.weight(2f),
                thickness = 2.dp,
                color = Color.LightGray
            )
        }
        Spacer(Modifier.height(16.dp))
        //todo: email login Button
        Button(
            onClick = {
                openBottomDialog = true
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = myBlue,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(48.dp)
        ) {
            Text(
                text = "Login with Email",
                fontWeight = FontWeight.W500,
                fontSize = 20.sp,
            )
        }

    }

    //input variables
    var email by remember { mutableStateOf("") }
    var emailValid by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    var passwordValid by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

    val passwordFocus = remember { FocusRequester() }
    //todo: bottom dialog
    if(openBottomDialog){
        ModalBottomSheet(
            onDismissRequest = {
                openBottomDialog = false
            },
            sheetState = sheetState,
            modifier = Modifier.fillMaxSize(),
            windowInsets = WindowInsets.systemBars
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isFocused) LocalConfiguration.current.screenHeightDp.dp else LocalConfiguration.current.screenHeightDp.dp * 0.4f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //todo: top bar (title + close btn)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Login with Email",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                    IconButton(
                        onClick = {
                            openBottomDialog = false
                            email = ""
                            password = ""
                            emailValid = true
                            passwordValid = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Black,
                        )
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = {email = it},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    label = {
                        Text(
                            text = "Email",
                            fontSize = 20.sp
                        )
                    },
                    supportingText = {
                        if(!emailValid){
                            Text(
                                text = "*Wrong format!",
                                fontSize = 18.sp
                            )
                        }
                    },
                    isError = !emailValid,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            if(isValidUsername(email)){
                                emailValid = true
                                passwordFocus.requestFocus()
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                            else{
                                emailValid = false
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .focusable()
                        .onFocusChanged { isFocused = it.isFocused },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLeadingIconColor = myBlue,
                        unfocusedLeadingIconColor = Color.LightGray,
                        errorLeadingIconColor = myRed,
                        focusedLabelColor = myBlue,
                        unfocusedLabelColor = Color.LightGray,
                        errorLabelColor = myRed,
                        focusedBorderColor = myBlue,
                        unfocusedBorderColor = Color.LightGray,
                        errorBorderColor = myRed,
                        cursorColor = myBlue,
                        errorSupportingTextColor = myRed
                    )
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {password = it},
                    leadingIcon = {
                        IconButton(
                            onClick = {passwordVisible = !passwordVisible}
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = "Password",
                            fontSize = 20.sp
                        )
                    },
                    supportingText = {
                        if(!passwordValid){
                            Text(
                                text = "*Wrong format!",
                                fontSize = 18.sp
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if(isValidPassword(password)){
                                focusManager.clearFocus()
                                passwordValid = true
                            }
                            else{
                                passwordValid = false
                            }
                        }
                    ),
                    visualTransformation = if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .focusable()
                        .onFocusChanged { isFocused = it.isFocused }
                        .focusRequester(passwordFocus),
                    isError = !passwordValid,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLeadingIconColor = myBlue,
                        unfocusedLeadingIconColor = Color.LightGray,
                        errorLeadingIconColor = myRed,
                        focusedLabelColor = myBlue,
                        unfocusedLabelColor = Color.LightGray,
                        errorLabelColor = myRed,
                        focusedBorderColor = myBlue,
                        unfocusedBorderColor = Color.LightGray,
                        errorBorderColor = myRed,
                        cursorColor = myBlue,
                        errorSupportingTextColor = myRed
                    )
                )
                //todo: email login Button
                Button(
                    onClick = {
                        if(passwordValid && emailValid){
                            isLoading = true
                            apiService.login(Login(email,password)).enqueue(object : Callback<User>{
                                override fun onResponse(call: Call<User>, response: Response<User>) {
                                    if(response.isSuccessful){
                                        isLoading = false
                                        navController.navigate(Routes.HomeScreen.route)
                                        val sharedPref = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
                                        with(sharedPref.edit()){
                                            putString("name", response.body()?.firstName)
                                            putString("email", response.body()?.email)
                                            putString("gender",response.body()?.gender)
                                            putString("profilePic", response.body()?.image)
                                            apply()
                                        }
                                        Toast.makeText(
                                            context,
                                            "Sign In successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                override fun onFailure(call: Call<User>, t: Throwable) {
                                    isLoading = false
                                    Log.d("LoginError:","${t.message}")
                                    Toast.makeText(context,"Network error", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                        else{
                            isError = true
                        }

                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = myBlue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Login",
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }

    LaunchedEffect(isFocused) {
        scope.launch {
            if (isFocused) {
                sheetState.expand() // Expand to full height
            } else {
                sheetState.partialExpand() // Collapse to 40% height
            }
        }
    }

    //todo: error dialog
    if(isError){
        AlertDialog(
            onDismissRequest = {isError = false},
            title = { Text("Invalid format") },
            icon = {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(36.dp)
                )
            },
            text = {
                Text(
                    text = """
                        |Email and password must:  
                        |- Be between 4-20 characters.  
                        |- Contain only letters, numbers, underscores (_), and dots (.)  
                    """.trimMargin()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        isError = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = myRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isError = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = myRed
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(width = 1.dp, color = myRed)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    //todo: loading dialog
    if(isLoading){
        BasicAlertDialog(
            onDismissRequest = {},
            modifier = Modifier.size(128.dp)
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(96.dp)
            )
        }
    }
}



