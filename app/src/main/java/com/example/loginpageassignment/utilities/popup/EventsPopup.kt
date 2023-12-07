package com.example.loginpageassignment.utilities.popup

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.loginpageassignment.R
import com.example.loginpageassignment.dataobjects.CurrentUser
import com.example.loginpageassignment.dataobjects.Location
import com.example.loginpageassignment.dataobjects.PSB_Event
import com.example.loginpageassignment.utilities.queue.QueueManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class EventsPopup(private val context: Context) : DetailsPopup()
{
    private var alertDialog: AlertDialog? = null

    override fun showDetails(event: PSB_Event, user: CurrentUser)
    {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.event_details_popup, null)
        dialogBuilder.setView(dialogView)

        val eventNameTextView = dialogView.findViewById<TextView>(R.id.popupEventName)
        val eventTimeTextView = dialogView.findViewById<TextView>(R.id.popupEventTime)
        val eventDateTextView = dialogView.findViewById<TextView>(R.id.popupEventDate)
        val eventLocationTextView = dialogView.findViewById<TextView>(R.id.popupEventLocation)
        val eventDescriptionTextView = dialogView.findViewById<TextView>(R.id.popupEventDescription)
        val viewOnMapButton = dialogView.findViewById<Button>(R.id.popupViewOnMapButton)
        val addToQueueButton = dialogView.findViewById<Button>(R.id.popupAddToQueueButton)
        val backButton = dialogView.findViewById<Button>(R.id.popupCloseButton)

        // Set event details
        eventNameTextView.text = event.eventName
        eventTimeTextView.text = event.eventTime
        eventDateTextView.text = event.eventDate
        eventLocationTextView.text = event.eventLocation
        eventDescriptionTextView.text = event.eventDescription

        // Button actions
        viewOnMapButton.setOnClickListener {
            //TODO: make function
        }

        addToQueueButton.setOnClickListener {
            val queueManager = QueueManager.getQueueManager(user.username, context)
            val locationRef = FirebaseFirestore.getInstance().collection("Locations")

            locationRef.whereEqualTo("name", event.eventLocation).get()
                .addOnSuccessListener { documents ->
                    val location = documents.map{ doc -> doc.toObject(Location::class.java) }
                    queueManager?.addToQueue(location[0])
                    alertDialog?.dismiss()
                }
                .addOnFailureListener { exception ->
                    Log.e("SearchPopup", "Error getting search results", exception)
                }
        }

        backButton.setOnClickListener {
            alertDialog?.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
        alertDialog = dialog
    }
}