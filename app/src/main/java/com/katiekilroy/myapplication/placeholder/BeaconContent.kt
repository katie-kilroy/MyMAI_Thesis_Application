package com.katiekilroy.myapplication.placeholder

import com.katiekilroy.myapplication.shortestpath.Edge
import com.katiekilroy.myapplication.shortestpath.Graph
import com.katiekilroy.myapplication.shortestpath.Vertex

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object BeaconContent {
    var vertices: List<Vertex> = ArrayList()
    var v = Vertex()
    var g: Graph = Graph()
    var e: Edge = Edge()

    /**
     * An array of sample (placeholder) items.
     */
    val ITEMS: MutableList<BeaconItem> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, BeaconItem> = HashMap()

    private val COUNT = vertices.size

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(createBeaconItem(i))
        }
    }

    private fun addItem(item: BeaconItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createBeaconItem(position: Int): BeaconItem {
        val v: Vertex = g.getVertexByID(position)
        val e: List<Edge>
        e = v.edgeList
        var weight = 0
        var direction = 0
        for (edge in e) {
            if (edge.getDestinationVertexID() == position) {
                weight = edge.getWeight()
                direction = edge.getDirection()
            }
        }

        return BeaconItem(position.toString(), v.getStateName(), weight.toString(), direction.toString())
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class BeaconItem(val id: String, val name: String, val weight: String, val direction: String) {
        override fun toString(): String = name
    }
}