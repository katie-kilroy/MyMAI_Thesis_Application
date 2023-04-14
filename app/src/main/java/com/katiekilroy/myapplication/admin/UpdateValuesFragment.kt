package com.katiekilroy.myapplication.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.katiekilroy.myapplication.R
import com.katiekilroy.myapplication.databinding.FragmentUpdateValuesBinding
import com.katiekilroy.myapplication.shortestpath.Graph


class UpdateValuesFragment : Fragment(), AdapterView.OnItemSelectedListener   {


    private var _binding: FragmentUpdateValuesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    var fromVertex = 0
    var toVertex = 0
    var g = Graph()
    var weight = ""
    var direction = 0
    var selectedItem = ""
    var description = ""

    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        fromVertex = arguments?.getString("B1_ID")?.toInt()!!
        toVertex = arguments?.getString("B2_ID")?.toInt()!!


        val mDataref = FirebaseDatabase.getInstance().getReference("Map")
        mDataref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var text = "Update the path between "

                text += dataSnapshot.child(fromVertex.toString()).child("Name").getValue()!!.toString() + " and "
                text += dataSnapshot.child(toVertex.toString()).child("Name").getValue()!!.toString()
                binding?.title?.setText(text)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        // adding the values for the inputted beacons
        val databaseReference = FirebaseDatabase.getInstance().getReference("Map/$fromVertex/Edges/$toVertex")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var text = "Update the path between "

                dataSnapshot.child("weight").getValue()!!.toString()

                binding?.distanceInput?.setText(
                    dataSnapshot.child("weight").getValue()!!.toString()
                )
                direction = dataSnapshot.child("direction").getValue(Int::class.java)!!
                binding?.description?.setText(
                    dataSnapshot.child("description").getValue()!!.toString()
                )

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        _binding = FragmentUpdateValuesBinding.inflate(inflater, container, false)
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
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle item selection
                selectedItem = parent?.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection
            }
        }
        binding.buttonUpdate.setOnClickListener {
            weight = binding?.distanceInput?.text.toString()
            description = binding?.description?.text.toString()
            Log.d("MyActivity", "Result: $weight")
            g.updateEdgeByID(fromVertex, toVertex, weight.toInt(), direction, description)
            findNavController().navigate(R.id.action_UpdateValuesFragment_to_nav_gallery)
        }


    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
            } 4 -> {
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