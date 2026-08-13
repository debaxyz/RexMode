package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RexViewModel
import com.example.ui.components.BackgroundGrid
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.LightText
import com.example.ui.theme.MutedText
import com.example.ui.theme.RexCrimson
import com.example.ui.theme.RexCrimsonGlow
import com.example.ui.theme.RexDarkCrimson
import com.example.ui.theme.RexPinkText
import com.example.ui.theme.SystemReadyGreen
import com.example.ui.theme.SystemReadyGreenBg

@Composable
fun LoginScreen(viewModel: RexViewModel) {
    val context = LocalContext.current

    val keyInput by viewModel.licenseKeyInput.collectAsState()
    val isKeyVisible by viewModel.isKeyVisible.collectAsState()
    val validationMessage by viewModel.validationMessage.collectAsState()
    val isValidationSuccess by viewModel.isValidationSuccess.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val hwid = viewModel.hwid
    val isFastApiConnected by viewModel.isFastApiConnected.collectAsState()

    var showLangMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Grid Mesh Background
        BackgroundGrid()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Language Selector & FastAPI Config button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // FastAPI Server connection status indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1B0E17))
                        .border(1.dp, if (isFastApiConnected) SystemReadyGreen.copy(alpha = 0.5f) else DarkCardBorder, RoundedCornerShape(20.dp))
                        .clickable { viewModel.setServerConfigDialogVisible(true) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = "FastAPI Node",
                            tint = if (isFastApiConnected) SystemReadyGreen else RexPinkText,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFastApiConnected) "FastAPI Live" else "FastAPI Mock",
                            color = LightText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Language Dropdown Pill
                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1E0E18))
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
                            .clickable { showLangMenu = true }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedLanguage,
                                color = LightText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Language",
                                tint = LightText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showLangMenu,
                        onDismissRequest = { showLangMenu = false },
                        modifier = Modifier.background(DarkSurfaceCard)
                    ) {
                        listOf("EN", "ES", "RU", "CN").forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang, color = LightText) },
                                onClick = {
                                    viewModel.setLanguage(lang)
                                    showLangMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Logo Frame (Futuristic R box)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = RexCrimsonGlow)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF140A10))
                    .border(2.dp, Brush.linearGradient(listOf(RexCrimson, RexDarkCrimson)), RoundedCornerShape(24.dp))
            ) {
                // Outer inner border accent
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .border(1.dp, RexPinkText.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                )

                // R Symbol
                Text(
                    text = "R",
                    color = LightText,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )

                // Top right tiny green dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                        .clip(CircleShape)
                        .background(SystemReadyGreen)
                )

                // Bottom underline inside icon box
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(3.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 14.dp)
                        .background(RexCrimson)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Title "Rex Mode" + "V6" pill badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Rex ",
                    color = LightText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "Mode",
                    color = RexCrimson,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(RexDarkCrimson)
                        .border(1.dp, RexCrimson, CircleShape)
                ) {
                    Text(
                        text = "V6",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "DARK DOODLE  //  SECURE ACCESS",
                color = RexPinkText.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Main Access Card Panel
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF160B12).copy(alpha = 0.95f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = RexDarkCrimson)
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                RexCrimson.copy(alpha = 0.4f),
                                DarkCardBorder
                            )
                        ),
                        RoundedCornerShape(28.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    // Curved Wavy Accent Header Art
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    ) {
                        val path = Path().apply {
                            moveTo(0f, size.height * 0.5f)
                            quadraticTo(
                                size.width * 0.25f, 0f,
                                size.width * 0.5f, size.height * 0.5f
                            )
                            quadraticTo(
                                size.width * 0.75f, size.height,
                                size.width, size.height * 0.5f
                            )
                        }
                        drawPath(
                            path = path,
                            color = RexCrimson.copy(alpha = 0.6f),
                            style = Stroke(width = 2f)
                        )
                        drawCircle(color = RexPinkText, radius = 3f, center = Offset(size.width * 0.6f, size.height * 0.2f))
                        drawCircle(color = RexPinkText, radius = 2f, center = Offset(size.width * 0.64f, size.height * 0.2f))
                        drawCircle(color = RexPinkText, radius = 2f, center = Offset(size.width * 0.68f, size.height * 0.2f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Green Pill Badge "• SYSTEM READY"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SystemReadyGreenBg)
                            .border(1.dp, SystemReadyGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SystemReadyGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SYSTEM READY",
                                color = SystemReadyGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Card Title & Subtitle
                    Text(
                        text = "ACCESS REX MODE",
                        color = LightText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Enter your license key to open the control deck.",
                        color = MutedText,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Security Indicator Badges Box: ECDSA | HWID | TLS
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF10070E))
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                            .padding(vertical = 10.dp, horizontal = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(RexPinkText)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ECDSA",
                                    color = LightText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(text = "|", color = DarkCardBorder)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(RexPinkText)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "HWID",
                                    color = LightText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(text = "|", color = DarkCardBorder)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SystemReadyGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TLS",
                                    color = LightText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Input Header Row: "LICENSE KEY" & "ENCRYPTED ACCESS"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LICENSE KEY",
                            color = LightText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "ENCRYPTED ACCESS",
                            color = RexPinkText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // License Key Input Text Field
                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { viewModel.onKeyInputChange(it) },
                        placeholder = {
                            Text(
                                text = "REX-XXXX-XXXX",
                                color = MutedText.copy(alpha = 0.4f),
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "Key Icon",
                                tint = MutedText
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.toggleKeyVisibility() }) {
                                Icon(
                                    imageVector = if (isKeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Visibility",
                                    tint = MutedText
                                )
                            }
                        },
                        visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = LightText,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RexCrimson,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = Color(0xFF0F070D),
                            unfocusedContainerColor = Color(0xFF0F070D)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Validation status feedback line
                    Text(
                        text = validationMessage,
                        color = if (isValidationSuccess) SystemReadyGreen else RexPinkText,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Main Glowing Button: "OPEN CONTROL DECK"
                    Button(
                        onClick = { viewModel.verifyLicense() },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Unspecified
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = RexCrimsonGlow)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        RexCrimson,
                                        RexDarkCrimson
                                    )
                                ),
                                RoundedCornerShape(18.dp)
                            )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "OPEN CONTROL DECK",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Divider: "—— OR ——"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(DarkCardBorder)
                        )
                        Text(
                            text = " OR ",
                            color = MutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(DarkCardBorder)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Secondary Glass Button: "GET LICENSE KEY"
                    Surface(
                        onClick = { viewModel.getLicenseKey() },
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF1C0E17),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(18.dp))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "GET LICENSE KEY",
                                color = LightText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // HWID Footer Label: "ID: F500DD0337A9F4DC"
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("HWID", hwid)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "HWID copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Text(
                            text = "ID: $hwid",
                            color = MutedText.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer bar text
            Text(
                text = "—  REX MODE V6  /  PROTECTED SESSION  —",
                color = MutedText.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
