package com.bussiness.awpl.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.bussiness.awpl.NetworkResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object VideoCallCheck {

    fun isChannelAvailable(
        context: Context,
        channelName: String,
        onResult: (Boolean) -> Unit
    ) {
        val callRef = FirebaseDatabase.getInstance()
            .getReference("calls")
            .child(channelName)
            .child("users")

        callRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isAvailable = snapshot.childrenCount < 1
                Log.d("TESTING_VIDEO_CHECKING","VALUE InSide "+isAvailable)

                onResult(isAvailable)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error checking call status", Toast.LENGTH_SHORT).show()
                onResult(false) // default to false on error
            }
        })
    }


    fun checkAndJoinAppointmentCall(
        context: Context,
        appointmentId: String,
        onJoin: () -> Unit
    ) {
        val callRef = FirebaseDatabase.getInstance()
            .getReference("calls")
            .child(appointmentId)

        callRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alreadyJoined = snapshot.child("joined").getValue(Boolean::class.java) ?: false

                if (alreadyJoined) {
                    // Someone already joined
                    LoadingUtils.showSuccessDialog(
                        context,
                        "This appointment has already been joined by someone."
                    ) {
                        if (context is Activity) {
                            context.finish()
                        }
                    }
                }
                else {
                    try {
                        // Safely mark as joined
                        callRef.updateChildren(
                            mapOf(
                                "joined" to true,
                                "joinedAt" to System.currentTimeMillis(),
                                "by" to "Android App"
                            )
                        ).addOnSuccessListener {
                            // Set onDisconnect to auto reset joined state if app crashes or disconnects
                            try {
                                callRef.child("joined").onDisconnect().setValue(false)
                            } catch (disconnectException: Exception) {
                                // Log only — doesn't block joining
                                Log.e("CALL_JOIN", "onDisconnect failed: ${disconnectException.message}")
                            }

                            onJoin() // ✅ Safe to join call now
                        }.addOnFailureListener { e ->
                            LoadingUtils.hideDialog()
                            LoadingUtils.showErrorDialog(
                                context,
                                "Failed to join appointment. Please try again."
                            )
                        }
                    } catch (e: Exception) {
                        LoadingUtils.hideDialog()
                        LoadingUtils.showErrorDialog(
                            context,
                            "Failed to join appointment. Please try again."
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                LoadingUtils.hideDialog()
                LoadingUtils.showErrorDialog(
                    context,
                    "Failed to join appointment. Please try again."
                )
            }
        })
    }
}