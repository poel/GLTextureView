package cc.glglore.gltextureview

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.Surface
import android.view.TextureView
import cc.glglore.gltextureview.helper.PermissionHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TextureView.SurfaceTextureListener {

    lateinit var camera: Camera
    lateinit var backCameraInfo: Camera.CameraInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textureView.surfaceTextureListener = this
    }

    override fun onResume() {
        super.onResume()

        if (!PermissionHelper.hasRequiredPermissions(this)) {
            PermissionHelper.requestRequiredPermissions(this, true)
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        camera.stopPreview()
        camera.release()
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        Log.d("!!!!", "onSurfaceTextureAvailable!!!")
        val backCamera = getBackCamera()
        val backCameraId = backCamera!!.second
        backCameraInfo = backCamera.first
        camera = Camera.open(backCameraId)
        cameraDisplayRotation()

        try {
            camera.setPreviewTexture(surface)
            camera.startPreview()
        } finally {

        }
    }

    fun cameraDisplayRotation() {
        val rotation = windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        val displayOrientation = (backCameraInfo.orientation - degrees + 360) % 360
        camera.setDisplayOrientation(displayOrientation)
    }

    private fun getBackCamera(): Pair<Camera.CameraInfo, Int>? {
        val cameraInfo = Camera.CameraInfo()
        val numberOfCameras = Camera.getNumberOfCameras()

        for (i in 0 until numberOfCameras) {
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return Pair(cameraInfo, Integer.valueOf(i))
            }
        }
        return null
    }
}
