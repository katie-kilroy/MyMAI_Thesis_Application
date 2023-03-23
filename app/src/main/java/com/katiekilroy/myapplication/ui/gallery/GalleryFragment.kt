package com.katiekilroy.myapplication.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.katiekilroy.myapplication.R
import com.katiekilroy.myapplication.databinding.FragmentGalleryBinding
import com.katiekilroy.myapplication.shortestpath.Edge
import com.katiekilroy.myapplication.shortestpath.Vertex

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    var database = FirebaseDatabase.getInstance()
    var db = database.getReference("Map")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.ScanningButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_gallery_to_calibrationFragment)
        }
        binding.UpdateBeaconButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_gallery_to_UpdateFragment)
        }

//        db.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val vertices = ArrayList<Vertex>()
//
//
//                for (vertexSnapshot in snapshot.children) {
//                    val temp_vertex = Vertex()
//                    val vertexId = vertexSnapshot.key?.toIntOrNull() ?: continue
//                    temp_vertex.setID(vertexId)
//                    val vertexName = vertexSnapshot.child("Name").getValue(String::class.java) ?: continue
//                    temp_vertex.stateName = vertexName
//                    vertices.add(temp_vertex)
//
//                    for (edgeSnapshot in vertexSnapshot.child("Edges").children) {
//                        val destVertexId = edgeSnapshot.key?.toIntOrNull() ?: continue
//                        val description = edgeSnapshot.child("description").getValue(String::class.java) ?: ""
//                        val direction = edgeSnapshot.child("direction").getValue(Int::class.java) ?: 0
//                        val weight = edgeSnapshot.child("weight").getValue(Int::class.java) ?: 0
//                        vertices[vertexId].edgeList.add(Edge(destVertexId, weight, direction, description))
//                    }
//
//
//                }
//
//                // Use the vertices list as needed
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle errors
//            }
//        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}