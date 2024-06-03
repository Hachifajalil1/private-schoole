package com.example.myapplication


import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter


/**
 * @author Coding Meet
 * Created 17-01-2024 at 02:07 pm
 */

@Composable
fun NavBarHeader() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val userName = sharedPreferences.getString("userName", "No Name")
    val userFamilyName = sharedPreferences.getString("userFamilyName", "No Name")
    val userEmail = sharedPreferences.getString("userEmail", "No Email")
    val userImageUrl = sharedPreferences.getString("userImageUrl", null)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        userImageUrl?.let {
            Image(
                painter = rememberImagePainter(it), // Use coil for image loading
                contentDescription = "User Image",
                modifier = Modifier
                    .size(150.dp)

                    .clip(shape = CircleShape), // تحويل الصورة إلى دائرة
                contentScale = ContentScale.Crop,
            )
        }
        Text(
            text = userFamilyName+" "+userName ?: "No Name",
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = userEmail ?: "No Email",
            modifier = Modifier.padding(top = 5.dp)
        )

    }
}

@Composable
fun NavBarBody(
    items: List<NavigationItem>,
    currentRoute: String?,
    onClick: (NavigationItem) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(horizontal = 12.dp, vertical = 8.dp))
    ) {
        itemsIndexed(items) { index, navigationItem ->
            NavigationDrawerItem(
                colors = NavigationDrawerItemDefaults.colors(),
                label = {
                    Text(text = navigationItem.title)
                },
                selected = currentRoute == navigationItem.route,
                onClick = {
                    onClick(navigationItem)
                },
                icon = {
                    Icon(
                        imageVector = if (currentRoute == navigationItem.route) {
                            navigationItem.selectedIcon
                        } else {
                            navigationItem.unSelectedIcon
                        },
                        contentDescription = navigationItem.title
                    )
                },
                badge = {
                    navigationItem.badgeCount?.let {
                        Text(text = it.toString())
                    }
                }
            )
        }
    }
}
