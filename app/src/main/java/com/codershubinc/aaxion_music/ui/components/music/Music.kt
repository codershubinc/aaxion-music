package com.codershubinc.aaxion_music.ui.components.music

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.launch
import com.codershubinc.aaxion_music.utils.AaxionServiceInfo
import com.codershubinc.aaxion_music.utils.TokenManager
import com.codershubinc.aaxion_music.utils.ServerSelector
import com.codershubinc.aaxion_music.utils.music.FetchMusic
import com.codershubinc.aaxion_music.utils.music.MusicTrack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import kotlin.contracts.Effect

val AmoledBlack = Color(0xFF000000)
val Zinc800 = Color(0xFF18181B)
val Zinc400 = Color(0xFFA1A1AA)
val CyanAccent = Color(0xFF404E50)

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MusicMainScreen(
    serverInfo: AaxionServiceInfo?,
    onLogout: () -> Unit = {},
    onRetryDiscovery: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val serverSelector = remember { ServerSelector(context) }
    val token = remember { tokenManager.getToken() }
    var musicList by remember { mutableStateOf<List<MusicTrack>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    
    var isPlayerExpanded by remember { mutableStateOf(false) }

    val activeUrl = serverSelector.getActiveServerUrl(serverInfo)
    
    LaunchedEffect(serverInfo) {
        if (serverInfo == null && serverSelector.hasBackupUrlConfigured()) {
            Toast.makeText(context, "Local server lost. Switching to remote...", Toast.LENGTH_SHORT)
                .show()
        }
    }
            
    LaunchedEffect(serverInfo, activeUrl, refreshTrigger) {
        if (activeUrl.isNotBlank()) {
            isLoading = true
            val result = FetchMusic.fetchAllMusic(activeUrl, tokenManager.getToken())
            result.onSuccess {
                musicList = it
            }
            isLoading = false
        }
    }

    if (activeUrl.isBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AmoledBlack),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp)
                )
                Text(
                    "No Server Connected",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    "Check your local network or Settings for backup URL",
                    color = Zinc400,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onRetryDiscovery,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = Color.Black
                    ),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text("Retry Discovery")
                }
                Button(
                    onClick =  onOpenSettings,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = Color.Black
                    ),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text("Settings")
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onLogout) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            }
        }
        return
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        val isTablet = maxWidth >= 600.dp
        
        Box(modifier = Modifier.fillMaxSize()) {
            if (isTablet) { 
                Row(modifier = Modifier.fillMaxSize()) {
                    SidebarWrapper(
                        modifier = Modifier
                            .width(280.dp)
                            .fillMaxHeight(),
                        isMobile = false,
                        onCloseDrawer = {},
                        onLogout = onLogout,
                        onOpenSettings = onOpenSettings,
                        onModeChange = { refreshTrigger++ }
                    )
                    
                        MainContentWrapper(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onOpenDrawer = {},
                            showHamburger = false,
                            musicList = musicList,
                            isLoading = isLoading,
                            serverUrl = activeUrl,
                            serverInfo = serverInfo,
                            token = token,
                            serverSelector = serverSelector,
                            onRefresh = { refreshTrigger++ }
                        )
                    }
                } else { 
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        scrimColor = Color.Black.copy(alpha = 0.6f),
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = Color.Transparent,
                                drawerContentColor = Color.Transparent,
                                modifier = Modifier.width(300.dp)
                            ) {
                                SidebarWrapper(
                                    modifier = Modifier.fillMaxSize(),
                                    isMobile = true,
                                    onCloseDrawer = {
                                        scope.launch { drawerState.close() }
                                    },
                                    onLogout = onLogout,
                                    onOpenSettings = onOpenSettings,
                                    onModeChange = { refreshTrigger++ }
                                )
                            }
                        }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AmoledBlack)
                            ) {
                                MainMusicContent(
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    showHamburger = true,
                                    musicList = musicList,
                                    isLoading = isLoading,
                                    serverUrl = activeUrl,
                                    serverInfo = serverInfo,
                                    token = token,
                                    serverSelector = serverSelector,
                                    onRefresh = { refreshTrigger++ }
                                )
                            }
                        }
                    }
                }

            // --- Floating Island Player ---
            // The component handles its own visibility and animation internally
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(10f)
            ) {
                FloatingMusicIsland(
                    serverInfo = serverInfo,
                    token = token,
                    onExpand = { isPlayerExpanded = true }
                )
            }
        }
    }

    if (isPlayerExpanded) {
        FullScreenPlayer(
            serverInfo = serverInfo,
            token = token,
            onClose = { isPlayerExpanded = false }
        )
    }
}

@Composable
fun MainContentWrapper(
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit,
    showHamburger: Boolean = false,
    musicList: List<MusicTrack>,
    isLoading: Boolean,
    serverUrl: String,
    serverInfo: AaxionServiceInfo?,
    token: String?,
    serverSelector: ServerSelector,
    onRefresh: () -> Unit
) {
    Box(
        modifier = modifier.padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(AmoledBlack)
        ) {
            MainMusicContent(onOpenDrawer, showHamburger, musicList, isLoading, serverUrl, serverInfo, token, serverSelector, onRefresh)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMusicContent(
    onOpenDrawer: () -> Unit,
    showHamburger: Boolean,
    musicList: List<MusicTrack>,
    isLoading: Boolean,
    serverUrl: String,
    serverInfo: AaxionServiceInfo?,
    token: String?,
    serverSelector: ServerSelector,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (showHamburger) {
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Sidebar",
                    tint = Color.White
                )
            }
        } else {
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (musicList.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Music Found",
                        color = Zinc400,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Button(
                        modifier = Modifier.padding(top = 16.dp),
                        onClick = onRefresh,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanAccent,
                            contentColor = Color.Black
                        ),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Text("Refresh")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp, 
                        top = 16.dp, 
                        end = 16.dp, 
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column(modifier = Modifier.padding(bottom = 16.dp)) {
                            Text(
                                text = "All Songs",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            val source = serverSelector.getSourceLabel(serverInfo)
                            Text(
                                text = "Source: $source • $serverUrl",
                                color = CyanAccent.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    items(musicList) { track ->
                        val musicController = LocalMusicController.current
                        
                        MusicTrackItem(track, serverUrl, token, onClick = {
                            musicController.playTrack(track, musicList, serverUrl, token)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun MusicTrackItem(track: MusicTrack, serverUrl: String, token: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail using the /files/thumbnail endpoint
        val imageUrl = if (serverUrl.isNotBlank() && token != null) {
            val encodedPath = URLEncoder.encode(track.imagePath, StandardCharsets.UTF_8.toString())
            val encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.toString())
            "$serverUrl/files/thumbnail?path=$encodedPath&tkn=$encodedToken"
        } else null

        val imageRequest = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .diskCacheKey(track.imagePath)
            .memoryCacheKey(track.imagePath)
            .crossfade(true)
            .build()

        SubcomposeAsyncImage(
            model = imageRequest,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AmoledBlack),
            contentScale = ContentScale.Crop,
            loading = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = CyanAccent
                    )
                }
            },
            error = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "No image",
                        tint = Zinc400,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artist,
                color = Zinc400,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = { /* More options */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = Zinc400
            )
        }
    }
}
