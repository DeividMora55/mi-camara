package com.deividmora.micamara

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var imagePreview: ImageView
    private lateinit var textDate: TextView
    private lateinit var textLocation: TextView
    private lateinit var textPath: TextView

    private var currentPhotoUri: Uri? = null
    private var currentPhotoFile: File? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoUri?.let { uri ->
                    imagePreview.setImageURI(uri)

                    val now = SimpleDateFormat(
                        "dd/MM/yyyy HH:mm:ss",
                        Locale.getDefault()
                    ).format(Date())

                    textDate.text = "Fecha y hora: $now"
                    textPath.text = "Archivo: ${currentPhotoFile?.absolutePath ?: "—"}"
                    updateLocation()
                }
            } else {
                Toast.makeText(
                    this,
                    "No se tomó la fotografía.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            updateLocation()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imagePreview = findViewById(R.id.imagePreview)
        textDate = findViewById(R.id.textDate)
        textLocation = findViewById(R.id.textLocation)
        textPath = findViewById(R.id.textPath)

        findViewById<Button>(R.id.buttonPhoto).setOnClickListener {
            launchCamera()
        }

        findViewById<Button>(R.id.buttonLocation).setOnClickListener {
            requestLocationPermissionsIfNeeded()
        }

        requestLocationPermissionsIfNeeded()
    }

    private fun launchCamera() {
        val picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (picturesDir == null) {
            Toast.makeText(
                this,
                "No se pudo acceder al almacenamiento.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
        }

        val timestamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        val photoFile = File(
            picturesDir,
            "IMG_$timestamp.jpg"
        )

        val photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )

        currentPhotoFile = photoFile
        currentPhotoUri = photoUri

        takePictureLauncher.launch(photoUri)
    }

    private fun requestLocationPermissionsIfNeeded() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            updateLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun updateLocation() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            textLocation.text = "Ubicación: permiso no concedido"
            return
        }

        val manager = getSystemService(LOCATION_SERVICE) as LocationManager

        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER
        )

        var bestLocation: Location? = null

        for (provider in providers) {
            try {
                val location = manager.getLastKnownLocation(provider)

                if (
                    location != null &&
                    (bestLocation == null || location.accuracy < bestLocation.accuracy)
                ) {
                    bestLocation = location
                }
            } catch (_: SecurityException) {
            } catch (_: IllegalArgumentException) {
            }
        }

        if (bestLocation != null) {
            textLocation.text = "Ubicación: %.6f, %.6f".format(
                Locale.US,
                bestLocation.latitude,
                bestLocation.longitude
            )
        } else {
            val gpsEnabled = runCatching {
                manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            }.getOrDefault(false)

            textLocation.text = if (gpsEnabled) {
                "Ubicación: esperando una ubicación disponible..."
            } else {
                "Ubicación: activa el GPS del dispositivo"
            }
        }
    }
}
