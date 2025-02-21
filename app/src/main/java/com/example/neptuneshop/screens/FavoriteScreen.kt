package com.example.neptuneshop.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.example.neptuneshop.AppDatabase
import com.example.neptuneshop.R
import com.example.neptuneshop.model.FavProduct
import com.example.neptuneshop.model.Product
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(
    navController: NavHostController,
    db: AppDatabase
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(Routes.HomeScreen.route)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Favorites",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) { pad->
        Column(
            modifier = Modifier.fillMaxSize().padding(pad)
        ){
            val favorites = db.favoriteDao().allFavorites()
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(favorites){
                    FavCard (it){
                        navController.navigate("${Routes.ProductInfo.route}/${it.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun FavCard(
    product: FavProduct,
    onClick:()->Unit,
){
    Card (
        modifier = Modifier.padding(8.dp).size(230.dp,280.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = {onClick()}) {
        Column(modifier = Modifier.fillMaxSize().padding(start = 4.dp)) {
            Box {
                AsyncImage(
                    modifier = Modifier.size(300.dp, 140.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .data(product.thumbnail)
                        .size(300,200)
                        .scale(Scale.FILL)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.loading),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(5.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = product.title,
                modifier = Modifier.width(160.dp),
                fontSize = 15.sp
            )
            Spacer(Modifier.height(10.dp))
            Row (verticalAlignment = Alignment.CenterVertically){
                Text("$${product.price}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("$${String.format("%.2f",(product.price / (1 - product.discountPercentage/100)))}", style = TextStyle(textDecoration = TextDecoration.LineThrough))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${product.discountPercentage}% off", color = Color(0xFFB96F0E),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
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
}