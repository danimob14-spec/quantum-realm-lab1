package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        QuantumApp()
      }
    }
  }
}

@Composable
fun QuantumApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "dashboard",
                    onClick = { navController.navigate("dashboard") { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Home, contentDescription = "داشبورد") },
                    label = { Text("داشبورد") }
                )
                NavigationBarItem(
                    selected = currentRoute == "simulations",
                    onClick = { navController.navigate("simulations") { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Science, contentDescription = "شبیه‌سازی‌ها") },
                    label = { Text("شبیه‌سازی‌ها") }
                )
                NavigationBarItem(
                    selected = currentRoute == "wiki",
                    onClick = { navController.navigate("wiki") { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "ویکی کوانتوم") },
                    label = { Text("ویکی") }
                )
                NavigationBarItem(
                    selected = currentRoute == "about",
                    onClick = { navController.navigate("about") { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Person, contentDescription = "درباره") },
                    label = { Text("تنظیمات") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") { DashboardScreen(navController) }
            composable("simulations") { SimulationsScreen() }
            composable("wiki") { WikiScreen() }
            composable("about") { AboutScreen() }
        }
    }
}

@Composable
fun DashboardScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "آزمایشگاه کوانتوم",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "به دنیای حیرت‌انگیز ذرات خوش آمدید",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        DashboardCard(title = "شبیه‌سازی‌ها", subtitle = "گربه شرودینگر، درهم‌تنیدگی", icon = Icons.Default.Science) {
            navController.navigate("simulations")
        }
        Spacer(modifier = Modifier.height(16.dp))
        DashboardCard(title = "ویکی کوانتوم", subtitle = "دانشنامه تصویری و مقالات", icon = Icons.Default.MenuBook) {
            navController.navigate("wiki")
        }
        Spacer(modifier = Modifier.height(16.dp))
        DashboardCard(title = "پازل‌ها", subtitle = "چالش‌های ذهنی فیزیک", icon = Icons.Default.Extension) {
            // Placeholder
        }
    }
}

@Composable
fun DashboardCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

enum class CatState { UNKNOWN, ALIVE, DEAD }

@Composable
fun SimulationsScreen() {
    var catState by remember { mutableStateOf(CatState.UNKNOWN) }
    var isObserving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "آزمایش گربه شرودینگر",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(targetState = catState, label = "cat_animation") { state ->
                when (state) {
                    CatState.UNKNOWN -> Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "جایگاه ناشناخته",
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    CatState.ALIVE -> Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = "گربه زنده",
                        modifier = Modifier.size(100.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    CatState.DEAD -> Icon(
                        imageVector = Icons.Default.SentimentVeryDissatisfied,
                        contentDescription = "گربه مرده",
                        modifier = Modifier.size(100.dp),
                        tint = Color(0xFFF44336)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val statusText = when {
            isObserving -> "در حال اندازه‌گیری..."
            catState == CatState.UNKNOWN -> "ذره در حالت برهم‌نهی (Superposition) قرار دارد"
            catState == CatState.ALIVE -> "نتیجه: زنده (زنده بودن گربه مشاهده شد)"
            catState == CatState.DEAD -> "نتیجه: مرده (مرگ گربه مشاهده شد)"
            else -> ""
        }

        Text(
            text = statusText,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (catState == CatState.UNKNOWN) {
                    isObserving = true
                    scope.launch {
                        delay(800) // Simulate processing time
                        isObserving = false
                        catState = if (Random.nextBoolean()) CatState.ALIVE else CatState.DEAD
                    }
                } else {
                    catState = CatState.UNKNOWN
                }
            },
            modifier = Modifier.fillMaxWidth(0.7f).height(56.dp).testTag("observe_particle_btn"),
            enabled = !isObserving
        ) {
            Text(
                text = if (catState == CatState.UNKNOWN) "مشاهده ذره" else "تنظیم مجدد سیستم",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun WikiScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "ویکی کوانتوم",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "برهم‌نهی کوانتومی (Superposition)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "در مکانیک کوانتومی، یک ذره می‌تواند همزمان در چندین حالت مختلف قرار داشته باشد، تا زمانی که با یک ابزار اندازه‌گیری، مشاهده یا سنجیده شود. پس از اندازه‌گیری، تابع موج فرو می‌پاشد و ذره تنها یک حالت قطعی به خود می‌گیرد.", style = MaterialTheme.typography.bodyMedium)
            }
        }
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "درهم‌تنیدگی کوانتومی (Entanglement)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "پدیده‌ای فیزیکی است که در آن دو یا چند ذره به گونه‌ای با یکدیگر مرتبط می‌شوند که بدون در نظر گرفتن فاصله میان آن‌ها، وضعیت کوانتومی یکی فوراً بر دیگری اثر می‌گذارد.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "درباره توسعه‌دهنده",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().testTag("about_developer_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "تصویر توسعه‌دهنده",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "مهدی احسانی‌نسب",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "دانشجوی دکتری فیزیک کوانتوم",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "دانشگاه صنعتی امیرکبیر\n(دانشگاه پلی‌تکنیک تهران)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "این اپلیکیشن با هدف آموزش مفاهیم پیچیده‌ی مکانیک کوانتومی به زبانی ساده و آکادمیک برای دانشجویان و علاقه‌مندان حوزه فیزیک در ایران توسعه یافته است.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
