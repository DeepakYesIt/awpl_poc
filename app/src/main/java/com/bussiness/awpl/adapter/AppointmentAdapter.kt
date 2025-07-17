package com.bussiness.awpl.adapter

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.appsflyer.share.LinkGenerator
import com.appsflyer.share.ShareInviteHelper
import com.bumptech.glide.Glide
import com.bussiness.awpl.databinding.ItemAppointmentBinding
import com.bussiness.awpl.model.UpcomingModel
import com.bussiness.awpl.utils.AppConstant
import com.bussiness.awpl.utils.SessionManager
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AppointmentAdapter(
    private var appointmentList: MutableList<UpcomingModel>,
    private val onCancelClick: (UpcomingModel) -> Unit,
    private val onRescheduleClick: (UpcomingModel) -> Unit,
    private val onInfoClick: (UpcomingModel, View) -> Unit,
    private val startAppoitmentClick :(UpcomingModel) ->Unit,
    private val context :Context
    ) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(private val binding: ItemAppointmentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: UpcomingModel) {
            binding.apply {
                doctorName.text = appointment.doctorName
                dateTxt.text = appointment.date
                timeTxt.text = appointment.time

                if(appointment.is_referred){
                    referedPatient.visibility =View.VISIBLE
                    shareIcon.visibility =View.VISIBLE
                    referedPatient.setText("Referred: "+""+appointment.patientName)
                }else{
                    referedPatient.visibility =View.GONE
                    shareIcon.visibility =View.GONE
                }

                 if(appointment.is_free_call){

                     tagFreeText.visibility =View.VISIBLE
                }
                else{
                     tagFreeText.visibility =View.GONE
                }

              //  doctorImage.setImageResource(appointment.doctorImage)
                if(isCurrentTimeInRange(appointment.time)){
                    twoBtn.visibility=View.GONE
                    startAppointmentBtn.visibility =View.VISIBLE
                }else{
                    twoBtn.visibility =View.VISIBLE
                    startAppointmentBtn.visibility =View.GONE
                }
                Log.d("TESTING_UPCOMING","name :- "+ appointment.doctorName+" Date:- "+ appointment.date+" Time:- "+appointment.time)
                Glide.with(binding.root.context).load(AppConstant.Base_URL+appointment.doctorImage).into(doctorImage)

                cancelButton.setOnClickListener { onCancelClick(appointment) }
                rescheduleButton.setOnClickListener { onRescheduleClick(appointment) }
                infoIcon.setOnClickListener { onInfoClick(appointment, infoIcon) }

                shareIcon.setOnClickListener { generateDeepLink(appointment.id.toString(),appointment.doctorName,
                    appointment.time,
                    appointment.date) }
                startAppointmentBtn.setOnClickListener { startAppoitmentClick(appointment) }
                // Hide buttons if cancelled
//                if (appointment.isCancelled) {
//                    cancelButton.visibility = View.GONE
//                    rescheduleButton.visibility = View.GONE
//                } else {
//                    cancelButton.visibility = View.VISIBLE
//                    rescheduleButton.visibility = View.VISIBLE
//                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(appointmentList[position])
    }

    override fun getItemCount(): Int = appointmentList.size

     fun updateAdapter(appointmentList: MutableList<UpcomingModel>){
         this.appointmentList =appointmentList
         notifyDataSetChanged()
     }

    fun isCurrentTimeInRange(timeRange: String): Boolean {
        try {
            // Clean up and normalize the time range
            val cleaned = timeRange.trim().replace("\\s+".toRegex(), "")
            // Example cleaned: "02:15-02:30PM"

            val amPm = cleaned.takeLast(2) // "AM" or "PM"
            val times = cleaned.removeSuffix(amPm).split("-")
            if (times.size != 2) return false

            val startTimeStr = times[0] + " " + amPm // "02:15 PM"
            val endTimeStr = times[1] + " " + amPm   // "02:30 PM"

            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val now = Calendar.getInstance()
            val currentTime = formatter.format(now.time)

            val current = formatter.parse(currentTime)
            val start = formatter.parse(startTimeStr)
            val end = formatter.parse(endTimeStr)

            return current != null && start != null && end != null &&
                    current >= start && current <= end

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun generateDeepLink(appointmentId:String,doctorName :String,
                                 time:String,date :String) {
        val currentCampaign = "videocall"
        val oneLinkId = "pmck"
        val brandDomain = "awpluser.onelink.me"

        val deepLinkBase = "awpluser://videocall"
        val webFallbackBase = "https://zyvo.tgastaging.com"
        val doctorNameSlot = URLEncoder.encode(doctorName, "UTF-8")
        val dateSlot = URLEncoder.encode(date,"UTF-8")
        val token = SessionManager(context).getAuthToken()

        val tokenencode =  Base64.encodeToString(token?.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
        Log.d("TEST_TIMING","tOKEN Decoded IS "+tokenencode)

        val deepLink = "$deepLinkBase"
        val webLink = "$webFallbackBase"
        Log.d("TESTING_TIME",time)


        val encodedSlot = URLEncoder.encode(time, "UTF-8")

        val linkGenerator = ShareInviteHelper.generateInviteUrl(context)
            .setBaseDeeplink("https://$brandDomain/$oneLinkId")
            .setCampaign(currentCampaign)
            .addParameter("af_dp", deepLink)
            .addParameter("af_web_dp", webLink)
            // Optional: extra params for analytics or display
            .addParameter("appointmentId", appointmentId)
            .addParameter("token", tokenencode)
            .addParameter("doctor",doctorNameSlot)
            .addParameter("time",encodedSlot)
            .addParameter("date",dateSlot)

        linkGenerator.generateLink(context, object : LinkGenerator.ResponseListener {
            override fun onResponse(s: String) {
                Log.d("AppsFlyer", "Generated Link: $s")
                val message = "Join Call at: $s"
                shareLink(message) // or shareLinkWithImage(...)
            }

            override fun onResponseError(s: String) {
                Log.e("AppsFlyer", "Link generation failed: $s")
            }
        })


    }

    private fun shareLink(message: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share via")
        context.startActivity(shareIntent)
    }




}
