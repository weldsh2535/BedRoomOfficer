package com.version1.badroom.ui.home

import android.R
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.version1.badroom.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var selectRoomNumber: Int = 1
    private lateinit var imageCapture: ImageCapture
    private lateinit var imageView: ImageView
    private val REQUEST_TAKE_PHOTO = 1
    private val REQUEST_CAMERA_PERMISSION = 1
    lateinit var photoPath: String

    private var uri: Uri? = null
    private lateinit var storageRef: StorageReference

    private val cameraRequest = 1888

    // storageRef = Firebase.storage.reference
    val db = Firebase.firestore
    var loading: ProgressBar? = null
    var datePicker: DatePickerDialog? = null
    val checkRoomBox: ArrayList<String> = arrayListOf<String>()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        storageRef = FirebaseStorage.getInstance().getReference("Images")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView = binding.appname
        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        textView!!.startAnimation(fadeInAnimation)


        loading = binding.loading
        lifecycleScope.launch {
            loading!!.visibility = View.VISIBLE
            search()
        }
        // initialising the calendar
        val calendar = Calendar.getInstance()

        // initialising the layout
        val editText = binding.registerDate
        val day = calendar[Calendar.DAY_OF_MONTH]
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]

        // initialising the datepickerdialog
        datePicker = DatePickerDialog(requireContext())

        // click on edittext to set the value
        editText!!.setOnClickListener {
            datePicker = DatePickerDialog(
                requireContext(),
                { view, year, month, dayOfMonth -> // adding the selected date in the edittext
                    editText!!.setText(dayOfMonth.toString() + "/" + (month + 1) + "/" + year)
                }, year, month, day
            )

            // set maximum date to be selected as today
            datePicker!!.datePicker.minDate = calendar.timeInMillis

            // show the dialog
            datePicker!!.show()
        }

//        binding.registerDate.setOnClickListener {
//            view?.let { it1 -> showDateTimePicker(it1, 1) }
//        }
        binding.cbone.setOnClickListener {
            binding.cbone.isClickable = true
            selectRoomNumber = 1
            binding.selectedId.text = "1"
            binding.cbtwo.isChecked = checkRoomBox.contains("2")
            binding.cbthree.isChecked = checkRoomBox.contains("3")
            binding.chfour.isChecked = checkRoomBox.contains("4")
            binding.ckfive.isChecked = checkRoomBox.contains("5")
            binding.cksix.isChecked = checkRoomBox.contains("6")
            binding.ckseven.isChecked = checkRoomBox.contains("7")
            binding.ckeight.isChecked = checkRoomBox.contains("8")
            binding.cknight.isChecked = checkRoomBox.contains("9")
            binding.ckten.isChecked = checkRoomBox.contains("10")
        }
        binding.cbtwo.setOnClickListener {
            binding.cbone.isChecked = checkRoomBox.contains("1")
            binding.cbtwo.isChecked = true
            selectRoomNumber = 2
            binding.selectedId.text = "2"
            binding.cbthree.isChecked = checkRoomBox.contains("3")
            binding.chfour.isChecked = checkRoomBox.contains("4")
            binding.ckfive.isChecked = checkRoomBox.contains("5")
            binding.cksix.isChecked = checkRoomBox.contains("6")
            binding.ckseven.isChecked = checkRoomBox.contains("7")
            binding.ckeight.isChecked = checkRoomBox.contains("8")
            binding.cknight.isChecked = checkRoomBox.contains("9")
            binding.ckten.isChecked = checkRoomBox.contains("10")
        }
        binding.cbthree.setOnClickListener {
            binding.cbone.isChecked = checkRoomBox.contains("1")
            binding.cbtwo.isChecked = checkRoomBox.contains("2")

            binding.cbthree.isChecked = true
            selectRoomNumber = 3
            binding.selectedId.text = "3"
            binding.chfour.isChecked = checkRoomBox.contains("4")
            binding.ckfive.isChecked = checkRoomBox.contains("5")
            binding.cksix.isChecked = checkRoomBox.contains("6")
            binding.ckseven.isChecked = checkRoomBox.contains("7")
            binding.ckeight.isChecked = checkRoomBox.contains("8")
            binding.cknight.isChecked = checkRoomBox.contains("9")
            binding.ckten.isChecked = checkRoomBox.contains("10")
        }
        binding.chfour.setOnClickListener {
            binding.cbone.isChecked = checkRoomBox.contains("1")
            binding.cbtwo.isChecked = checkRoomBox.contains("2")
            binding.cbthree.isChecked = checkRoomBox.contains("3")
            binding.chfour.isChecked = true
            selectRoomNumber = 4
            binding.selectedId.text = "4"
            binding.ckfive.isChecked = checkRoomBox.contains("5")
            binding.cksix.isChecked = checkRoomBox.contains("6")
            binding.ckseven.isChecked = checkRoomBox.contains("7")
            binding.ckeight.isChecked = checkRoomBox.contains("8")
            binding.cknight.isChecked = checkRoomBox.contains("9")
            binding.ckten.isChecked = checkRoomBox.contains("10")
        }

        binding.ckfive.setOnClickListener {
            binding.cbone.isChecked = checkRoomBox.contains("1")
            binding.cbtwo.isChecked = checkRoomBox.contains("2")
            binding.cbthree.isChecked = checkRoomBox.contains("3")
            binding.chfour.isChecked = checkRoomBox.contains("4")
            binding.ckfive.isChecked = true
            selectRoomNumber = 5
            binding.selectedId.text = "5"
            binding.cksix.isChecked = checkRoomBox.contains("6")
            binding.ckseven.isChecked = checkRoomBox.contains("7")
            binding.ckeight.isChecked = checkRoomBox.contains("8")
            binding.cknight.isChecked = checkRoomBox.contains("9")
            binding.ckten.isChecked = checkRoomBox.contains("10")
        }
        binding.cksix.setOnClickListener {
            binding.cbone.isChecked = checkRoomBox.contains("1")
            binding.cbtwo.isChecked = checkRoomBox.contains("2")
            binding.cbthree.isChecked = checkRoomBox.contains("3")
            binding.chfour.isChecked = checkRoomBox.contains("4")
            binding.ckfive.isChecked = checkRoomBox.contains("5")
            binding.cksix.isChecked = true
            selectRoomNumber = 6
            binding.selectedId.text = "6"
            binding.ckseven.isChecked = checkRoomBox.contains("7")
            binding.ckeight.isChecked = checkRoomBox.contains("8")
            binding.cknight.isChecked = checkRoomBox.contains("9")
            binding.ckten.isChecked = checkRoomBox.contains("10")
        }
        binding.ckseven.setOnClickListener {
            binding.cbone.isChecked = checkRoomBox.contains("1")
            binding.cbtwo.isChecked = checkRoomBox.contains("2")
            binding.cbthree.isChecked = checkRoomBox.contains("3")
            binding.chfour.isChecked = checkRoomBox.contains("4")
            binding.ckfive.isChecked = checkRoomBox.contains("5")
            binding.cksix.isChecked = checkRoomBox.contains("6")
            binding.ckseven.isChecked = true
            selectRoomNumber = 7
            binding.selectedId.text = "7"
            binding.ckeight.isChecked = checkRoomBox.contains("8")
            binding.cknight.isChecked = checkRoomBox.contains("9")
            binding.ckten.isChecked = checkRoomBox.contains("10")
        }
        binding.ckeight.setOnClickListener {
            binding.cbone.isChecked = checkRoomBox.contains("1")
            binding.cbtwo.isChecked = checkRoomBox.contains("2")
            binding.cbthree.isChecked = checkRoomBox.contains("3")
            binding.chfour.isChecked = checkRoomBox.contains("4")
            binding.ckfive.isChecked = checkRoomBox.contains("5")
            binding.cksix.isChecked = checkRoomBox.contains("6")
            binding.ckseven.isChecked = checkRoomBox.contains("7")
            binding.ckeight.isChecked = true
            selectRoomNumber = 8
            binding.selectedId.text = "8"
            binding.cknight.isChecked = checkRoomBox.contains("9")
            binding.ckten.isChecked = checkRoomBox.contains("10")
        }
        binding.cknight.setOnClickListener {
            binding.cbone.isChecked = checkRoomBox.contains("1")
            binding.cbtwo.isChecked = checkRoomBox.contains("2")
            binding.cbthree.isChecked = checkRoomBox.contains("3")
            binding.chfour.isChecked = checkRoomBox.contains("4")
            binding.ckfive.isChecked = checkRoomBox.contains("5")
            binding.cksix.isChecked = checkRoomBox.contains("6")
            binding.ckseven.isChecked = checkRoomBox.contains("7")
            binding.ckeight.isChecked = checkRoomBox.contains("8")
            binding.cknight.isChecked = true
            selectRoomNumber = 9
            binding.selectedId.text = "9"
            binding.ckten.isChecked = checkRoomBox.contains("10")
        }
        binding.ckten.setOnClickListener {
            binding.cbone.isChecked = checkRoomBox.contains("1")
            binding.cbtwo.isChecked = checkRoomBox.contains("2")
            binding.cbthree.isChecked = checkRoomBox.contains("3")
            binding.chfour.isChecked = checkRoomBox.contains("4")
            binding.ckfive.isChecked = checkRoomBox.contains("5")
            binding.cksix.isChecked = checkRoomBox.contains("6")
            binding.ckseven.isChecked = checkRoomBox.contains("7")
            binding.ckeight.isChecked = checkRoomBox.contains("8")
            binding.cknight.isChecked = checkRoomBox.contains("9")
            binding.ckten.isChecked = true
            selectRoomNumber = 10
            binding.selectedId.text = "10"
        }
        binding.send.setOnClickListener {
            // saveData()
            val iv1 = binding.picture
            iv1.buildDrawingCache()
            val bmap = iv1.drawingCache
            val bos = ByteArrayOutputStream()
            bmap.compress(CompressFormat.PNG, 100, bos)
            val bb = bos.toByteArray()
            val image = Base64.encodeToString(bb, Base64.DEFAULT)

            val fullname = binding.fullname.text.toString()
            val phone = binding.txtphonenumber.text.toString()
            val registerDate = binding.registerDate.text.toString()
            val kebele = binding.txtkebele.text.toString()
            val houseNumber = binding.txthousenumber.text.toString()
            if (fullname.isEmpty()) binding.fullname.error = "ስም ያስገቡ"
            if (phone.isEmpty()) binding.txtphonenumber.error = "ስልክ ቁጥር ያስገቡ"
            if (registerDate.isEmpty()) binding.registerDate.error = "የምድገባ ቀን ያስገቡ"
            if (kebele.isEmpty()) binding.txtkebele.error = "የቀበሌ ቁጥር ያስገቡ"
            if (houseNumber.isEmpty()) binding.txthousenumber.error = "የቤት ቁጥር ያስገቡ"
            if (checkValidation()) {
                loading!!.visibility = View.VISIBLE
                val user = hashMapOf(
                    "fullname" to binding.fullname.text.toString(),
                    "phonenumber" to binding.txtphonenumber.text.toString(),
                    "kebele" to binding.txtkebele.text.toString(),
                    "housenumber" to binding.txthousenumber.text.toString(),
                    "roomnumber" to selectRoomNumber,
                    "registerDate" to binding.registerDate.text.toString(),
                    "exitDate" to "",
                    "status" to 0,
                    "imageUrl" to image
                )
                // Add a new document with a generated ID
                db.collection("users")
                    .add(user)
                    .addOnSuccessListener { documentReference ->
                        clearFields()
                        loading!!.visibility = View.GONE
                        Toast.makeText(context, "ተመዝግቧል!!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Error adding user document: ${e.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
            }
        }

        binding.captureButton.setOnClickListener {
            takepicture()
            //  takePicture(it)
        }
        return root
    }

    private fun clearFields() {
        binding.fullname.text?.clear()
        binding.registerDate.text?.clear()
        binding.txtphonenumber.text?.clear()
        binding.picture.setImageResource(R.drawable.bottom_bar)
        binding.txtkebele.text?.clear()
        binding.txthousenumber.text?.clear()
    }

    private fun checkValidation(): Boolean {
        val fullname = binding.fullname.text.toString()
        val phone = binding.txtphonenumber.text.toString()
        // val roomNumber = binding.txtroomnumber.toString().toInt()
        val registerDate = binding.registerDate.text.toString()
        val kebele = binding.txtkebele.text.toString()
        val houseNumber = binding.txthousenumber.text.toString()
        if (fullname.isEmpty() && phone.isEmpty() && registerDate.isEmpty() && kebele.isEmpty() && houseNumber.isEmpty()) {
            return false
        }
        return true
    }

    @SuppressLint("SuspiciousIndentation")
    private fun takepicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
            }
            if (photoFile != null) {
                val photoUrl = FileProvider.getUriForFile(
                    requireContext(), "com.vesrion1.android.fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUrl)
                startActivityForResult(intent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    private fun createImageFile(): File? {
        val fileName = "myPicture"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            fileName,
            ".jpg",
            storageDir
        )
        photoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.i("Tag", photoPath)
            if (data != null) {
                uri = Uri.parse(photoPath)
            }
            binding.picture.setImageURI(Uri.parse(photoPath))
//            val imageBitmap = data?.extras?.get("data") as Bitmap
//            binding.imageView.setImageBitmap(imageBitmap)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == cameraRequest && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Camera permission granted
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun search() {
        //read documnet
        db.collection("users")
            .whereEqualTo("status", 0)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val index = document.data["roomnumber"].toString()
                    checkRoomBox.add(index)
                    if (index == "1") {
                        binding.cbone.isClickable = false
                        binding.cbone.isChecked = true
                    } else if (index == "2") {
                        binding.cbtwo.isClickable = false
                        binding.cbtwo.isChecked = true
                    } else if (index == "3") {
                        binding.cbthree.isClickable = false
                        binding.cbthree.isChecked = true
                    } else if (index == "4") {
                        binding.chfour.isClickable = false
                        binding.chfour.isChecked = true
                    } else if (index == "5") {
                        binding.ckfive.isClickable = false
                        binding.ckfive.isChecked = true
                    } else if (index == "6") {
                        binding.cksix.isClickable = false
                        binding.cksix.isChecked = true
                    } else if (index == "7") {
                        binding.ckseven.isClickable = false
                        binding.ckseven.isChecked = true
                    } else if (index == "8") {
                        binding.ckeight.isClickable = false
                        binding.ckeight.isChecked = true
                    } else if (index == "9") {
                        binding.cknight.isClickable = false
                        binding.cknight.isChecked = true
                    } else if (index == "10") {
                        binding.ckten.isClickable = false
                        binding.ckten.isChecked = true
                    }
                    loading!!.visibility = View.GONE
                }

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }


}