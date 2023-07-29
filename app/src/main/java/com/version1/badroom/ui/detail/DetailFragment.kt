package com.version1.badroom.ui.detail

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.version1.badroom.databinding.FragmentDetailBinding
import com.version1.badroom.ui.Dashboard.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // storageRef = Firebase.storage.reference
    val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val model = ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)

        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        model.fullname.observe(viewLifecycleOwner, Observer { message ->
            binding.fullname.text = message.toString()
        })

        model.kebele.observe(viewLifecycleOwner, Observer { message ->
            binding.kebele.text = message.toString()
        })

        model.roomNumber.observe(viewLifecycleOwner, Observer { message ->
            binding.roomnumber.text = message.toString()
        })

        model.phonenumber.observe(viewLifecycleOwner, Observer { message ->
            binding.phone.text = message
        })
        model.images.observe(viewLifecycleOwner, Observer { message ->
            //decode base64 string to image
            val imageBytes = Base64.decode(message, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.images.setImageBitmap(decodedImage)
        })
        binding.exitDate.setOnClickListener {
            view?.let { it -> showDateTimePicker(it) }
        }
        binding.send.setOnClickListener {
            // Update a new document with a generated ID
            Log.i("UsersId",model.userId.value.toString())
            model.userId.value?.let { it1 ->
                db.collection("users").document(it1)
                    .update("exitDate", binding.exitDate.text.toString(),"status",1)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(context, "አልጋው ተለቋል!!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Error adding user document: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
        return root
    }

    fun showDateTimePicker(view: View) {
        val currentDateTime = Calendar.getInstance()
        val year = currentDateTime.get(Calendar.YEAR)
        val month = currentDateTime.get(Calendar.MONTH)
        val day = currentDateTime.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            context?.let {
                DatePickerDialog(it, { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDateTime = Calendar.getInstance()
                    selectedDateTime.set(selectedYear, selectedMonth, selectedDay)

                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = format.format(selectedDateTime.time)
                    binding.exitDate.setText(formattedDate)

                }, year, month, day)
            }

        if (datePickerDialog != null) {
            datePickerDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}