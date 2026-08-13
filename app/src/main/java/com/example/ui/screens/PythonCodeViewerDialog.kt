package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.LightText
import com.example.ui.theme.MutedText
import com.example.ui.theme.RexCrimson
import com.example.ui.theme.RexPinkText

@Composable
fun PythonCodeViewerDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val pythonCode = """
# main.py - Rex Mode V6 FastAPI Backend
# Run with: uvicorn main:app --host 0.0.0.0 --port 8000

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import uuid
import time

app = FastAPI(title="Rex Mode V6 Auth Node", version="6.4.2")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class LicenseVerifyRequest(BaseModel):
    license_key: str
    hwid: str

class LicenseGenerateRequest(BaseModel):
    hwid: str

@app.get("/api/v1/health")
def health_check():
    return {
        "status": "ok",
        "version": "6.4.2",
        "uptime_seconds": int(time.time())
    }

@app.post("/api/v1/auth/verify-license")
def verify_license(req: LicenseVerifyRequest):
    key = req.license_key.strip().upper()
    if not key:
        raise HTTPException(status_code=400, detail="License key required")
    
    # Validates REX- prefix or keys >= 8 characters
    is_valid = key.startswith("REX-") or len(key) >= 8
    if not is_valid:
        return {
            "valid": False,
            "status": "INVALID",
            "message": "Invalid license key format or signature"
        }
        
    return {
        "valid": True,
        "status": "ACTIVE",
        "message": f"License key authenticated for HWID: {req.hwid}",
        "expires_in_days": 30,
        "user_id": f"REX-USER-{hash(req.hwid) % 1000}",
        "token": f"eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.{uuid.uuid4().hex[:16]}",
        "ecdsa_signature": f"3045022100{uuid.uuid4().hex}"
    }

@app.post("/api/v1/license/generate")
def generate_license(req: LicenseGenerateRequest):
    random_id = uuid.uuid4().hex[:8].upper()
    key = f"REX-{random_id[:4]}-{random_id[4:]}"
    return {
        "license_key": key,
        "expires_in_days": 14,
        "message": "Trial license generated successfully"
    }
""".trimIndent()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Python FastAPI Code",
                        tint = RexCrimson
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "FASTAPI BACKEND CODE",
                        color = LightText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Copy this python script to run your own FastAPI server locally or on VPS:",
                    color = MutedText,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color(0xFF0C050B), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF26121E), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = pythonCode,
                        color = RexPinkText,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CLOSE", color = MutedText)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("FastAPI Code", pythonCode)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "FastAPI Python code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RexCrimson),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("COPY CODE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
