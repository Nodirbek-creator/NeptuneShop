package com.example.neptuneshop.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import com.example.neptuneshop.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.example.neptuneshop.model.Product
import com.example.neptuneshop.model.Products
import com.example.neptuneshop.network.ApiService
import com.example.neptuneshop.ui.theme.myBlue
import com.example.neptuneshop.ui.theme.myRed
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.max
import kotlin.math.roundToInt

//
@Composable
fun SearchScreen(
    navController: NavHostController,
    apiService: ApiService){
    var allProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var searchTrigger by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(searchTrigger) {
        try {
            isLoading = true
            apiService.searchProducts(q = searchQuery).enqueue(object :Callback<Products>{
                override fun onResponse(
                    call: Call<Products>,
                    response: Response<Products>) {
                    if(response.isSuccessful){
                        allProducts = response.body()!!.products
                        isLoading = false
                        if(allProducts.size>1){
                            Toast.makeText(context,"${allProducts.size} items found", Toast.LENGTH_SHORT).show()
                        }
                        else if(allProducts.size == 1){
                            Toast.makeText(context,"1 item found", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(context,"No items found :(", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(
                    call: Call<Products>,
                    t: Throwable) {
                    Log.d("search-fetch.Fail:", "${t.message}")
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }
        catch (e:Exception){
            errorMessage = "Error: ${e.message}"
        }
    }
    Column (
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
        horizontalAlignment = Alignment.CenterHorizontally,){

        OutlinedTextField(value = searchQuery, onValueChange = {searchQuery = it},
            modifier = Modifier.fillMaxWidth().focusable(),
            leadingIcon = {
                if(searchQuery.isNotEmpty()){
                    IconButton(
                        onClick = {
                            searchQuery = ""
                            searchTrigger = !searchTrigger
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                else{
                    IconButton(
                        onClick = {
                            navController.navigate(Routes.HomeScreen.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        searchTrigger = !searchTrigger
                    }
                ){
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search",
                        modifier = Modifier.size(24.dp),
                    )
                }
            },
            placeholder = {
                Text("Search Anything...", fontSize = 16.sp)
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedPlaceholderColor = Color.LightGray,
                unfocusedLeadingIconColor = Color.LightGray,
                unfocusedTrailingIconColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color.Black,
                focusedTrailingIconColor = Color.Black,
                focusedLeadingIconColor = Color.Black,
                focusedTextColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                }
            )
        )
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.LightGray,
        )
        when{
            isLoading ->{
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Spacer(Modifier.height(32.dp))
                    CircularProgressIndicator(
                        color = myBlue,
                        modifier = Modifier.size(96.dp)
                    )
                }
            }
            errorMessage != null ->{
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = errorMessage!!,
                        color = myRed,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                Spacer(Modifier.height(24.dp))

                if(allProducts.isNotEmpty()){
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 12.dp,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                    ) {
                        items(allProducts){
                            OneCardMens(it){

                            }
                        }
                    }
                }
                else{
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(R.drawable.founf), contentDescription = "notfoundimage",
                            Modifier.size(400.dp))
                        Spacer(Modifier.height(35.dp))
                        Text("No Result found!", fontWeight = FontWeight.Bold,
                            fontSize = 28.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun OneCardMens(
    product: Product,
    onClick:() -> Unit,
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
        .padding(6.dp)
        .clickable { onClick() },) {
        Box{
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .data(product.images[0])
                    .size(300,200)
                    .scale(Scale.FILL)
                    .crossfade(true)
                    .build(),
                contentDescription = "photo",
                modifier = Modifier.size(160.dp, 240.dp).align(Alignment.Center),
            )
            Row(
                modifier = Modifier.width(150.dp).align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if(product.rating >= 4.5 && product.discountPercentage>=10){
                    Text(
                        "Trending",
                        modifier = Modifier
                            .background(Color(0xFF4DC930), shape = RoundedCornerShape(4.dp))
                            .padding(5.dp)
                    )
                }
                Spacer(modifier = Modifier.width(40.dp))
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "favourite",
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(25.dp))
                        .padding(3.dp)
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = product.title,
            maxLines = 1,
            modifier = Modifier.width(160.dp),
            fontSize = 15.sp
        )
        Spacer(Modifier.height(10.dp))
        Row (verticalAlignment = Alignment.CenterVertically){
            Text("$${String.format("%.1f",product.price)}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("$${String.format("%.1f",(product.price / (1 - product.discountPercentage/100)))}", style = TextStyle(textDecoration = TextDecoration.LineThrough))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "${product.discountPercentage.roundToInt()}% off", color = Color(0xFFB96F0E),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("${product.rating}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            Text("(${product.stock})")
        }
    }
}