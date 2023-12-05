//package com.xd.mvvm.testrecorder.opencv
//
//import org.opencv.core.*
//import org.opencv.imgcodecs.Imgcodecs
//import org.opencv.imgproc.Imgproc
//import java.io.ByteArrayInputStream
//import org.opencv.core.MatOfPoint
//import org.opencv.core.Scalar
//import org.opencv.core.Size
//
//// Load OpenCV library
////System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
//
//fun detectUIButtons(width: Int, height: Int, imageBytes: ByteArray): Mat {
//    // Convert ByteArray to BufferedImage
//    val inputStream = ByteArrayInputStream(imageBytes)
//
//    // Convert BufferedImage to Mat
//    val mat = Mat(height, width, CvType.CV_8UC3)
//    mat.put(0, 0, imageBytes)
//
//    // Convert to grayscale and apply threshold
//    val grayMat = Mat()
//    Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY)
//    Imgproc.threshold(grayMat, grayMat, 100.0, 255.0, Imgproc.THRESH_BINARY)
//
//    // Find contours
//    val contours: List<MatOfPoint> = ArrayList()
//    val hierarchy = Mat()
//    Imgproc.findContours(
//        grayMat,
//        contours,
//        hierarchy,
//        Imgproc.RETR_TREE,
//        Imgproc.CHAIN_APPROX_SIMPLE
//    )
//
//    // Filter contours by shape and size to detect buttons
//    for (contour in contours) {
//        val rect: Rect = Imgproc.boundingRect(contour)
//        // Assuming buttons are rectangular and have a certain size
//        if (rect.width > 20 && rect.height > 10) {
//            Imgproc.rectangle(mat, rect.br(), rect.tl(), Scalar(0.0, 255.0, 0.0), 2)
//        }
//    }
//
//    return mat
//}
//
//// Usage example
//// val imageBytes: ByteArray = // Load your image bytes here
//// val resultMat = detectUIButtons(imageBytes)
//// Imgcodecs.imwrite("output.jpg", resultMat) // Save or display the output
//
