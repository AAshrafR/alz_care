package com.example.alzcare.ui.patient.ml

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.alzcare.R
import com.example.alzcare.ml.Model
import com.example.alzcare.ui.patient.home.PatientHomeActivity
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException

class DLModelActivity : AppCompatActivity() {
    private lateinit var selectBtn: Button
    private lateinit var predBtn: Button
    private lateinit var imageView: ImageView
    private lateinit var result: TextView
    private lateinit var bitmap: Bitmap
    private lateinit var backButton: ImageView
    private val imageSize = 224

    private val imageProcessor = ImageProcessor.Builder()
        .add(NormalizeOp(0.0f, 255.0f))
        .add(ResizeOp(imageSize, imageSize, ResizeOp.ResizeMethod.BILINEAR)) // Resize the image to a smaller size
        .build()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dl_model)

        selectBtn = findViewById(R.id.btn_select)
        predBtn = findViewById(R.id.btn_predict)
        imageView = findViewById(R.id.imageView)
        result = findViewById(R.id.txtResult)
        backButton = findViewById(R.id.back_button)

        backButton.setOnClickListener {
            val intent = Intent(this, PatientHomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val labels = application.assets
            .open("labels.txt").bufferedReader().readLines()

        selectBtn.setOnClickListener {
            val intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, 100)
        }

        predBtn.setOnClickListener {
            if (!::bitmap.isInitialized) {
                // Show a toast or a message indicating that no image has been selected
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)

            tensorImage = imageProcessor.process(tensorImage)

            val model = Model.newInstance(this)

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            // Find the index of the class with the highest probability
            val maxIndex = outputFeature0.indices.maxByOrNull { outputFeature0[it] } ?: -1

            // Check if maxIndex is valid
            if (maxIndex != -1) {
                // Get the class label corresponding to the maxIndex
                val maxClass = labels[maxIndex]

                // Get the highest probability
                val maxProbability = outputFeature0[maxIndex]

                // Calculate the percentage
                val percentage = (maxProbability * 100).toInt()

                // Display the result
                result.text = "Most likely class: $maxClass\nWith  $percentage% Probability"
            } else {
                // Handle the case where no class probabilities are available
                result.text = "No class probabilities available"
            }

            model.close()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data
                uri?.let {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        imageView.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Image selection cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
