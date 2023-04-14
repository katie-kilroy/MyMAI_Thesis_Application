package com.katiekilroy.myapplication.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.katiekilroy.myapplication.R
import com.katiekilroy.myapplication.databinding.FragmentUpdateBinding
import com.katiekilroy.myapplication.shortestpath.Graph
import com.katiekilroy.myapplication.shortestpath.Vertex

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class UpdateBeaconFragment : Fragment() {

    var beacon1 = Vertex()
    var beacon2 = Vertex()
    var g = Graph()
    var checktxt = "Make sure you have filled out the following: "
    var check = true
    var checkv1 = false
    var checkv2 = false


    val mDatabase =
        FirebaseDatabase.getInstance()
            .getReference("Map")

    private lateinit var spinner: Spinner

    private var _binding: FragmentUpdateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.buttonUpdate.setOnClickListener {
            check = true
            checkv1 = false
            checkv2 = false

            checktxt = "Make sure you have filled out the following: "
            var i = 0

            val b1 = binding?.beaconNumberEdit?.text.toString()
            val b2 = binding?.beaconNumberEdit2?.text.toString()
//            val b1n = binding?.beacon1NameInput?.text.toString()
//            val b2n = binding?.beacon2NameInput?.text.toString()


//            if (b1n.isEmpty()) {
//                checktxt += "beacon 1 name, "
//                check = false
//                i++
//            }
            if (b1.isEmpty()) {
                checktxt += " beacon 1 ID,"
                check = false
                i++
            }
//            if (b2n.isEmpty()) {
//                checktxt += "beacon 2 name,"
//                check = false
//                i++
//            }
            if (b2.isEmpty()) {
                checktxt += " beacon 2 ID,"
                check = false
                i++
            }

            if (!check) {
                Toast.makeText(context, checktxt, Toast.LENGTH_SHORT).show()
            }

            if (check) {
                mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

//                check if beacon2 exists
//                loop through vertices
                        for (locationSnapshot in dataSnapshot.children) {


                            Log.i("Vertices", "checking vertices")
                            if (locationSnapshot
                                    .key == b1
                            ) {
                                checkv1 = true
                                Log.i("Vertices", "Vertex 1 exists")
                            } else if (locationSnapshot.key == b2
                            ) {
                                checkv2 = true
                                Log.i("Vertices", "Vertex 2 exists")
                            }
                        }

                        Log.i("Vertices", "$checkv1 $checkv2")
                        if (checkv1 && checkv2) {
                            val bundle = Bundle()
                            bundle.putString("B1_ID", b1)
                            bundle.putString("B2_ID", b2)
//                        bundle.putString("B1_name", b1n)
//                        bundle.putString("B2_B1_name", b2n)

                            Toast.makeText(context, "button clicked", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(
                                R.id.action_UpdateFragment_to_UpdateValuesFragment,
                                bundle
                            )
//
                        } else {

                            Toast.makeText(context, "Vertiecs don't exist", Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
