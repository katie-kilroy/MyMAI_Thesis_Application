package com.katiekilroy.myapplication.ui.user

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.katiekilroy.myapplication.R
import com.katiekilroy.myapplication.databinding.FragmentHomeBinding
import com.katiekilroy.myapplication.shortestpath.Graph


@Suppress("SENSELESS_COMPARISON")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
//    private lateinit var locationAdapter: ArrayAdapter<String>
//    private val locationNameList: MutableList<String> = ArrayList()

    var fromVertex = 0
    var toVertex = 0
    var selectedItem = ""
    data class LocationItem(val id: Int, val name: String)
    val locationList = mutableListOf<LocationItem>()
val locationList1 = mutableListOf<LocationItem>()
    val locationList2 = mutableListOf<LocationItem>()
//    val actualLocations = mutableListOf<LocationItem>()


    var g = Graph()
    val mDatabase =
        FirebaseDatabase.getInstance()
            .getReference("Map")

    private lateinit var spinner_start: Spinner
    private lateinit var spinner_destination: Spinner


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        spinner_start = binding.spinner2
        spinner_destination = binding.spinner3
        g.printGraph()


        getData()



        binding.buttonUpdate.setOnClickListener {
            if ((toVertex == 0) || (fromVertex == -1)) {
                Toast.makeText(
                    requireContext(),
                    "Make Sure you pick start and end locations",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (toVertex == fromVertex){
            Toast.makeText(
                requireContext(),
                "Make Sure you pick different start and end locations",
                Toast.LENGTH_SHORT
            ).show()
        } else {

                val bundle = Bundle().apply {
                    putInt("toVertex", toVertex)
                    putInt("fromVertex", fromVertex)
                }

                Toast.makeText(
                    requireContext(),
                    "Start: $fromVertex and End: $toVertex",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigate(R.id.action_nav_home_to_ScanningFragment, bundle)
            }
            {

            }
        }
        return root
    }

    private fun getData() {

        Log.i("Database", "Database reading starting")
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

//                checking if the map is empty,
                val checklist = g.checkVertices()
                val locationNameList = mutableListOf<String>()
                val IDList = mutableListOf<Int>()
                locationNameList.add("Please Select a Location")

//                loop through vertices
                for (locationSnapshot in dataSnapshot.children) {

                    var num =  locationSnapshot.key
                    val edgesSnapshot = locationSnapshot.child("Edges")
                    val locationName = locationSnapshot.child("Name").getValue(String::class.java)

//                    getting ID of vertex
                    if (locationName != null){

                        locationNameList.add(locationName)

                        locationList.add(LocationItem(Integer.parseInt(num), locationName))
//                        actualLocations.add(LocationItem(Integer.parseInt(num), locationName))
//                        adding vertex name to name list for spinner


//                        if it is empty it will return false are going to fill the current vertex's edges
                        if (!checklist) {

                            Log.i(
                                "Graph", "Adding Vertices to List"
                            )

//                            iterating through edges of the current vertex
                            if (edgesSnapshot.exists()) {

                                for (edgeSnapshot in edgesSnapshot.children) {

                                    // getting the edges destination ID and properties from the data base and adding them to the linked list of the app

                                    val id2 =  edgeSnapshot.key
                                    val direction = edgeSnapshot.child("direction").value as Long
                                     val weight = edgeSnapshot.child("weight").value as Long
                                    val description =
                                        edgeSnapshot.child("description").value as String
                                    val name =
                                        mDatabase.child(id2.toString()).child("Name").toString()
                                    if ((num != null) && (id2 != null)) {
                                            g.addEdgeByID(
                                                Integer.parseInt(num),
                                                Integer.parseInt(id2),
                                                locationName,
                                                name,
                                                weight.toInt(),
                                                direction.toInt(),
                                                description
                                            )

                                    }
                                }
                            }



                            Log.i("vertices size:", num.toString())
                        }
                    }
                }
//                actualLocations.sortBy { it.name }
                locationList1.add(LocationItem(0, "Please Select a Location"))
                locationList2.add(LocationItem(0, "Please Select a Location"))
                locationList1.add(LocationItem(1, "Current Location"))
                locationList1.addAll(locationList)
                locationList2.addAll(locationList)
//                Log.i("Print", "Printing")
                g.printGraph()

                if (!g.checkVertices()) {

                    Log.e("Error:", "no vertex added to Vertices")
                }


//                val locationAdapter = ArrayAdapter(
//                    requireContext(),
//                    android.R.layout.simple_spinner_item,
//                    locationNameList
//                )
                val locationAdapter1 = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    locationList1.map { it.name }
                )
                val locationAdapter2 = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    locationList2.map { it.name }
                )

                locationAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                locationAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner_start.adapter = locationAdapter1
                spinner_destination.adapter = locationAdapter2

                // setting up and getting value from spinner start location
                spinner_start.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            // Get the selected location name
                            val selectedLocationName = parent?.getItemAtPosition(position) as String

                        binding.selectedValue.setText(selectedLocationName)
                            // Find the index of the selected location in the database
//                            fromVertex = position

//                            fromVertex = locationList[position].id
                            fromVertex = locationList1[position].id
                            Log.i("From Vertex ", fromVertex.toString())
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Do nothing
                        }
                    }

                // setting up and getting value from spinner end location
                spinner_destination.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            // Get the selected location name
                            val selectedLocationName = parent.getItemAtPosition(position) as String

                            binding.selectedValue2.setText(selectedLocationName)
                            // Find the index of the selected location in the database
//                            toVertex = position

                            toVertex = locationList2[position].id
                            Log.i("To Vertex ", toVertex.toString())
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Do nothing
                        }
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle the error
            }
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        locationList1.clear()
        locationList2.clear()
//        actualLocations.clear()
        locationList.clear()
    }


}