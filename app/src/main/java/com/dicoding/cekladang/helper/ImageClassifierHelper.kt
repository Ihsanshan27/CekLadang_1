package com.dicoding.cekladang.helper

import android.app.Activity
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.Collections
import kotlin.math.min

class ImageClassifierHelper(
    private val context: Activity,
    private val classifierListener: ClassifierListener,
) {
    private var imageSizeX = 256
    private var imageSizeY = 256

    private var labels: List<String>? = null
    private var tfLite: Interpreter? = null

    private var inputImageBuffer: TensorImage? = null
    private var outputProbabilityBuffer: TensorBuffer? = null
    private var probabilityProcessor: TensorProcessor? = null

    private var label = "corn_labels.txt"
    private var model = "corn_model_with_metadata.tflite"

    fun updateModelAndLabels(labelPath: String, modelPath: String) {
        this.label = labelPath
        this.model = modelPath
        init()
    }

    fun init() {
        try {
            val opt = Interpreter.Options()
            tfLite = Interpreter(loadModelFile(context)!!, opt)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadImage(bitmap: Bitmap): TensorImage {
        inputImageBuffer!!.load(bitmap)

        val cropSize = min(bitmap.width, bitmap.height)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(127.5f, 127.5f))
            .build()
        return imageProcessor.process(inputImageBuffer)
    }

    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity?): MappedByteBuffer? {
        val modelName = model
        val fileDescriptor = activity!!.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classifyImage(bitmap: Bitmap) {
        try {
            val imageTensorIndex = 0
            val imageShape = tfLite!!.getInputTensor(imageTensorIndex).shape()

            imageSizeY = imageShape[1]
            imageSizeX = imageShape[2]

            val imageDataType = tfLite!!.getInputTensor(imageTensorIndex).dataType()
            val probabilityTensorIndex = 0
            val probabilityShape = tfLite!!.getOutputTensor(probabilityTensorIndex).shape()
            val probabilityDataType = tfLite!!.getOutputTensor(probabilityTensorIndex).dataType()

            inputImageBuffer = TensorImage(imageDataType)
            outputProbabilityBuffer =
                TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)

            probabilityProcessor = TensorProcessor.Builder()
                .add(NormalizeOp(0f, 1f))
                .build()

            inputImageBuffer = loadImage(bitmap)
            tfLite!!.run(inputImageBuffer!!.buffer, outputProbabilityBuffer!!.buffer.rewind())

            val results = showResult() ?: emptyList()
            classifierListener.onResults(results, 0L)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showResult(): List<String>? {
        labels = try {
            FileUtil.loadLabels(context, label)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val labeledProbability = TensorLabel(
            labels!!, probabilityProcessor!!.process(outputProbabilityBuffer)
        ).mapWithFloatValue

        val maxValueInMap = Collections.max(labeledProbability.values)

        val result: MutableList<String> = ArrayList()
        for ((key, value) in labeledProbability) {
            if (value == maxValueInMap) {
                result.add(key)
            }
        }
        return result
    }

    interface ClassifierListener {
        fun onError(errorMessage: String)
        fun onResults(results: List<String>, inferenceTime: Long)
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }

}