package com.katiekilroy.myapplication.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.katiekilroy.myapplication.R
import com.katiekilroy.myapplication.databinding.FragmentNameChangeBinding
import com.katiekilroy.myapplication.shortestpath.Graph
import com.katiekilroy.myapplication.shortestpath.Vertex


class NameChangeFragment : Fragment() {



    private var _binding: FragmentNameChangeBinding? = null
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

        _binding = FragmentNameChangeBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonUpdate.setOnClickListener {
            val beacon_ID = binding?.beaconNumberEdit2?.text.toString()
            val beacon_name = binding?.beacon1NameInput2?.text.toString()




            if (beacon_name.isEmpty()) {

                Toast.makeText(context, "No New Name inputted", Toast.LENGTH_SHORT).show()
            }
            else{
                mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

//                check if beacon2 exists
//                loop through vertices
                        for (locationSnapshot in dataSnapshot.children) {

                            if (locationSnapshot.key == beacon_ID) {
                                mDatabase.child(beacon_ID).child("Name").setValue(beacon_name)

                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                Toast.makeText(context, "New Name: $beacon_name inputted", Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.action_nameChangeFragment_to_nav_gallery)



            }

        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}