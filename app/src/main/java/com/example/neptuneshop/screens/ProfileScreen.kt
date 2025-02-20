package com.example.neptuneshop.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.example.neptuneshop.R
import com.example.neptuneshop.ui.theme.myBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    onSignOut:() -> Unit,) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val sharedPreferences = remember { context.getSharedPreferences("user_profile", Context.MODE_PRIVATE) }

    var name by remember { mutableStateOf(sharedPreferences.getString("name", "David Guetta") ?: "David Guetta") }
    var email by remember { mutableStateOf(sharedPreferences.getString("email", "davidguetta@gmail.com") ?: "davidguetta@gmail.com") }
    var mobileNumber by remember { mutableStateOf(sharedPreferences.getString("mobile", "+1-202 555 0143") ?: "+1-202 555 0143") }
    var location by remember { mutableStateOf(sharedPreferences.getString("location", "") ?: "") }
    var gender by remember { mutableStateOf(sharedPreferences.getString("gender", "Male"))}
    var profilePic by remember { mutableStateOf(sharedPreferences.getString("profilePic", "")) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var isChanged by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) } // State to control Snackbar visibility

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri:Uri? ->
        if (uri != null) {
            profileImageUri = uri
            profilePic = uri.toString()
        }
    }

    var originalName by remember { mutableStateOf(name) }
    var originalEmail by remember { mutableStateOf(email) }
    var originalMobileNumber by remember { mutableStateOf(mobileNumber) }
    var originalLocation by remember { mutableStateOf(location) }
    var originalGender by remember { mutableStateOf(gender) }
    var originalPic by remember { mutableStateOf(profilePic) }

    fun checkChanges() {
        isChanged = name != originalName || email != originalEmail || mobileNumber != originalMobileNumber || location != originalLocation || gender != originalGender || profilePic != originalPic
    }

    LaunchedEffect(name, email, mobileNumber, location, gender, profilePic) {
        checkChanges()
    }

    val items = listOf(
        MenuOption("Shop by Categories", R.drawable.category),
        MenuOption("My Orders", R.drawable.clock),
        MenuOption("Favorites", R.drawable.heart),
        MenuOption("Profile", R.drawable.account),
        MenuOption("Saved Cards", R.drawable.card),
        MenuOption("Contributors", R.drawable.conversation),
        MenuOption("Sign Out", R.drawable.exit)

    )
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color.White) {
                ProfileSection(navController)
                items.forEach { item ->
                    NavigationDrawerItem(
                        label = {
                            Text(item.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        },
                        icon = {
                            Image(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        selected = false,
                        onClick = {
                            when (item.title) {
                                "Profile" -> {
                                    scope.launch {
                                        navController.navigate(Routes.ProfileScreen.route)
                                    }
                                } // This will now work
                                "Sign Out" -> {
                                    scope.launch {
                                        val sharedPreferences = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
                                        with(sharedPreferences.edit()){
                                            putString("name", "")
                                            putString("email", "")
                                            putString("mobile", "")
                                            putString("location", "")
                                            putString("gender", "")
                                            putString("profilePic", "")
                                            apply()
                                        }
                                        onSignOut()
                                        navController.navigate(Routes.LoginScreen.route)
                                    }
                                }
                                else ->{
                                    scope.launch { drawerState.close() }
                                }
                            }
                        }
                    )
                    if(item.title == "Saved Cards"){
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (isChanged) {
                            TextButton(onClick = {
                                with(sharedPreferences.edit()) {
                                    putString("name", name)
                                    putString("email", email)
                                    putString("mobile", mobileNumber)
                                    putString("location", location)
                                    putString("gender", gender)
                                    putString("profilePic", profilePic)
                                    apply()
                                }

                                originalName = name
                                originalEmail = email
                                originalMobileNumber = mobileNumber
                                originalLocation = location
                                originalGender = gender
                                isChanged = false
                                showSnackbar = true // Show the Snackbar
                            }) {
                                Text("Save Changes", color = myBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                )
            },
            snackbarHost = {
                if (showSnackbar) {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            TextButton(onClick = { showSnackbar = false }) {
                                Text("Dismiss", color = Color.White)
                            }
                        }
                    ) {
                        Text("Changes Saved Successfully!", color = Color.White)
                    }
                }
            },
            bottomBar = {
                BottomNavBar(navController)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(10.dp))

                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(100.dp)
                ) {
                    Image(
                        painter = if (profilePic != "") {
                            rememberAsyncImagePainter(profilePic)
                        } else {
                            painterResource(id = R.drawable.account)
                        },
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                    IconButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color.Gray, CircleShape)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Profile", tint = Color.Black)
                    }
                }

                Spacer(Modifier.height(20.dp))

                ProfileTextField(value = name, onValueChange = { name = it }, label = "Username")
                ProfileTextField(value = email, onValueChange = { email = it }, label = "Email")
                ProfileTextField(value = mobileNumber, onValueChange = { mobileNumber = it }, label = "Mobile Number", keyboardType = KeyboardType.Phone)
                ProfileTextField(value = location, onValueChange = { location = it }, label = "Location")

                Spacer(Modifier.height(10.dp))

                Text("Gender", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Male", "Female").forEach { option ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = gender.equals(option, ignoreCase = true), onClick = { gender = option })
                            Text(option, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ProfileTextField(value: String, onValueChange: (String) -> Unit, label: String, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
@Composable
fun ProfileSection(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("user_profile", Context.MODE_PRIVATE) }
    val name = sharedPreferences.getString("name", "David") ?: "David"
    val email = sharedPreferences.getString("email", "david@gmail.com") ?: "david@gmail.com"
    val profilePic = sharedPreferences.getString("profilePic","")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = if (profilePic != "") {
                rememberAsyncImagePainter(profilePic)
            } else {
                painterResource(id = R.drawable.account)
            },
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(50.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(name, fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Text(email, fontSize = 15.sp, color = Color.Gray)
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { navController.navigate("profile") }) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit Profile", tint = Color.Gray)
        }
    }
}