package com.katiekilroy.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.katiekilroy.myapplication.databinding.FragmentCalibrationBinding
import com.katiekilroy.myapplication.shortestpath.Graph
import com.katiekilroy.myapplication.shortestpath.Vertex

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CalibrationFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentCalibrationBinding? = null
    val database = Firebase.database
    val db = database.getReference("Map")

    var beacon1 = Vertex()
    var beacon2 = Vertex()
    var g = Graph()
    var checktxt = "Make sure you have filled out the following: "
    var check = true

    var weight = 0
    var direction = 0
    var selectedItem = ""
    var description = ""

    private lateinit var spinner: Spinner


    val mDatabase =
        FirebaseDatabase.getInstance()
            .getReference("Map")


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCalibrationBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = binding.spinner
        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.directions_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handle item selection
                selectedItem = parent?.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT)
                    .show()
                parent?.getItemAtPosition(position)

                Toast.makeText(context, "spinner selected", Toast.LENGTH_SHORT).show()

                when (position) {
                    0 -> {
                        // straight selected
                        direction = 0
                    }
                    1 -> {
                        // straight selected
                        direction = 1
                    }
                    2 -> {
                        // Right selected
                        direction = -1
                    }
                    3 -> {
                        // Left Selected
                        direction = 2
                    }
                    4 -> {
                        // Left Selected
                        direction = -2
                    }
                    else -> {
                        // Code to execute if number is not 1, 2, or 3
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection
            }
        }
        binding.buttonUpdate.setOnClickListener {
            check = true
            checktxt = "Make sure you have filled out the following: "
            var i = 0
            var weight_S = ""
            val b1 = binding?.beaconNumberEdit?.text.toString()
            val b2 = binding?.beaconNumberEdit2?.text.toString()
            val b1n = binding?.beacon1NameInput?.text.toString()
            val b2n = binding?.beacon2NameInput?.text.toString()
            weight_S = binding?.distance?.text.toString()
            weight = Integer.parseInt(weight_S)

            description = binding?.description?.text.toString()



            if (b1n.isEmpty()) {
                checktxt += "beacon 1 name, "
                check = false
                i++
            }
            if (b1.isEmpty()) {
                checktxt += " beacon 1 ID,"
                check = false
                i++
            }
            if (b2n.isEmpty()) {
                checktxt += "beacon 2 name,"
                check = false
                i++
            }
            if (b2.isEmpty()) {
                checktxt += " beacon 2 ID,"
                check = false
                i++
            }
            if (weight == 0) {
                checktxt += " beacon 2 ID,"
                check = false
                i++
            }
            if (selectedItem.isEmpty()) {
                checktxt += " direction,"
                check = false
                i++

            }
            if (!check) {
                if (i > 3) {
                    Toast.makeText(
                        context,
                        "Make sure all the fields are filled in",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(context, checktxt, Toast.LENGTH_SHORT).show()
                }
            }
            if (check) {


                val updates1 = HashMap<String, Any>()
                updates1["weight"] = weight
                updates1["direction"] = direction
                updates1["description"] = description
                // Create a new child node under the vertex with a unique key using push()
                val updates2 = HashMap<String, Any>()
                updates2["weight"] = weight
                updates2["direction"] = -direction
                updates2["description"] = description

                var beacon1_check = false
                var beacon2_check = false

                mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

//                check if beacon2 exists
//                loop through vertices
                        for (locationSnapshot in dataSnapshot.children) {


                            if (locationSnapshot.child("Name")
                                    .getValue(String::class.java) == b1n
                            ) {
                                beacon1_check = true
                                mDatabase.child(b1).child(b2).child("Edges")
                                    .updateChildren(updates1)
                                g.addEdgeByID(
                                    b1.toInt(),
                                    b2.toInt(),
                                    b1n,
                                    b2n,
                                    weight.toInt(),
                                    direction,
                                    description
                                )

                            } else if (locationSnapshot.child("Name")
                                    .getValue(String::class.java) == b2n
                            ) {

                                beacon2_check = true
                                mDatabase.child(b2).child(b1).child("Edges")
                                    .updateChildren(updates1)

                            }

                            // getting the edges destination ID and properties from the data base and adding them to the linked list of the app
//                                        mDatabase.child(b1).child(b2).child("Edges").child("direction").setValue(direction)

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
                if (!beacon1_check) {
                    mDatabase.child(b1).child("Name").setValue(b1n)
                    mDatabase.child(b1).child("Edges").child(b2).setValue(updates1)
                    g.addEdgeByID(
                        b1.toInt(),
                        b2.toInt(),
                        b1n,
                        b2n,
                        weight.toInt(),
                        direction,
                        description
                    )
                }
                if (!beacon2_check) {
                    Log.i("Firebase", b2)
                    Log.i("Firebase", beacon2_check.toString())
                    mDatabase.child(b1).child("Name").setValue(b2n)
                    mDatabase.child(b2).child("Edges").child(b1).setValue(updates2)
                }

                val bundle = Bundle().apply {
                    putInt("weight", weight)
                    putInt("direction", -direction)
                    putString("description", description)
                }

                findNavController().navigate(
                    R.id.action_calibrationFragment_to_nav_gallery,
                    bundle
                )

            }

        }
    }


    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        // An item was selected. You can retrieve the selected item using
        parent?.getItemAtPosition(position)

        Toast.makeText(context, "spinner selected", Toast.LENGTH_SHORT).show()

        when (position) {
            0 -> {
                // straight selected
                direction = 0
            }
            1 -> {
                // straight selected
                direction = 1
            }
            2 -> {
                // Right selected
                direction = -1
            }
            3 -> {
                // Left Selected
                direction = 2
            }
            4 -> {
                // Left Selected
                direction = -2
            }
            else -> {
                // Code to execute if number is not 1, 2, or 3
            }
        }
    }


    override fun onNothingSelected(parent: AdapterView<*>) {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}